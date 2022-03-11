plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-util"))
    api(project(":sduoj-common:sduoj-common-entity"))

    /* 2-nd party dependency */

    /* 3-rd party dependency */
    api("org.springframework:spring-web")
}

group = "cn.edu.sdu.qd.oj.filesys"
description = "sduoj-filesys-interface"
