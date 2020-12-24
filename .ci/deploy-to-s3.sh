#!/bin/sh
# プロダクションビルドしたfrontendの資材をDelivery環境のS3バケットに配置する

set -eux

# zip化するためのzipコマンドをインストールする
apk --no-cache add zip

# ビルドしたfrontendの資材を/tmp配下にコピーしてzip化する
cd frontend/build
zip -r $ARCHIVE_FILE_NAME ./*

# awsコマンドでS3バケットへPUSH
aws s3api put-object --bucket $PUSH_TO_S3_BUCKET_NAME --key $ARCHIVE_FILE_NAME --body ./$ARCHIVE_FILE_NAME
