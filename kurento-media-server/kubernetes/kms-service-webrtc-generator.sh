#!/bin/bash

mkdir kms-service-webrtc
for i in {1..100}
do
  PORT=$((30000 + i))
  CONTENT=$(cat <<-END > kms-service-webrtc/kms-service-webrtc${i}.yml
apiVersion: v1
kind: Service
metadata:
  name: kms-cloud-webrtc-${i}
  labels:
    nodePort: webrtc
spec:
  type: NodePort
  ports:
    - name: webrtc
      port: $PORT
      targetPort: $PORT
      nodePort: $PORT
  selector:
    app: kms
END
)
done

