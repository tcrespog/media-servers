* Generate NodePort services definition files.
```bash
./kms-service-webrtc-generator.sh
```
* Create all elements.
```bash
kubectl create -R -f .
```
* Delete all elements.
```bash
kubectl delete pod kms-cloud
kubectl delete svc kms-cloud-web
kubectl delete svc -l nodePort=webrtc
rm -rf kms-service-webrtc/
```