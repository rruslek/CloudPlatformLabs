# Лабораторная работа №4
## Реализация запуска приложения в Kubernetes

### Содержание

[1. Постановка задачи](#setTask)

[2. Решение](#decision)

## <a id="setTask" style="color: lightgrey">1. Постановка задачи

- #### Пройти "Interactive Tutorial" по Kubernetes/Minikube
- #### Создать yaml файлы для работы с Kubernetes
- #### Управлять развертыванием контейнеров с использованием kubectl

## <a id="decision" style="color: lightgrey">2. Решение</a>

Для работы было использовано веб-приложение, разработанное в рамках лабораторных работы №3.

Перед использованием Minikube, необходимо было создать Docker Image и сделать "push" в Docker Hub.

Для этого был создан Dockerfile:

```Dockerfile
FROM openjdk:21-jdk

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Далее были использованы команды :

- **сборка JAR файла**

```cmd
mvn clean package
```

- **создание Docker Image**

```cmd
docker build -t rruslek/imagic-spring .
```

- **push в Docker Hub**

```cmd
docker push rruslek/imagic-spring
```

После подготовки необходимо было создать два файла:

- **service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: imagic

spec:
  type: LoadBalancer
  selector:
    app: imagic
  ports:
    - protocol: TCP
      name: http-traffic
      port: 8080
      targetPort: 8080
```

- **deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: imagic

spec:
  replicas: 1
  selector:
    matchLabels:
      app: imagic
  template:
    metadata:
      labels:
        app: imagic
    spec:
      containers:
        - name: imagic
          image: rruslek/imagic-spring
          ports:
            - containerPort: 8080
```

После их создания, а также предварительно установив Minikube, можно приступать к запуску приложения в Kubernetes.

Команды для работы c Minikube:

- **запуск minikube**

```cmd
minikube start
```

- **остановка minikube**

```cmd
minikube stop
```

- **применение файлов**

```cmd
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```


- **просмотр логов**

```cmd
kubectl logs <pod-name>
```

- **просмотр панели управления**

```cmd
kubectl dashboard
```

- **работа с локальными запросами**

```cmd
minikube tunnel
```
