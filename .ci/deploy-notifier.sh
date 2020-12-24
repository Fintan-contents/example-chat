#!/bin/sh

set -eux

mvn -f notifier/pom.xml \
	package jib:build \
	-Dmaven.test.skip=true \
	-Dmaven.repo.local=$(pwd)/.m2/repository \
	-Djib.to.image=$CI_REGISTRY_IMAGE/example-chat-notifier \
	-Djib.to.tags=$CI_COMMIT_REF_NAME \
	-Djib.to.auth.username=$CI_REGISTRY_USER \
	-Djib.to.auth.password=$CI_REGISTRY_PASSWORD
