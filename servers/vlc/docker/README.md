* Build VLC launcher.
```shell script
cd vlc-launcher && \
./gradlew build && \
cd .. && \
docker build -t vlc-launcher . && \
docker tag vlc-launcher cloud.canister.io:5000/tcrespo12/vlc-launcher:latest && \
docker push cloud.canister.io:5000/tcrespo12/vlc-launcher:latest
```
* Initiate stream
```shell script
curl -X GET -G "http://localhost:8080/emit/" \
 -d resolution=360p \
 -d frameRate=24fps \
 -d codec=H264
```