plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-entity"))

    /* 2-nd party dependency */

    /* 3-rd party dependency */
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.apache.commons:commons-lang3")
    api("com.google.guava:guava")
    api("com.alibaba:fastjson")
}

group = "cn.edu.sdu.qd.oj.common"
description = "sduoj-common-redis"

