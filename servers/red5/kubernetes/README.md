* Create all elements.
```shell script
kubectl create -R -f .
```
* Delete all elements.
```shell script
kubectl delete pod red5-cloud && \
kubectl delete svc red5-cloud-http && \
kubectl delete svc red5-cloud-rtmp && \
kubectl delete svc red5-cloud-launcher
```
* Initiate stream.
```shell script
curl -X GET -G "http://localhost:8080/emit/" \
 -d resolution=480p \
 -d frameRate=24fps \
 -d codec=H264 \
 -d output=flv \
 -d url=rtmp://localhost:1935/live/stream
```