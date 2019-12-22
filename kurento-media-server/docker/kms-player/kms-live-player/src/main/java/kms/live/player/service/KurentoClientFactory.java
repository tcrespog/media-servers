package kms.live.player.service;

import io.micronaut.context.annotation.Factory;
import org.kurento.client.KurentoClient;

import javax.inject.Singleton;

@Factory
public class KurentoClientFactory {

    @Singleton
    KurentoClient kurentoClient() {
        return KurentoClient.create();
    }

}
