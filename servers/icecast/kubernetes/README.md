* Create all elements.
```shell script
kubectl create -R -f .
```
* Delete all elements.
```shell script
kubectl delete pod icecast-cloud && \
kubectl delete svc icecast-cloud-launcher && \
kubectl delete svc icecast-cloud-stream
```
* Initiate stream.
```shell script
curl -X GET -G "http://localhost:8080/emit/" \
 -d resolution=360p \
 -d frameRate=30fps \
 -d codec=VP8 \
 -d url=icecast://source:hackme@192.168.1.38:8000/video.webm
```