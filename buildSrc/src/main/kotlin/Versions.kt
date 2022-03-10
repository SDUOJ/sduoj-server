object Versions {
    val sduoj = "1.0.0-SNAPSHOT"

    val springBoot = "2.1.12.RELEASE"
    val springCloud = "Greenwich.SR5"
    val springCloudAlibaba = "2.1.2.RELEASE"

    val mybatisStarter = "2.1.1"
    val mysql = "8.0.16"
    val mybatisPlus = "3.4.0"

    val lombok = "1.18.12"
    val commonsLang3 = "3.11"
    val mapstruct = "1.4.0.CR1"
    val fastjson = "1.2.73"
    val guava = "29.0-jre"
    val easyexcel = "2.2.10"
    val javaxValidation = "2.0.1.Final"
    val hibernate = "6.0.18.Final"
    val commonsPool = "2.6.2"
    val hutool = "5.3.1"
    val caffeine = "2.6.2"
    val javassist = "3.27.0-GA"
    val slf4j = "1.7.30"
    val jackson = "2.9.10" // TODO: upgrade
    val commonsCodec = "1.11"
    val feignJackson = "10.4.0"
    val netty = "4.1.45.Final"
    val junit = "4.12"
    val httpclient = "4.5.12"
    val log4j = "2.17.1" // fix security bug of log4j2, 需注意若子模块主动填写了log4j版本号(在<version>标签内)则该修复不生效
}