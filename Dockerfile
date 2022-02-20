FROM ubuntu:18.04
MAINTAINER SDUOJ-dev

ENV LANG C.UTF-8

COPY docker/sources.list /etc/apt/sources.list

ADD https://github.com/SDUOJ/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN mkdir -p /sduoj/dockerWorkspace \
 && chmod +x /wait

RUN apt-get update \
 && apt-get install -qq -y wget curl unzip openjdk-8-jdk

RUN ln -sf /usr/lib/jvm/java-8-openjdk-amd64/bin/java /usr/bin/java \
 && ln -sf /usr/lib/jvm/java-8-openjdk-amd64/bin/javac /usr/bin/javac \
# download
 && wget -O /sduoj/dockerWorkspace/server.zip https://codeload.github.com/SDUOJ/sduoj-server/zip/master \
 && unzip -o -q -d /sduoj/dockerWorkspace /sduoj/dockerWorkspace/server.zip \
# build
 && cd /sduoj/dockerWorkspace/sduoj-server* \
 && chmod +x ./gradlew \
 && ./gradlew build --scan \
# copy
 && mv                       sduoj-gateway/build/libs/* /sduoj/ \
 && mv                     sduoj-websocket/build/libs/* /sduoj/ \
 && mv       sduoj-auth/sduoj-auth-service/build/libs/* /sduoj/ \
 && mv sduoj-contest/sduoj-contest-service/build/libs/* /sduoj/ \
 && mv sduoj-filesys/sduoj-filesys-service/build/libs/* /sduoj/ \
 && mv sduoj-problem/sduoj-problem-service/build/libs/* /sduoj/ \
 && mv   sduoj-submit/sduoj-submit-service/build/libs/* /sduoj/ \
 && mv       sduoj-user/sduoj-user-service/build/libs/* /sduoj/ \
# clean
 && rm -rf ~/.m2 \
 && rm -rf ~/.gradle \
 && rm -rf /sduoj/dockerWorkspace \
 && apt remove -y openjdk-11-jre-headless \
 && apt-get autoremove -y

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