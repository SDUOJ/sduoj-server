plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    implementation(project(":sduoj-common:sduoj-common-web"))
    implementation(project(":sduoj-common:sduoj-common-util"))
    implementation(project(":sduoj-filesys:sduoj-filesys-interface"))

    /* 2-nd party dependency */

    /* 3-rd party dependency */
}

group = "cn.edu.sdu.qd.oj.filesys"
description = "sduoj-filesys-service"
