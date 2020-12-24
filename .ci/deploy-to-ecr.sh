#!/bin/sh
# GitLab Container Repository 上の Image ($IMAGE_PUSHED_TO_ECR) にタグ付けした上で ECR に PUSH する

set -eux

# GitLab Container Registry から、branch に対応する Image を Pull する
docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
docker image pull $IMAGE_PUSHED_TO_ECR

# ECR 用のタグ付け
docker image tag $IMAGE_PUSHED_TO_ECR $ECR_REPOSITORY:$CI_COMMIT_REF_NAME
docker image tag $IMAGE_PUSHED_TO_ECR $ECR_REPOSITORY:$CI_COMMIT_SHORT_SHA

# master branch への merge の場合は latest タグを付与する
if [ "$CI_COMMIT_REF_NAME" == "master" ]; then
    docker image tag $IMAGE_PUSHED_TO_ECR $ECR_REPOSITORY:latest
fi
aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_REPOSITORY
docker image push $ECR_REPOSITORY
