#!/bin/sh

set -eux

# ここで指定している.m2ディレクトリはGitLab CI/CDによってキャッシュされる
mvn -f backend/pom.xml -e \
	verify \
	-DtrimStackTrace=false \
	-Dmaven.repo.local=$(pwd)/.m2/repository
