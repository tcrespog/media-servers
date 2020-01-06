* Build configured Icecast.
```shell script
docker build -t icecast . && \
docker tag icecast cloud.canister.io:5000/tcrespo12/icecast:latest && \
docker push cloud.canister.io:5000/tcrespo12/icecast:latest
```