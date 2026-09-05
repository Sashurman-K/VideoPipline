"""# Video Pipeline Microservices

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=flat&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-Event--Driven-231F20?style=flat&logo=apachekafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

Микросервисная платформа для загрузки, обработки и анализа видео с единой точкой входа через API Gateway, асинхронной обработкой событий через Apache Kafka и изолированными БД PostgreSQL.

---

## 📖 Описание

Проект представляет собой отказоустойчивую микросервисную архитектуру на **Java 21** и **Spring Boot 3**, предназначенную для эффективной загрузки, парсинга и обработки видеопотоков. Все сервисы изолированы в Docker-сети, а внешние вызовы маршрутизируются через **Spring Cloud Gateway** с централизованной проверкой **JWT**.

---

## 🛠 Технологический стек

* **Backend:** Java 21, Spring Boot 3, Spring Cloud Gateway (WebFlux), Spring Security (JWT), Spring Data JPA.
* **Storage & Messaging:** PostgreSQL 17, Apache Kafka, AWS / Selectel S3 Object Storage.
* **DevOps & Management:** Docker, Docker Compose, CloudBeaver, Kafka UI.

---

## 🏗 Архитектура системы

```text
                                +-------------------+
                                |    Client / Web   |
                                +---------+---------+
                                          |
                                          v
                                +-------------------+
                                |    API Gateway    |
                                |     (:8080)       |
                                +----+----+----+----+
                                     |    |    |
        +----------------------------+    |    +----------------------------+
        | ( /api/v1/users )               | ( /api/v1/videos )              | ( /api/v1/analysis )
        v                                 v                                 v
+---------------+                 +---------------+                 +---------------+
|  User Service |                 | Upload Service|                 |   AI Service  |
|    (:8081)    |                 |    (:8082)    |                 |    (:8084)    |
+-------+-------+                 +-------+-------+                 +-------+-------+
        |                                 |                                 |
        v                                 v                                 v
 [Postgres User]                   [Postgres Upload]                  [Postgres AI]
                                          |
                                   (Kafka Event)
                                          |
                                          v
                                   [Apache Kafka]
                                          |
                                   (Consume Event)
                                          |
                                          v
                                  +---------------+
                                  |Splitter Service|
                                  |    (:8083)    |
                                  +-------+-------+
                                          |
                                          v
                                 [Postgres Splitter]

