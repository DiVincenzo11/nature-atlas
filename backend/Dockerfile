# Original archive Dockerfile attribution: jyz. Restored local-demo entrypoint.
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Shanghai SERVER_ADDRESS=0.0.0.0
COPY target/bird.jar /app/bird.jar
EXPOSE 18900
ENTRYPOINT ["java","-jar","/app/bird.jar"]
