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
 -d frameRate=24fps \
 -d codec=VP8 \
 -d url=icecast://source:hackme@localhost:8000/stream.webm
```