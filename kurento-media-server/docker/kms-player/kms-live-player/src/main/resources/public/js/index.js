/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

var ws = new WebSocket('ws://' + location.host + '/live');
var video;
var webRtcPeer;

var startViewingTimestamp;
var startupDelay;
var stalls = 0;
var droppedFrames = 0;
var droppedFramesPeriodicChecker;


window.onload = function() {
	toggleTestSession('start');

	video = document.getElementById('video');
	video.addEventListener('playing', function () {
		startupDelay = Date.now() - startViewingTimestamp;
		sendPlaybackMetrics();

		startDroppedFramesPeriodicChecker();

		console.log("Video playing");
	}, false);
	["stalled", "waiting"].forEach(function (e) {
		video.addEventListener(e, function () {
			stalls++;
			sendPlaybackMetrics();

			console.log("Video: " + e)
		}, false);
	});
};

window.onbeforeunload = function() {
	ws.close();
	dispose();
};

ws.onopen = function() {
	document.getElementById("play").disabled = false;
};

ws.onclose = function () {
	console.log('Playback ended');
};

function startViewing() {
	startViewingTimestamp = Date.now();
	viewer();
	document.getElementById("play").disabled = true;
}

function viewer() {
	if (webRtcPeer) {
		return;
	}

	webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
		{ remoteVideo: video,
		  onicecandidate: onIceCandidate
		},
		function(error) {
			if (error) {
				return console.error(error);
			}
			this.generateOffer(onOfferViewer);
		}
	);
}

ws.onmessage = function(message) {
	var parsedMessage = JSON.parse(message.data);

	switch (parsedMessage.id) {
		case 'viewerResponse':
			console.info('Received viewer response');
			viewerResponse(parsedMessage);
			break;
		case 'iceCandidate':
			console.info('Received ICE candidate', message.data);
			webRtcPeer.addIceCandidate(parsedMessage.candidate, function(error) {
				if (error)
					return console.error('Error adding candidate: ' + error);
			});
			break;
		case 'playEnd':
			console.info('Received play end', message.data);
			dispose();
			break;
		default:
			console.error('Unrecognized message', parsedMessage);
	}
};

function viewerResponse(message) {
	webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
		if (error) return console.error(error);
		console.log("Viewer response processed");
	});
}

function onOfferViewer(error, offerSdp) {
	if (error)
		return console.error('Error generating the offer');

	console.info('Invoking SDP offer callback function ' + location.host);
	var message = { id : 'viewer', sdpOffer : offerSdp };
	console.log('Sending viewer offer');
	sendMessage(message);
}

function onIceCandidate(candidate) {
	var message = { id : 'onIceCandidate', candidate : candidate };
	console.log('Sending ICE candidate', JSON.stringify(message));
	sendMessage(message);
}

function sendMessage(message) {
	var jsonMessage = JSON.stringify(message);
	ws.send(jsonMessage);
}

function dispose() {
	toggleTestSession('end');
	if (!webRtcPeer) {
		return;
	}

	getDroppedFrames();
	clearInterval(droppedFramesPeriodicChecker);

	webRtcPeer.dispose();
	webRtcPeer = null;
}

function getPlayerId() {
	return $('#playerId').val();
}

function toggleTestSession(action) {
	console.log(action + ' test session');
	$.ajax({
		url: $('#metricsServerUrl').val() + '/test/' + action,
		method: 'GET',
	});
}

function sendPlaybackMetrics() {
	var metrics = {
		playerId: getPlayerId(),
		startupDelay: startupDelay,
		stalls: stalls,
		droppedFrames: droppedFrames
	};

	console.log('Sending metrics', metrics);
	$.ajax({
		url: $('#metricsServerUrl').val() + '/playback/stats',
		method: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(metrics),
		error: function (error) {
			console.error(error);
		},
		success: function (success) {
			console.log(success);
		}
	});
}

function startDroppedFramesPeriodicChecker() {
	droppedFramesPeriodicChecker = setInterval(function () {
		getDroppedFrames();
	}, 30000)
}

function getDroppedFrames() {
	webRtcPeer.peerConnection.getStats().then(function (stats) {
		stats.forEach(function (value) {
			if (value.type === 'track' && value.kind === 'video') {
				console.log('Video stats', value);
				droppedFrames = value.framesDropped;
				sendPlaybackMetrics()
			}
		})
	});
}