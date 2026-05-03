FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/homeserver-0.0.1-SNAPSHOT.jar app.jar
VOLUME /homeserver-files
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
