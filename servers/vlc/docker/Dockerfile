FROM adoptopenjdk/openjdk11-openj9:x86_64-ubuntu-jdk-11.0.4_11_openj9-0.15.1-slim

# Install VLC
RUN apt-get update && apt-get install -y vlc
# Add user to run as
RUN useradd -M vlc

USER vlc
COPY vlc-launcher/videos /videos
COPY vlc-launcher/build/libs/vlc-launcher-*-all.jar vlc-launcher.jar

EXPOSE 8080 8554
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar vlc-launcher.jar