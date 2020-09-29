#!/bin/sh

set -eux

mvn -f backend/pom.xml \
	package jib:build \
	-Dmaven.test.skip=true \
	-Dmaven.repo.local=$(pwd)/.m2/repository \
	-Djib.to.image=$CI_REGISTRY_IMAGE/example-chat-backend \
	-Djib.to.auth.username=$CI_REGISTRY_USER \
	-Djib.to.auth.password=$CI_REGISTRY_PASSWORD
