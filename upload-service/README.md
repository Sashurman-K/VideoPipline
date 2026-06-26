# Upload Service Documentation
## 1. Обзор
   Upload Service — это микросервис в составе системы обработки видео, отвечающий за прием файлов от пользователей, их валидацию и сохранение в S3-хранилище.
## 2. Стек технологий
   * **Java 25**
   * **Spring Boot 4.0.6**
   * **Spring Data JPA**
   * **PostgreSQL 17**
   * **AWS S3 (Compatible Storage)**
   * **Flyway (миграции БД)**
   * **Kafka** 
## 3. Архитектура и компоненты
   Сервис построен по модульному принципу. Работа с данными осуществляется через Hibernate, а передача файлов — через AWS SDK для S3.

## 4. Конфигурация (application.yaml)
**Порт для kafka по умолчанию 9092**

|Переменная|Описание|
|-|-|
SPRING_DATASOURCE_URL |JDBC URL к базе данных PostgreSQL
SPRING_DATASOURCE_USERNAME |Имя пользователя БД
SPRING_DATASOURCE_PASSWORD |Пароль от БД
JWT_SECRET_KEY| JWT секретный ключ
AWS_S3_ENDPOINT|Эндпоинт S3-хранилища
AWS_ACCESS_KEY_ID|Ключ доступа S3
AWS_SECRET_ACCESS_KEY|Секретный ключ S3
AWS_BUCKET_NAME|Название корзины в s3 хранилище



## 5. База данных и миграции
   Сервис использует Flyway для версионирования схемы БД.\
   Путь к миграциям: ```src/main/resources/db/migration/\```\
   Правило именования: ```V<номер>__<описание>.sql (например, V1__init_schema.sql)```\
   **Важно**: При внесении изменений в схему БД всегда создавай новый файл миграции. Не допускается изменение существующих миграций после их применения.
## 6. Запуск проекта
   Локально
   Убедись, что запущен Docker Compose (PostgreSQL).
   Выполни сборку проекта:
```Bash
./gradlew clean build
```

Запусти приложение:
```Bash
./gradlew bootRun
```

Docker
Для сборки образа и запуска контейнера:


```Bash
docker-compose build --no-cache
docker-compose up -d
```


