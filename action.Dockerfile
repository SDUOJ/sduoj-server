FROM openjdk:8-jre
MAINTAINER SDUOJ-Team

ENV LANG C.UTF-8

COPY                        sduoj-gateway/build/libs/ /sduoj/
COPY                      sduoj-websocket/build/libs/ /sduoj/
COPY        sduoj-auth/sduoj-auth-service/build/libs/ /sduoj/
COPY  sduoj-contest/sduoj-contest-service/build/libs/ /sduoj/
COPY  sduoj-filesys/sduoj-filesys-service/build/libs/ /sduoj/
COPY  sduoj-problem/sduoj-problem-service/build/libs/ /sduoj/
COPY    sduoj-submit/sduoj-submit-service/build/libs/ /sduoj/
COPY        sduoj-user/sduoj-user-service/build/libs/ /sduoj/

ADD https://github.com/SDUOJ/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN mkdir -p /sduoj/dockerWorkspace \
 && chmod +x /wait


ENV NACOS_ADDR=127.0.0.1:8848 \
    ACTIVE=prod \
    SERVICE=none \
    PORT=8080

EXPOSE 8080

WORKDIR /sduoj

HEALTHCHECK --interval=15s --timeout=3s --retries=3 \
  CMD test `curl -s http://localhost:8080/actuator/health` = '{"status":"UP"}' || exit 1

CMD /wait \
 && java -jar sduoj-$SERVICE.jar \
         --sduoj.config.nacos-addr=$NACOS_ADDR \
         --sduoj.config.active=$ACTIVE \
         --sduoj.config.port=$PORT \
         >> /sduoj/sduoj.log