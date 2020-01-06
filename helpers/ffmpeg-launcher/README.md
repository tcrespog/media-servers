* Build FFMPEG Launcher
```shell script
./gradlew build && \
docker build -t ffmpeg-launcher . && \
docker tag ffmpeg-launcher cloud.canister.io:5000/tcrespo12/ffmpeg-launcher:latest && \
docker push cloud.canister.io:5000/tcrespo12/ffmpeg-launcher:latest
```
