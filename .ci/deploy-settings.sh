#!/bin/sh
# CodeDeploy用設定をS3にアップロードする

set -eux

yum install -y zip
zip --junk-paths $DEPLOYMENT_SETTING_NAME $SETTING_DIR/{appspec.yaml,taskdef.json}
aws s3 cp $DEPLOYMENT_SETTING_NAME s3://$S3_BUCKET_NAME
