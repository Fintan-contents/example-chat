# SPA + REST API構成のチャットサービス コード例

[SPA + REST API構成のサービス開発リファレンス](https://fintan.jp/?p=5952)が提供するコンテンツの1つです。
小さなチャットサービスを実装したコード例です。

## このコンテンツの使い方

SPAやREST APIを開発するときに参考にして頂いたり、必要に応じてソースコードを流用して再利用して頂くことを目的としています。

GitHub上でソースコードを見て頂いたり、`git clone`をしてローカルにソースコードを取得してテキストエディターやJavaのIDEで見て頂くことを想定しています。

トップレベルのファイル・ディレクトリの説明をします。

|ファイル・ディレクトリ|説明|
|---|---|
|`.ci`|CI用のスクリプトを格納しています|
|`.gitlab`|GitLab上でマージリクエストを作成するときのテンプレートです|
|`.gitlab-ci.yml`|GitLabでCI/CDするためのビルドパイプラインの設定ファイルです|
|`README.md`|このファイルです|
|`api-document`|REST APIのインターフェースを記述したOpenAPIドキュメントです|
|`backend`|REST APIのソースコードです|
|`docker`|Dockerを使用してローカルで動かすミドルウェアのための設定ファイルを格納しています|
|`docker-compose.yml`|ローカルで必要なミドルウェアを動かすためのDocker Compose設定ファイルです|
|`frontend`|SPAのソースコードです|
|`notifier`|WebSocketで通知を行うアプリケーションのソースコードです（※SPAでもなくREST APIでもありませんが、WebSocketによる通知の例として参考にして頂けます）|

本コンテンツはソースコードを参考にして頂くことは勿論ですが、GitLabでCI/CDを行う際に必要となるビルドパイプラインの設定なども参考にして頂くことも想定しています。

※本コンテンツはGitHubで公開していますが、開発自体はGitLabを用いて行っています

## 開発に必要なソフトウェア

本アプリケーションの開発には次のソフトウェアを使用しています。

- [Node v12.16](https://nodejs.org/ja/)
- [Java 11(AdoptOpenJDK HotSpot)](https://adoptopenjdk.net/)
- [Maven 3.6](https://maven.apache.org/)
- [Docker 19.03, Docker Compose 1.26](https://www.docker.com/)

ローカルで本アプリケーションを動かす場合はこれらソフトウェアのインストールをしてください。

また、必要に応じてテキストエディタやJavaのIDEをインストールしてください。

## 対象ブラウザ

本アプリケーションは[Google Chrome](https://www.google.com/intl/ja_jp/chrome/)を対象としています。
他の環境では動作しない可能性があります。

## ローカルでの起動方法・停止方法

ローカルで本アプリケーションを動かすには大きくわけて次の4つを起動する必要があります（括弧内はソースコードのディレクトリです）。

- データベースや外部サービスのモックなどのミドルウェア
- REST API（`backend`）
- WebSocketで通知を行うアプリケーション（`notifier`）
- SPA（`frontend`）

### データベースや外部サービスのモックなどのミドルウェア

本アプリケーションでは次のような外部サービスを利用しています。
※実際にサービスとして運用を行なっているわけではないので、AWSのマネージドサービスに関しては「実際にサービス運用をすると仮定した場合に利用するもの」と捉えてください

- [Amazon RDS for PostgreSQL](https://aws.amazon.com/jp/rds/postgresql/)
    - 永続化データの保持
    - ローカルでは[PostgreSQL](https://www.postgresql.jp/)で代替
- [Amazon ElastiCache for Redis](https://aws.amazon.com/jp/elasticache/redis/)
    - 認証前の一時的なユーザー情報の保持
    - 通知のためのキュー
    - ローカルでは[Redis](https://redis.io/)で代替
- [Amazon S3](https://aws.amazon.com/jp/s3/)
    - アップロードされた画像ファイルの保持
    - ダウンロードさせるためのチャットログの保持
    - ローカルでは[MinIO](https://min.io/)で代替
- [Amazon SES](https://aws.amazon.com/jp/ses/)
    - ユーザーへ連絡するためのメール送信
    - ローカルでは[MailHog](https://github.com/mailhog/MailHog)で代替

ローカル環境ではDockerおよびDocker Composeを利用することで、動作できるようにしています。

次のコマンドで起動してください。

```
$ docker-compose up -d
```

次のように`docker-compose ps`コマンドを実行して`State`が`Up`になっていれば起動成功です。

```
$ docker-compose ps
         Name                        Command               State                       Ports                     
-----------------------------------------------------------------------------------------------------------------
example-chat_mail_1       MailHog                          Up      0.0.0.0:1025->1025/tcp, 0.0.0.0:8025->8025/tcp
example-chat_minio_1      /usr/bin/docker-entrypoint ...   Up      0.0.0.0:9000->9000/tcp                        
example-chat_postgres_1   docker-entrypoint.sh postgres    Up      0.0.0.0:5432->5432/tcp                        
example-chat_redis_1      docker-entrypoint.sh redis ...   Up      0.0.0.0:6379->6379/tcp 
```

停止する場合は次のコマンドを実行してください。

```
$ docker-compose down
```

### REST API（backend）

REST APIをローカルで動かす場合、アプリケーションサーバとして[Jetty](https://www.eclipse.org/jetty/)を利用しています。

Mavenプラグインを利用しており、以下のようなコマンドを実行することで起動できます。  

```
$ # backendディレクトリで実行してください
$ mvn jetty:run
```

初回の実行時はMavenリポジトリから依存するJARファイルをダウンロードするため、多少時間がかかります。

例外が発生することなく次のようなログが出力されると起動成功です。

```
[INFO] Started ServerConnector@114a18d6{HTTP/1.1,[http/1.1]}{0.0.0.0:9080}
[INFO] Started Server@75689d9c{STARTING}[10.0.0.alpha1] @6271ms

Hit <enter> to redeploy:
```

停止する場合は`Ctrl + C`を押してください。

#### Dockerコンテナで起動する場合

REST APIをローカルで動かす場合、Dockerコンテナで起動することもできます。

まず、次のコマンドでDockerイメージを作成します。Dockerイメージの名前は`example-chat-backend`になります。

```
$ # backendディレクトリで実行してください
$ mvn package jib:dockerBuild -DskipTests
```

Dockerイメージが作成されているかどうかは、次のコマンドで確認することができます。

```
$ docker images
REPOSITORY                           TAG                 IMAGE ID            CREATED             SIZE
example-chat-backend                 1.0.0      662b55a12169        50 years ago        466MB
example-chat-backend                 latest              662b55a12169        50 years ago        466MB
```

Dockerイメージが作成されたら、次のコマンドでDockerコンテナを起動します。Dockerコンテナ内から他のミドルウェアへは`localhost`でアクセスできないため、環境変数でミドルウェアの接続先を設定します。

```
docker run -it --rm -p 9080:8080 --network example-chat-network -e NABLARCH_DB_URL="jdbc:postgresql://postgres:5432/postgres" -e NABLARCH_LETTUCE_SIMPLE_URI="redis://password@redis:6379" -e MAIL_SMTP_HOST="mail" -e AWS_S3_ENDPOINTOVERRIDE="http://minio:9000" example-chat-backend:latest
```

例外が発生することなく次のようなログが出力されると起動成功です。

```
04-Sep-2020 07:55:36.952 情報 [main] org.apache.catalina.startup.Catalina.start サーバーの起動 [5,184]ms
```

停止する場合は`Ctrl + C`を押してください。

### WebSocketで通知を行うアプリケーション（notifier）

WebSocketで通知を行うアプリケーションはREST APIと同様にローカルで動かす場合、アプリケーションサーバとして[Jetty](https://www.eclipse.org/jetty/)を利用しています。

次のコマンドで起動ができます。

```
$ # notifierディレクトリで実行してください
$ mvn jetty:run
```

例外が発生することなく次のようなログが出力されると起動成功です。

```
[INFO] Started ServerConnector@38eafdab{HTTP/1.1,[http/1.1]}{0.0.0.0:9081}
[INFO] Started Server@491cc0eb{STARTING}[10.0.0.alpha1] @4770ms

Hit <enter> to redeploy:
```

停止する場合は`Ctrl + C`を押してください。

#### 既知の問題

まれにアプリケーション起動後、通知を行うと次のようなエラーになる場合があります。

```
java.lang.NoClassDefFoundError: Could not initialize class com.example.presentation.websocket.WebSocketNotifier
```

このエラーが発生し、解消されない場合は後述するDockerコンテナでの起動を試してください。

#### Dockerコンテナで起動する場合

WebSocketで通知を行うアプリケーションをローカルで動かす場合、Dockerコンテナで起動することもできます。

まず、次のコマンドでDockerイメージを作成します。Dockerイメージの名前は`example-chat-notifer`になります。

```
$ # notifierディレクトリで実行してください
$ mvn package jib:dockerBuild -DskipTests
```

Dockerイメージが作成されているかどうかは、次のコマンドで確認することができます。

```
$ docker images
REPOSITORY                           TAG                 IMAGE ID            CREATED             SIZE
example-chat-notifier                1.0.0      d3d0a0844ffb        50 years ago        454MB
example-chat-notifier                latest              d3d0a0844ffb        50 years ago        454MB
```

Dockerイメージが作成されたら、次のコマンドでDockerコンテナを起動します。Dockerコンテナ内から他のミドルウェアへアクセスするため、環境変数でミドルウェアの接続先を設定します。次のコマンドでは、ホスト端末(実行端末)を表す`host.docker.internal`を使用して、`localhost`と同等の接続先に設定しています。

```
docker run -it --rm -p 9081:8080 --network example-chat-network -e NABLARCH_LETTUCE_SIMPLE_URI="redis://password@redis:6379" example-chat-notifier:latest
```

例外が発生することなく次のようなログが出力されると起動成功です。

```
04-Sep-2020 07:58:41.380 情報 [main] org.apache.catalina.startup.Catalina.start サーバーの起動 [3,314]ms
```

停止する場合は`Ctrl + C`を押してください。

### SPA（frontend）

SPAは`create-react-app`を使用して開発しており、[create-react-appの利用方法](https://github.com/facebook/create-react-app#npm-start-or-yarn-start)に則っています。

依存するライブラリをダウンロードするため、初回のみ次のコマンドを実行してください。

```
$ # frontendディレクトリで実行してください
$ npm ci
```

起動するには以下のように`npm start`を実行します。

```
$ # frontendディレクトリで実行してください
$ npm start
```

次のように出力されると起動成功です。

```
Compiled successfully!

You can now view example-chat-frontend in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://192.168.0.19:3000

Note that the development build is not optimized.
To create a production build, use npm run build.
```

更に`npm start`は自動でブラウザを起動して [http://localhost:3000](http://localhost:3000) を開きます。

停止する場合は`Ctrl + C`を押してください。

## 動作確認をする

アプリケーションを触ってみてください。

本アプリケーションにはログイン認証機能があります。
あらかじめ用意しているユーザーの情報は次の通りです。

|ユーザー名|メールアドレス|パスワード|
|---|---|---|
|`user1`|`user1@example.com`|`pass123-`|
|`user2`|`user2@example.com`|`pass123-`|

新しくユーザーを登録することも可能です。
ユーザー登録にはメールを使いますが、本アプリケーションをローカルで動かしている場合はメールサーバーのモックを使用します。
本アプリケーション内で送信されたメールを読む場合は、ブラウザで [http://localhost:8025](http://localhost:8025) を開いてください。

### Docker Composeで起動しているミドルウェアへの接続方法

Docker Composeで起動しているミドルウェアへ接続するための情報は`docker-compose.yml`および`docker`ディレクトリ内のファイルに記載してあります。

PostgreSQLとRedisはDockerコンテナ内にCUIのクライアントツールが同梱されています。

PostgreSQLは次のコマンドで接続できます。

```
$ docker-compose exec postgres psql -U postgres
```

Redisは次のコマンドで接続した後に[AUTHコマンド](https://redis.io/commands/auth)でパスワードを入力して認証を行なってください。

```
$ docker-compose exec redis redis-cli
```

MinIOとMailHogはDockerコンテナ内にGUIのクライアントツールが同梱されています。
いずれもウェブアプリケーションです。

MinIOのクライアントツールを使うにはブラウザで [http://localhost:9000](http://localhost:9000) を開いてください。

MailHogのクライアントツールを使うにはブラウザで [http://localhost:8025](http://localhost:8025) を開いてください。

## その他

### 静的解析

#### REST API（backend）・WebSocketで通知を行うアプリケーション（notifier）

REST APIとWebSocketで通知を行うアプリケーションは[Nablarchが提供しているJavaスタイルガイド](https://github.com/nablarch-development-standards/nablarch-style-guide/tree/2.0/java)を参考にして静的解析を組み込んでいます。

[SpotBugs](https://spotbugs.readthedocs.io/ja/latest/)による静的解析をMavenで実行できます。
次のコマンドでチェックできます。

```
$ # backendディレクトリ、またはnotifierディレクトリで実行してください
$ mvn spotbugs:check
```

#### SPA（frontend）

SPAは[ESLint](https://eslint.org/)による静的チェックが行えるようにしています。

次のコマンドでチェックできます。

```
$ # frontendディレクトリで実行してください
$ npm run lint
```

内容によっては機械的に修正が可能なものもあります。
そういったものは、次のコマンドで修正することが可能です。

```
$ # frontendディレクトリで実行してください
$ npm run lint:fix
```

### テストの実行

#### REST API（backend）・WebSocketで通知を行うアプリケーション（notifier）

JUnitを使ったテストコードを用意しています。

次のコマンドでテストを実行できます。

```
$ # backendディレクトリ、またはnotifierディレクトリで実行してください
$ mvn test
```

`mvn test`で実行されるものはミドルウェアを使用しない箇所に対するテストになります。

ミドルウェアを使用する箇所に対するテストは次のコマンドで実行できます。なお、ミドルウェアを使用するため、事前にDocker Composeでミドルウェアを起動しておく必要があります。

```
$ # backendディレクトリ、またはnotifierディレクトリで実行してください
$ mvn verify
```

#### SPA（frontend）

次のコマンドでテストを実行できます。

```
$ # frontendディレクトリで実行してください
$ npm test
```

---

※ このリポジトリは[Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)の下に提供されています。

※ このドキュメントに記載されている会社名、製品名は、各社の登録商標または商標です。

※ AWS、Amazon RDS for PostgreSQL、Amazon ElastiCache for Redis、Amazon S3、Amazon SESは、米国および/またはその他の諸国における、Amazon.com, Inc.またはその関連会社の商標です。
