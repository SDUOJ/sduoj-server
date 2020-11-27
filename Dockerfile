FROM ubuntu:18.04
MAINTAINER SDUOJ-dev

COPY docker/sources.list /etc/apt/sources.list
COPY docker/mavenSettings.xml /usr/share/maven/conf/settings.xml

RUN apt-get update \
 && apt-get install -y wget curl unzip openjdk-8-jdk maven

RUN ln -sf /usr/lib/jvm/java-8-openjdk-amd64/bin/java /usr/bin/java \
 && ln -sf /usr/lib/jvm/java-8-openjdk-amd64/bin/javac /usr/bin/javac \
 && mkdir /sduoj \
 && wget -O /sduoj/server.zip https://codeload.github.com/SDUOJ/sduoj-server/zip/master \
 && unzip -o -q -d /sduoj /sduoj/server.zip \
 && mkdir /usr/share/maven/conf/logging \
 && cd /sduoj/sduoj-server-master \
 && mvn package \
 && mv sduoj-gateway/target/sduoj-gateway.jar ../sduoj-gateway.jar \
 && mv sduoj-auth/sduoj-auth-service/target/sduoj-auth.jar ../sduoj-auth.jar \
 && mv sduoj-user/sduoj-user-service/target/sduoj-user.jar ../sduoj-user.jar \
 && mv sduoj-problem/sduoj-problem-service/target/sduoj-problem.jar ../sduoj-problem.jar \
 && mv sduoj-submit/sduoj-submit-service/target/sduoj-submit.jar ../sduoj-submit.jar \
 && mv sduoj-filesys/sduoj-filesys-service/target/sduoj-filesys.jar ../sduoj-filesys.jar \
 && mv sduoj-contest/sduoj-contest-service/target/sduoj-contest.jar ../sduoj-contest.jar \
 && mv sduoj-websocket/target/sduoj-websocket.jar ../sduoj-websocket.jar \
 && rm -rf ~/.m2 \
 && rm -rf /sduoj/sduoj-server-master \
 && apt remove -y openjdk-11-jre-headless \
 && apt-get purge -y maven \
 && apt-get autoremove -y

ENV NACOS_ADDR=127.0.0.1:8848 \
    ACTIVE=prod \
    SERVICE=none \
    PORT=8080

EXPOSE 8080

WORKDIR /sduoj

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD test `curl -s http://localhost:8080/actuator/health` == "{\"status\":\"UP\"}" || exit 1

CMD java -jar sduoj-$SERVICE.jar \
 --sduoj.config.nacos-addr=$NACOS_ADDR \
 --sduoj.config.active=$ACTIVE \
 --sduoj.config.port=$PORT \
 > /sduoj/sduoj.log