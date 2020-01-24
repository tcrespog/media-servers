* Create all elements.
```shell script
kubectl create -R -f .
```
* Delete all elements.
```shell script
kubectl delete pod vlc-cloud && \
kubectl delete svc vlc-cloud-launcher && \
kubectl delete svc vlc-cloud-stream
```
* Initiate stream.
```shell script
curl -X GET -G "http://localhost:8080/emit/" \
 -d resolution=360p \
 -d frameRate=24fps \
 -d codec=H264
```