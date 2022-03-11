plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-entity"))

    /* 2-nd party dependency */

    /* 3-rd party dependency */
    api("org.springframework:spring-context")
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("commons-codec:commons-codec")
}

group = "cn.edu.sdu.qd.oj.common"
description = "sduoj-common-util"

