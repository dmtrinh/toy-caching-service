Toy Caching Service
===================
[![build](https://github.com/dmtrinh/toy-caching-service/actions/workflows/build.yaml/badge.svg)](https://github.com/dmtrinh/toy-caching-service/actions/workflows/build.yaml)
[![Coverage](.github/badges/jacoco.svg)](https://dmtrinh.github.io/toy-caching-service/index.html)
[![Branches](.github/badges/branches.svg)](https://dmtrinh.github.io/toy-caching-service/index.html)

Simple service to support implementation of operations that need to be idempotent.

## Dependencies
This service is dependent on Redis.  The [local-stack](https://github.com/dmtrinh/local-stack) repo has Redis and other dependencies pre-wired for local development.

## Getting Started

### Building

Run `gradlew` to build and run unit tests locally; two default tasks will be executed: `assemble` and `test` 
```shell
./gradlew
```

Run the `buildImage` Gradle task to create a local Docker image:
```shell
./gradlew buildImage
```

### Running

To use the standard Spring Boot-Gradle tasks (e.g. `bootRun`, `bootTestRun`), the following environment variables will need to be set:
   *  `REDIS_HOST`
   *  `REDIS_PORT`
   *  `REDIS_USERNAME`
   *  `REDIS_PASSWORD`

**For a quicker setup**, use [local-stack](https://github.com/dmtrinh/local-stack) and then one of these two options:

#### Option #1: Docker Compose
```shell
docker compose up
```

This will instantiate the previously built container for this microservice and join it to the same `local-stack-network`.

#### Option #2: application-local.yaml config for Spring Boot
The [application-local.yaml](application-local.yaml) has been pre-wired to work with [local-stack](https://github.com/dmtrinh/local-stack):

```shell
./gradlew bootRun --args='--spring.config.location=application-local.yaml'
```

Alternatively, you can also use the custom `bootRunLocal` task:
```shell
./gradlew bootRunLocal
```

## API 
### POST /caching-service/cache

##### Summary

Save an item to cache.

##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| request | body | request | Yes | [IdempotentRequest](#idempotentrequest) |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | string |
| 201 | Created |  |
| 401 | Unauthorized |  |
| 403 | Forbidden |  |
| 404 | Not Found |  |

### GET /caching-service/cache/{key}

##### Summary

Retrieve an item from cache.

##### Parameters

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| key | path | key | Yes | string |

##### Responses

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | OK | string |
| 401 | Unauthorized |  |
| 403 | Forbidden |  |
| 404 | Not Found |  |

### Models

#### IdempotentRequest

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| clientId | string | client Id | No |
| idempotencyKey | string | key to use for retrieval | yes |
| requestDatetime | dateTime | request timestamp  | No |
| requestMethod | string | request Method(GET,POST,PUT,DELETE) | No |
| requestPayload | string | request body | No |
| responsePayload | string | response body | yes |
| ttl | long | time to leave cache | No |
| urlPath | string | URL Path | No |
