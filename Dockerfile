# 1. Build aşaması: testleri çalıştırır
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

# Testleri çalıştır, başarısızsa imaj oluşmaz
RUN mvn clean package

# 2. Run aşaması: testleri geçen jar'ı çalıştırır
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/target/digital-wallet-0.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
