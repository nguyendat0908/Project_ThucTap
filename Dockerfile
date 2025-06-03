# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Build ứng dụng, bỏ qua test để tăng tốc
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
EXPOSE 9803
COPY --from=build /app/target/*.jar /app/spring-boot-job-hunter.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-job-hunter.jar"]