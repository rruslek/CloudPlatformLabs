# Лабораторная работа №1
## Реализация балансировки и отказоустойчивости с использованием Nginx

### Содержание

[1. Постановка задачи](#task)

[2. Решение](#implementation)

[3. Выводы](#conclusion)

## <a id="task" style="color: lightgrey">1. Постановка задачи

- #### Разработать приложение, содержащее HTTP endpoint, при обращении к которому возвращается ответ вида {“counter”: “1”}. При каждом обращении счетчик должен увеличиваться.
- #### Запустить несколько экземпляров данного приложения на разных портах (возможно использование Docker, но не обязательно)
- #### Запустить Nginx, который балансирует нагрузку между запущенными веб-сервисами
- #### Реализовать и проанализировать различные алгоритмы балансировки: round robin, hash, least conn, least time, random, и т.д.
- #### Реализовать и проанализировать функционал реализации отказоустойчивости (fail_timeout, max_fails) при отключении одного или нескольких веб-сервисов
- #### Проанализировать результаты нагрузочного тестирования с использованием Apache benchmark и/или Apache JMeter.

## <a id="implementation" style="color: lightgrey">2. Решение</a>

Для работы было разработано проостое приложение, реализующее счетчик.

Чтобы запустить несколько экземпляров этого приложения был созданы файлы Dockerfile и docker-compose.yml:
 
- **Dockerfile**
```Dockerfile
FROM openjdk:21-jdk

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

- **docker-compose.yml**
```yml
services:
  app1:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    environment:
      - PORT=8081

  app2:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8082:8082"
    environment:
      - PORT=8082

  app3:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8083:8083"
    environment:
      - PORT=8083

```

Для запуска контейнера была использована команда:
```cmd
docker-compose up
```


Далее был запущен nginx:
```cmd
start nginx
```


- **Конфигурация nginx.conf**
```conf
worker_processes 1;

events {
    worker_connections 1024;
}

http {
    upstream spring_app {
        //СЮДА ВСТАВИТЬ АЛГОРИТМ БАЛАНСИРОВКИ (least_conn, round_robin, random и т.д)
		
        server 127.0.0.1:8081;
        server 127.0.0.1:8082;
        server 127.0.0.1:8083;
    }

    server {
        listen 8080;

        location / {
            proxy_pass http://spring_app;

            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

После этого были использованы и проанализированы различные алгоритмы балансировки:
- #### round-robin — запросы к серверам приложений распределяются по кругу
- #### least-connected — следующий запрос назначается серверу с наименьшим количеством активных подключений
- #### hash — позволяет распределять запросы пользователь по заданному параметру (URI, IP, порт)
- #### least-time — для каждого запроса выбирается сервер с наименьшей средней задержкой (время до первого или последнего байта) и наименьшим количеством активных подключений 
- #### random — запросы распределяются случайно (также можно использовать вместе с least-time и least-connected)

Также была реализована конфигурация для проверки отказоустойчивости:

- **nginx.conf**
```conf 
http {
    upstream spring_app {
        //СЮДА ВСТАВИТЬ АЛГОРИТМ БАЛАНСИРОВКИ (least_conn, round_robin, random и т.д)

        server 127.0.0.1:8081 max_fails=2 fail_timeout=5s;
        server 127.0.0.1:8082 max_fails=2 fail_timeout=5s;
        server 127.0.0.1:8083 max_fails=2 fail_timeout=5s;
    }

    server {
        listen 8080;

        location / {
            proxy_pass http://spring_app;

            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

После этого были проведены тесты нагрузочного тестирования с помощью Apache JMeter

![](C:\Users\user\Documents\CloudPlatformLabs\HighLoadLabs\lab1\imagic-spring-aws\tests.png)

## <a id="conclusion" style="color: lightgrey">3. Выводы</a>