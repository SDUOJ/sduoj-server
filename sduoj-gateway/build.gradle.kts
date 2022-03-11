plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("cn.edu.sdu.qd.oj.java-conventions")
}

dependencies {
    /* 1-st party dependency */
    implementation(project(":sduoj-common:sduoj-common-entity"))

    /* 2-nd party dependency */
    implementation(project(":sduoj-auth:sduoj-auth-interface"))
    implementation(project(":sduoj-user:sduoj-user-interface"))

    /* 3-rd party dependency */
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.apache.commons:commons-pool2")
    implementation("cn.hutool:hutool-core")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.javassist:javassist")
}

group = "cn.edu.sdu.qd.oj.gateway"
description = "sduoj-gateway"
