FROM openjdk:8-jre
MAINTAINER SDUOJ-Team

ENV LANG C.UTF-8

COPY build/sduoj-*.jar /root/sduoj/

# download docker-compose-wait
COPY --from=sduoj/docker-compose-wait:latest /wait /wait


ENV NACOS_ADDR=127.0.0.1:8848 \
    ACTIVE=prod \
    SERVICE=none \
    PORT=8080

EXPOSE 8080

WORKDIR /root/sduoj/

HEALTHCHECK --interval=15s --timeout=3s --retries=3 \
  CMD test `curl -s http://localhost:8080/actuator/health` = '{"status":"UP"}' || exit 1

CMD /wait                                                        \
 && JAVA_OPT="${JAVA_OPT} -jar *$SERVICE.jar"                    \
 && JAVA_OPT="${JAVA_OPT} --sduoj.config.port=$PORT"             \
 && JAVA_OPT="${JAVA_OPT} --sduoj.config.active=$ACTIVE"         \
 && JAVA_OPT="${JAVA_OPT} --sduoj.config.nacos-addr=$NACOS_ADDR" \
 && echo "SDUOJ is starting, you can check the '~/sduoj/logs'"   \
 && echo "Run: java ${JAVA_OPT}"                                 \
 && java ${JAVA_OPT}
