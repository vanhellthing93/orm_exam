FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/orm_exam-1.0.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
