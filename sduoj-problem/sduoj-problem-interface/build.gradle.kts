plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-entity"))
    api(project(":sduoj-common:sduoj-common-util"))

    /* 2-nd party dependency */
    api(project(":sduoj-user:sduoj-user-interface"))

    /* 3-rd party dependency */
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.springframework:spring-web")
    api("io.netty:netty-buffer")
}

group = "cn.edu.sdu.qd.oj.problem"
description = "sduoj-problem-interface"
