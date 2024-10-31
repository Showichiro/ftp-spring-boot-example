# ftp-spring-boot-example

## FTPサーバ構築

### 前提

Ubuntuを利用しての構築なので、AmazonLinuxなどの場合はインストールコマンドなどが異なることに留意.

dockerの環境構築.

https://docs.docker.com/engine/install/ubuntu/

などを参考に.

### FTPサーバを起動

以下のコマンドを実行して、Docker Containerとして[FTPサーバ](https://wiki.archlinux.jp/index.php/Pure-FTPd)を起動.

```
cd ftp-server
docker compose up -d
```

以下のようにイメージを落としてきて、起動すればOK.

```
$ docker compose up -d
[+] Running 11/11
 ✔ ftp_server Pulled                                                                                                                                                                                         7.7s 
   ✔ b4d181a07f80 Pull complete                                                                                                                                                                              4.0s 
   ✔ e98cb485cfd8 Pull complete                                                                                                                                                                              4.9s 
   ✔ 0822c77e0e0b Pull complete                                                                                                                                                                              4.9s 
   ✔ 8588bb5b4480 Pull complete                                                                                                                                                                              5.0s 
   ✔ a41cb6218cc9 Pull complete                                                                                                                                                                              5.0s 
   ✔ 7ac94abef10b Pull complete                                                                                                                                                                              5.0s 
   ✔ 8d5dc14fad00 Pull complete                                                                                                                                                                              5.0s 
   ✔ 417d6be4b5ec Pull complete                                                                                                                                                                              5.0s 
   ✔ 71cd100c9ec0 Pull complete                                                                                                                                                                              5.1s 
   ✔ 227193214c34 Pull complete                                                                                                                                                                              5.1s 
[+] Running 2/2
 ✔ Network ftp-server_default  Created                                                                                                                                                                       0.1s 
 ✔ Container ftp-server        Started 
```

接続情報などは[docker-compose.yaml](./ftp-server/docker-compose.yaml)を参照のこと.

起動しているプロセスの確認

```
$ docker ps
```

```
$ docker ps
CONTAINER ID   IMAGE                 COMMAND                  CREATED         STATUS         PORTS                                                                                                      NAMES
51ace1bb9ed1   stilliard/pure-ftpd   "/bin/sh -c '/run.sh…"   6 minutes ago   Up 6 minutes   0.0.0.0:21->21/tcp, :::21->21/tcp, 0.0.0.0:30000-30009->30000-30009/tcp, :::30000-30009->30000-30009/tcp   ftp-server
```

### CLIからFTPサーバに接続

lftpというCLIのFTPクライアントツールをインストールする.

```
$ sudo apt update
$ sudo apt install lftp
```

#### ローカルからFTPサーバへのファイルアップロード

```
$ lftp  # lftpのターミナルが開く
lftp:~>  open -u uname,uP@ssw0rd ftp://localhost/  # ログイン
lftp:~>  mkdir files  # FTPサーバにディレクトリ作成
lftp:~>  cd files  # 移動
lftp/files:~>  PUT ./sample.txt  # sample.txtをFTPサーバへアップロード
lftp/files:~>  exit
```

#### ファイルダウンロード


```
curl -u [username]:[password] -o [/path/to/download] [ftp://url/to/uploadedfile]
```

でダウンロードできるので、

```
curl -u uname:uP@ssw0rd -o ./sample-from-ftp.txt ftp://localhost/files/sample.txt
```

でダウンロード可能.


#### CLI経由でのファイルダウンロード


```
$ lftp
lftp :~> open -u uname,uP@ssw0rd ftp://localhost/
cd ok, cwd=/                                       
lftp uname@localhost:/> GET sample.txt 
8 bytes transferred
```

## アプリ経由でFTPサーバに接続

アプリはパッシブモードで接続している。設定は[FTPConfig.java](./app/demo/src/main/java/com/example/demo/config/FTPConfig.java)を参照。

### 前提

Java 17系を利用.
SDKMAN経由で環境構築している場合は、以下のコマンドで切り替え可能.

```
sdk env # .sdkmanrcに記載あるJavaバージョンに変更される.
```

FTPサーバをあげておくこと（上の手順でDocker Containerを起動しておく）

### アプリ起動

```
cd app/demo
./gradlew bootRun
```

#### ファイル作成

APIをコール.

```
curl --location 'http://localhost:8080/update-file?filePath=app-files.txt' \
--header 'Content-Type: text/plain' \
--data 'abcdefg'
```

lftpで確認すると、ファイルが作成されている.
```
$ lftp
lftp :~> open -u uname,uP@ssw0rd ftp://localhost
lftp uname@localhost:~> ls
-rw-r--r--    1 1000       ftpgroup            7 Oct 31 09:10 app-files.txt
```

#### ファイル取得

APIをコール.

```
curl --location 'http://localhost:8080/read-file?filePath=app-files.txt'
```

→レスポンスでファイルの内容が表示される.


#### ファイル削除

APIをコール.

```
curl --location --request DELETE 'http://localhost:8080/delete-file?filePath=app-files.txt'
```

lftpで確認するとファイルが削除されている.

```
$ lftp
lftp :~> open -u uname,uP@ssw0rd ftp://localhost
lftp uname@localhost:~> ls
```

## 参考文献

- DockerでのFTPサーバ構築

https://qiita.com/middle_aged_rookie_programmer/items/3ed0efeb123220d9bdef


- Spring BootでのFTPサーバ接続に関するAIとの対話の記録

https://gist.github.com/Showichiro/56797e7ad5a2c7a00fcae2fac49c72de