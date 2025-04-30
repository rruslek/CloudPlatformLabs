FROM openjdk:21-jdk

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar --PORT=${PORT}"]