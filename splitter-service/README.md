# Splitter Service Documentation

## 1. Обзор

Splitter Service — это микросервис в составе системы обработки видео, отвечающий за асинхронную нарезку видеофайлов на сегменты (чанки). Сервис принимает события из Apache Kafka, скачивает исходный файл из S3, выполняет сегментацию через нативную системную утилиту FFmpeg в изолированном пуле потоков и загружает готовые чанки обратно в S3-хранилище.

## 2. Стек технологий

- **Java 25**
- **Spring Boot 4.0.6**
- **Spring Kafka**
- **AWS SDK for S3**
- **FFmpeg CLI (Native ProcessBuilder)**
- **Alpine Linux (bellsoft/liberica-openjdk-alpine:25)**
- **Docker / Docker Compose**

## 3. Архитектура и компоненты

Сервис построен по событийно-ориентированной схеме. Вычислительно тяжелая работа по нарезке видео вынесена в чистые воркер-потоки `ExecutorService` без прямого вмешательства Spring DI во внутренний цикл обработки.

- **`com.sashurman.splitterservice.config`**
- `KafkaConfig` — конфигурация продюсеров и консьюмеров Spring Kafka.
- **`com.sashurman.splitterservice.DTO`**
- `VideoRequest` — DTO-запись (Java Record) входящей задачи на обработку.
- **`com.sashurman.splitterservice.enums`**
- `ProcessStatus` — перечисление статусов задачи (`APPLY`, `PROCESSING`, `COMPLETED`, `FAILED`).
- **`com.sashurman.splitterservice.kafkaListener`**
- `VideoUploadEventListener` — точка входа; вычитывает события о загрузке видео из Kafka.
- **`com.sashurman.splitterservice.listener`**
- `VideoProcessingEventListener` — слушатель внутренних событий и публикатор обновлений статусов.
- **`com.sashurman.splitterservice.service`**
- `splitterService` — сервис-оркестратор; управляет пулом потоков `ExecutorService` и жизненным циклом задач.
- **`com.sashurman.splitterservice.task`**
- `VideoProcessingTask` — задача (`Callable`), исполняемая в отдельном потоке. Отвечает за логику «скачал из S3 -> нарезал -> загрузил в S3».
- **`com.sashurman.splitterservice.utils`**
- `FfmpegNativeProducer` — низкоуровневая обертка над `ProcessBuilder` для запуска команд `ffmpeg` в Alpine Linux.

## 4. Конфигурация (application.yaml)

**Порт для Kafka по умолчанию: 9092**

| **Переменная** | **Описание** |
| --- | --- |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Адрес и порт кластера Kafka (`localhost:9092`) |
| `AWS_S3_ENDPOINT` | Эндпоинт S3-хранилища |
| `AWS_ACCESS_KEY_ID` | Ключ доступа S3 |
| `AWS_SECRET_ACCESS_KEY` | Секретный ключ S3 |
| `AWS_BUCKET_NAME` | Название корзины в S3-хранилище |
| `SPLITTER_THREAD_POOL_SIZE` | Размер пула потоков `ExecutorService` для нарезки |

## 5. Требования к окружению и Docker

Для корректной работы сервиса в базовом образе контейнера обязана присутствовать утилита `ffmpeg`. Установка происходит во время сборки через пакетный менеджер Alpine `apk`.

**Dockerfile:**

```dockerfile
FROM bellsoft/liberica-openjdk-alpine:25

# Установка нативного FFmpeg
RUN apk add --no-cache ffmpeg

WORKDIR /app

COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

```

## 6. Запуск проекта

### Локально

Убедись, что локально установлен `ffmpeg` и запущены окружения Kafka и S3 (MinIO).

Выполни сборку проекта:

```bash
./gradlew clean build

```

Запусти приложение:

```bash
./gradlew bootRun

```

### Docker

Для сборки образа и запуска контейнера:

```bash
docker-compose build --no-cache
docker-compose up -d

```



