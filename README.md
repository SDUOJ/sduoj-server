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

*   2020-02-26 `init0`:
    *   Create the repo.
*   2020-02-26 `init1`:
    *   Build a framework.
    *   Develop API specifications.
    *   General exception handling.
    *   Build `user-service`.
*   2020-02-27 `init2`:
    *   Add `studentId` column to the table `user`  of database.
    *   Add `registry-service` using `Eureka` component.
    *   Add `api-gateway` using `Zuul` component.
    *   Add `auth-service` which may use "token" as an authentication scheme.
*   2020-03-01 `init3`:
    *   Refactor : Change the key `account` of table `oj_users` to `username`
    *   The code of accepted request change to 0 from 1.
    *   CORS is used to solve cross-domain problems which .
    *   Add filter in `Gateway` module which can intercept all request and do some verification.
    *   `login` and `logout` can be used normally.
*   2020-03-02 `init4 `:
    *   Added support for  `application/json` instead of `application/x-www-form-urlencoded` in request method.
    *   Use `HandlerMethodReturnValueHandler` to format response json.
*   2020-03-03 :
    *   Refactor : Update the solution of global exception handle.
    *   Build`problem-service`.
*   2020-03-06 :
    *   Build `submit-service` and add a resolve view which is used to test RabbitMQ.
*   2020-03-09 :
    *   Complete the API of querying problem list in paging.

