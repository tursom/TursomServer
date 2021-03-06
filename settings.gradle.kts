rootProject.name = "TursomServer"
include("ts-core")
include("ts-core:ts-encrypt")
include("ts-core:ts-buffer")
include("ts-core:ts-datastruct")
include("ts-core:ts-pool")
include("ts-core:ts-hash")
include("ts-core:ts-log")
include("ts-core:ts-delegation")
include("ts-core:ts-delegation:ts-observer")
include("ts-core:ts-clone")
include("ts-core:ts-mail")
include("ts-core:ts-coroutine")
include("ts-core:ts-coroutine:ts-coroutine-lock")
include("ts-core:ts-ws-client")
include("ts-core:ts-yaml")
include("ts-core:ts-json")
include("ts-core:ts-xml")
include("ts-core:ts-async-http")
include("ts-socket")
include("ts-web")
include("ts-web:ts-web-netty")
include("ts-web:ts-web-coroutine")
include("ts-database")
include("ts-database:ts-mongodb")
include("ts-database:ts-mongodb:ts-mongodb-spring")
include("ts-database:ts-redis")
//include("web", "aop", "database", "utils", "utils:xml", "utils:async-http", "web:netty-web")
//include("socket", "socket:socket-async")
//include("AsyncSocket")
//include("log")
//include("json")
//include("utils:yaml")
//include("web:web-coroutine")
//include("microservices")
//include("database:database-mysql")
//include("database:mongodb")
//include("database:mongodb:mongodb-async")
//include("database:redis")
//include("utils:ws-client")
//include("utils:mail")
//include("utils:csv")
//include("utils:delegation")
//include("utils:observer")
//include("utils:TrafficForward")
//include("utils:performance-test")
//include("utils:math")
//include("utils:json")