FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim

ENV RED5_VERSION 1.2.3
ENV RED5_FILE red5-server-${RED5_VERSION}.tar.gz
ENV RED5_URL https://github.com/Red5/red5-server/releases/download/v${RED5_VERSION}/${RED5_FILE}
ENV RED5_HOME /usr/share/red5

#Installation: https://github.com/Red5/red5-server/wiki/Installation-on-Linux
RUN mkdir -p ${RED5_HOME}

WORKDIR ${RED5_HOME}

RUN wget -O red5.tar.gz ${RED5_URL}
RUN tar -zxvf red5.tar.gz --strip-components 1
RUN sed -i -e '1s/bash/sh/' red5.sh

#Ports: https://github.com/Red5/red5-server/blob/master/src/main/server/conf/red5.properties
#HTTP, HTTPS
EXPOSE 5080 5443
#WebSocket
EXPOSE 843
#RTMP, RTMPS, RTMPT
EXPOSE 1935 8443 8088

ENTRYPOINT ["./red5.sh"]