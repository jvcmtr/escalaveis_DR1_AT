FROM eclipse-temurin:21-jdk-alpine
EXPOSE 8080

RUN mvn clean package

WORKDIR /app
COPY target/escalaveis_dr1_at-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
