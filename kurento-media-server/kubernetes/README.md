* Generate NodePort services definition files.
```shell script
./kms-service-webrtc-generator.sh
```
* Create all elements.
```shell script
kubectl create -R -f .
```
* Delete all elements.
```shell script
kubectl delete pod kms-cloud
kubectl delete svc kms-cloud-web
kubectl delete svc -l nodePort=webrtc
rm -rf kms-service-webrtc/
```