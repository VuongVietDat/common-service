FROM openjdk:17
COPY target/common-service-1.0.0.jar /app/common.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=k8s", "/app/common.jar"]