# sduoj-server

sduoj 的业务服务器，技术栈 Spring Boot + Spring Cloud + MyBatis + Lombok + 通用 Mapper + PageHelper。

## 迭代日志

*   2020年2月26日 `init0`：建立项目仓库。

*   2020年2月26日 `init1`：初次提交，搭框架，实现 API 规范，指定通常异常处理，实现 User 接口查询。 

*   2020年2月27日 `init2`：
    *   数据库 user 表增加`学号`列
    *   更新增加微服务注册中心 `Eureka`
    *   更新增加网关中心 `Zuul`
    *   更新增加鉴权微服务 `auth-service`，初定认证方案为 token 机制

