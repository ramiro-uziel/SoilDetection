FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

RUN mkdir -p /app/wallet
COPY wallet /app/wallet

COPY target/SoilDetection-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]