#!/bin/sh

set -eux

# ここで指定している.npmディレクトリはGitLab CI/CDによってキャッシュされる
cat <<_EOF_>frontend/.npmrc
cache=$(pwd)/.npm
prefer-offline=true
_EOF_

cd frontend

npm ci

npm run build

