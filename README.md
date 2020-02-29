# SDUOJ Server

This is the service server of the SDUOJ.

## Solution stack

*   Spring Boot
*   Spring Cloud
*   MyBatis
*   Mybatis Common Mapper
*   Lombok
*   PageHelper

## Development log of V1.0

*   2020-2-26 `init0`：
    *   Create the repo.
*   2020-2-26 `init1`：
    *   Build a framework.
    *   Develop API specifications.
    *   General exception handling.
    *   Build `user-service`.
*   2020-2-27 `init2`：
    *   Add `studentId` column to the table `user`  of database.
    *   Add `registry-service` using `Eureka` component.
    *   Add `api-gateway` using `Zuul` component.
    *   Add `auth-service` which may use "token" as an authentication scheme.
