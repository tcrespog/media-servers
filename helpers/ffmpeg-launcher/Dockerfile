FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
COPY build/libs/ffmpeg-launcher-*-all.jar ffmpeg-launcher.jar

RUN apk add --no-cache ffmpeg
COPY /videos /videos

EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar ffmpeg-launcher.jar