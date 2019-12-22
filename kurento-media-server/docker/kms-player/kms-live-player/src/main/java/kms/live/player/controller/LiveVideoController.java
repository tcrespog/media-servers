package kms.live.player.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.micronaut.http.MediaType;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

@ServerWebSocket("/live")
public class LiveVideoController {

    private static final Logger log = LoggerFactory.getLogger(LiveVideoController.class);
    private static final Gson gson = new Gson();

    private final ConcurrentHashMap<String, WebRtcEndpoint> viewers;

    @Inject
    private KurentoClient kurentoClient;

    private MediaPipeline mediaPipeline;
    private PlayerEndpoint playerEndpoint;

    public LiveVideoController() {
        viewers = new ConcurrentHashMap<>();
    }

    @OnOpen
    public void onOpen(WebSocketSession session) {
        if (mediaPipeline != null) {
            return;
        }

        startMediaPlayback(session);
    }

    private void startMediaPlayback(WebSocketSession session) {
        mediaPipeline = kurentoClient.createMediaPipeline();

        playerEndpoint = new PlayerEndpoint.Builder(mediaPipeline, "file:///home/BBB_480p_24fps_H264_128kbit_AAC.mp4").build();
        playerEndpoint.addErrorListener(event -> {
            log.info("ErrorEvent: {} ", event.getDescription());
            finishMediaPlayback(session);
        });
        playerEndpoint.addEndOfStreamListener(event -> {
            log.info("EndOfStreamEvent: {} ", event.getTimestamp());
            finishMediaPlayback(session);
        });
        playerEndpoint.play();
    }

    private void finishMediaPlayback(WebSocketSession session) {
        mediaPipeline = null;
        playerEndpoint = null;
        viewers.clear();

        JsonObject response = new JsonObject();
        response.addProperty("id", "playEnd");
        synchronized (session) {
            session.sendSync(response.toString(), MediaType.APPLICATION_JSON_TYPE);
            session.close();
        }
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        log.info("Incoming message from session '{}': {}", session.getId(), jsonMessage);

        String messageId = jsonMessage.get("id").getAsString();
        if (messageId.equals("viewer")) {
            createViewerEndpoint(session, jsonMessage);
        } else if (messageId.equals("onIceCandidate")) {
            onIceCandidate(session, jsonMessage);
        }
    }

    private void createViewerEndpoint(WebSocketSession session, JsonObject jsonMessage) {
        WebRtcEndpoint viewerWebRtcEndpoint = new WebRtcEndpoint.Builder(mediaPipeline).build();
        viewers.put(session.getId(), viewerWebRtcEndpoint);

        viewerWebRtcEndpoint.addIceCandidateFoundListener(event -> {
            JsonObject candidateResponse = new JsonObject();
            candidateResponse.addProperty("id", "iceCandidate");
            candidateResponse.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
            synchronized (session) {
                log.info("Sending ICE candidate {}", candidateResponse.toString());
                session.sendSync(candidateResponse.toString(), MediaType.APPLICATION_JSON_TYPE);
            }
        });

        String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
        String sdpAnswer = viewerWebRtcEndpoint.processOffer(sdpOffer);

        playerEndpoint.connect(viewerWebRtcEndpoint);

        JsonObject viewerResponse = new JsonObject();
        viewerResponse.addProperty("id", "viewerResponse");
        viewerResponse.addProperty("response", "accepted");
        viewerResponse.addProperty("sdpAnswer", sdpAnswer);
        synchronized (session) {
            session.sendSync(viewerResponse.toString(), MediaType.APPLICATION_JSON_TYPE);
        }

        viewerWebRtcEndpoint.gatherCandidates();
    }

    private void onIceCandidate(WebSocketSession session, JsonObject jsonMessage) {
        WebRtcEndpoint endpoint = viewers.get(session.getId());
        JsonObject candidateJson = jsonMessage.get("candidate").getAsJsonObject();
        IceCandidate candidate = new IceCandidate(
                candidateJson.get("candidate").getAsString(), candidateJson.get("sdpMid").getAsString(),
                candidateJson.get("sdpMLineIndex").getAsInt()
        );

        log.info("Received ICE candidate {}", candidateJson.toString());
        try {
            endpoint.addIceCandidate(candidate);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
