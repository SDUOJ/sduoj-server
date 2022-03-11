plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-entity"))
    api(project(":sduoj-common:sduoj-common-util"))

    /* 2-nd party dependency */
    api(project(":sduoj-problem:sduoj-problem-interface"))

    /* 3-rd party dependency */
    api("org.springframework:spring-web")
}

group = "cn.edu.sdu.qd.oj.submit"
description = "sduoj-submit-interface"
