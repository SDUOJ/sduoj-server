plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    implementation(project(":sduoj-common:sduoj-common-web"))
    implementation(project(":sduoj-common:sduoj-common-util"))
    implementation(project(":sduoj-common:sduoj-common-redis"))
    implementation(project(":sduoj-user:sduoj-user-interface"))

    /* 2-nd party dependency */

    /* 3-rd party dependency */
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

group = "cn.edu.sdu.qd.oj.user"
description = "sduoj-user-service"
