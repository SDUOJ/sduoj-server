plugins {
    `java-library`
    `maven-publish`
    id("checkstyle")
    id("io.spring.dependency-management")
}

group = "cn.edu.sdu.qd.oj"
version = Versions.sduoj
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    // uncomment follows if GFW in your network
//    maven { url = uri("https://maven.aliyun.com/repository/public/") }
//    maven { url = uri("https://maven.aliyun.com/repository/spring/") }
}

checkstyle {
    toolVersion = "8.41.1"
}

dependencies {
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Versions.lombok}")
    testAnnotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.springBoot}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
        mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${Versions.springCloudAlibaba}")
    }
    dependencies {
        dependency("org.apache.commons:commons-lang3:${Versions.commonsLang3}")
        dependency("org.mybatis.spring.boot:mybatis-spring-boot-starter:${Versions.mybatisStarter}")
        dependency("mysql:mysql-connector-java:${Versions.mysql}")
        dependency("com.baomidou:mybatis-plus-boot-starter:${Versions.mybatisPlus}")
        dependency("org.mapstruct:mapstruct:${Versions.mapstruct}")
        dependency("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
        dependency("com.alibaba:fastjson:${Versions.fastjson}") // TODO: remove fastjson from this project
        dependency("com.google.guava:guava:${Versions.guava}")
        dependency("com.alibaba:easyexcel:${Versions.easyexcel}")
        dependency("javax.validation:validation-api:${Versions.javaxValidation}")
        dependency("org.hibernate.validator:hibernate-validator:${Versions.hibernate}")
        dependency("org.apache.commons:commons-pool2:${Versions.commonsPool}")
        dependency("cn.hutool:hutool-core:${Versions.hutool}")
        dependency("com.github.ben-manes.caffeine:caffeine:${Versions.caffeine}")
        dependency("org.slf4j:slf4j-api:${Versions.slf4j}")
        dependency("org.slf4j:slf4j-nop:${Versions.slf4j}")
        dependency("com.fasterxml.jackson.core:jackson-core:${Versions.jackson}")
        dependency("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")
        dependency("commons-codec:commons-codec:${Versions.commonsCodec}")
        dependency("io.github.openfeign:feign-jackson:${Versions.feignJackson}")
        dependency("io.netty:netty-buffer:${Versions.netty}")
        dependency("junit:junit:${Versions.junit}")
        dependency("org.apache.httpcomponents:httpclient:${Versions.httpclient}")
        dependency("org.apache.logging.log4j:log4j-to-slf4j:${Versions.log4j}")
        dependency("org.apache.logging.log4j:log4j-api:${Versions.log4j}")
        dependency("org.apache.logging.log4j:log4j-core:${Versions.log4j}")
    }
}

// 配置service工程
val isService = project.name.contains(Regex("-(service|gateway|websocket)$"))
if (isService) {
    tasks.named("bootJar") {
        (this as Jar).archiveFileName.set(project.name.replace("-service", "") + ".jar")
    }
    tasks.classes {
        copy {
            from("${rootProject.rootDir}/config/logback/")
            into("${buildDir}/resources/main/")
        }
    }
}
// 发布包到Maven仓库
tasks.jar { enabled = true }
publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.jar)
    }
}
// 编译器参数
tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    // 暂时关闭MapStruct Warning TODO: 设计好converter层来解决MapStruct中的unmappedTarget问题
    options.compilerArgs.add("-Amapstruct.unmappedTargetPolicy=IGNORE")
}
// style 检查，不通过不编译
tasks.withType<Checkstyle>().configureEach {
    doLast { reportErrorStyle(this as Checkstyle) }
}
tasks.classes { finalizedBy("checkstyleMain") }
tasks.testClasses { finalizedBy("checkstyleTest") }
