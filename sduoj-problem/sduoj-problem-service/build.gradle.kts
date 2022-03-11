plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    implementation(project(":sduoj-common:sduoj-common-web"))
    implementation(project(":sduoj-common:sduoj-common-util"))
    implementation(project(":sduoj-common:sduoj-common-redis"))
    implementation(project(":sduoj-problem:sduoj-problem-interface"))

    /* 2-nd party dependency */
    implementation(project(":sduoj-user:sduoj-user-interface"))
    implementation(project(":sduoj-filesys:sduoj-filesys-interface"))
    implementation(project(":sduoj-submit:sduoj-submit-interface"))

    /* 3-rd party dependency */
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}

group = "cn.edu.sdu.qd.oj.problem"
description = "sduoj-problem-service"
