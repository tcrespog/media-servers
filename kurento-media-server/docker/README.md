* Build Kurento Media Server.
```shell script
cd kms
docker build -t kms .
docker tag kms cloud.canister.io:5000/tcrespo12/kms:latest
docker push cloud.canister.io:5000/tcrespo12/kms:latest
```
* Build web interface.
```shell script
cd kms-player/kms-live-player 
./gradlew build

cd ..
docker build -t kms-player .
docker tag kms-player cloud.canister.io:5000/tcrespo12/kms-player:latest
docker push cloud.canister.io:5000/tcrespo12/kms-player:latest 
```