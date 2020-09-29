#!/bin/sh

set -eux

export IMAGE_BASE=$CI_REGISTRY_IMAGE/example-chat-frontend
export IMAGE_LATEST=$IMAGE_BASE:latest

cd frontend

docker pull $IMAGE_LATEST || true

docker build --cache-from $IMAGE_LATEST --tag $IMAGE_LATEST .

docker push $IMAGE_LATEST
