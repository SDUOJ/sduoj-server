plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-util"))
    api(project(":sduoj-common:sduoj-common-entity"))

    /* 2-nd party dependency */
    api(project(":sduoj-submit:sduoj-submit-interface"))

    /* 3-rd party dependency */
    api("org.springframework:spring-web")
}

group = "cn.edu.sdu.qd.oj.contest"
description = "sduoj-contest-interface"
