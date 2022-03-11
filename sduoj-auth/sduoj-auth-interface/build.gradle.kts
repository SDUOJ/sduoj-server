plugins {
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    api(project(":sduoj-common:sduoj-common-entity"))

    /* 2-rd party dependency */

    /* 3-rd party dependency */
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")
    api("org.hibernate.validator:hibernate-validator")
    api("javax.validation:validation-api")
}

group = "cn.edu.sdu.qd.oj.auth"
description = "sduoj-auth-interface"
