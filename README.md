# SDUOJ Server

This is the service server of the SDUOJ.

## Solution Stack

*   Spring Boot
*   Spring Cloud
*   MyBatis
*   MyBatis-Plus
*   Lombok
*   MapStruct

## Run

* pull image: 
```sh
docker pull registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server
```

* Run it (take the `gateway` as an example):
```sh
docker run -di \
  --name=sduoj-gateway \
  --publish=8080:8080 \
  --pids-limit=1024 \
  --cpu-shares=100 \
  --memory=2048M \
  --memory-swap=2048M \
  -e NACOS_ADDR=127.0.0.1:8848 \
  -e ACTIVE=prod \
  -e SERVICE=gateway \
  registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server
```

* Docker params:
    * `--publish`: the container's port to the host
    * `--pids-limit`: the limit of process in container
    * `--cpu-shares`: the relative weight of cpu
    * `NACOS_ADDR`: the host of Nacos
    * `ACTIVE`: `dev` or `prod`
    * `SERVICE`: `gateway`, `auth`, `user`, `problem`, `filesys`, `submit`, `contest` or `websocker`
    
* Run all micro-services at once:
```sh
docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-auth      -e SERVICE=auth      --publish=19010:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-contest   -e SERVICE=contest   --publish=19011:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-filesys   -e SERVICE=filesys   --publish=19012:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-gateway   -e SERVICE=gateway   --publish=8080:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-problem   -e SERVICE=problem   --publish=19013:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-submit    -e SERVICE=submit    --publish=19014:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-user      -e SERVICE=user      --publish=19015:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server

docker run -di --pids-limit=1024 --cpu-shares=100 --memory=2048M --memory-swap=2048M \
  --name=sduoj-websocket -e SERVICE=websocket --publish=19016:8080 \
  -e NACOS_ADDR=127.0.0.1:8848 -e ACTIVE=prod registry.cn-beijing.aliyuncs.com/sduoj/sduoj-server
```