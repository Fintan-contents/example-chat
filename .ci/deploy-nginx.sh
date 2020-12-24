#!/bin/sh

set -eux

# タグにはブランチ名を付与する
# ただし、hoge/fuga/piyo というような '/' 区切りのブランチ名については、'/' で区切られた最後の
# 単語をブランチ名とする
TAG=${CI_COMMIT_REF_NAME##*/}

# backend 用の nginx リバースプロキシの構築
docker image build \
    -t $CI_REGISTRY_IMAGE/nginx-backend:${TAG} \
    -f docker/nginx/Dockerfile.backend \
    docker/nginx
docker image push $CI_REGISTRY_IMAGE/nginx-backend:${TAG}

# notifier 用の nginx リバースプロキシの構築
docker image build \
    -t $CI_REGISTRY_IMAGE/nginx-notifier:${TAG} \
    -f docker/nginx/Dockerfile.notifier \
    docker/nginx
docker image push $CI_REGISTRY_IMAGE/nginx-notifier:${TAG}
