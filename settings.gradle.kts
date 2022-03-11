rootProject.name = "sduoj-server"

include(
    "sduoj-auth:sduoj-auth-interface",
    "sduoj-auth:sduoj-auth-service",

    "sduoj-common:sduoj-common-entity",
    "sduoj-common:sduoj-common-redis",
    "sduoj-common:sduoj-common-util",
    "sduoj-common:sduoj-common-web",

    "sduoj-contest:sduoj-contest-interface",
    "sduoj-contest:sduoj-contest-service",

    "sduoj-filesys:sduoj-filesys-interface",
    "sduoj-filesys:sduoj-filesys-service",

    "sduoj-gateway",

    "sduoj-problem:sduoj-problem-interface",
    "sduoj-problem:sduoj-problem-service",

    "sduoj-submit:sduoj-submit-interface",
    "sduoj-submit:sduoj-submit-service",

    "sduoj-user:sduoj-user-interface",
    "sduoj-user:sduoj-user-service",

    "sduoj-websocket",
)