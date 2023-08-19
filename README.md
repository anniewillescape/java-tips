# java-tips

自分用色々実装メモ

## how to start httpbin

1. use java-tips/docker-compose.yml

    ```bash
    docker compose up -d --build
    ```

2. use docker run command

    ```bash
    docker run --rm -p 80:80 kennethreitz/httpbin
    ```