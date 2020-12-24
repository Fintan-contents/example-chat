#!/bin/sh
set -eux

# https://hub.docker.com/_/docker 等、alpine ベースの Container に対する AWS CLI v2 のインストールには glibc が必要
# see: https://stackoverflow.com/questions/60298619/awscli-version-2-on-alpine-linux/61268529#61268529

GLIBC_VER=2.31-r0

# install glibc
apk --no-cache add binutils curl
curl -sL https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub -o /etc/apk/keys/sgerrand.rsa.pub
curl -sLO https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VER}/glibc-${GLIBC_VER}.apk
curl -sLO https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VER}/glibc-bin-${GLIBC_VER}.apk
apk add --no-cache glibc-${GLIBC_VER}.apk glibc-bin-${GLIBC_VER}.apk

# install awscliv2
curl -sL https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip
unzip -q awscliv2.zip
aws/install
