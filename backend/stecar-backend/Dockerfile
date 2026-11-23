FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src src
RUN ./mvnw -B -DskipTests package

FROM build AS integration-tests

RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.8.1:get \
        -Dartifact=org.apache.maven.surefire:surefire-junit-platform:3.5.6 \
    && ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.8.1:get \
        -Dartifact=org.junit.platform:junit-platform-launcher:6.0.3

CMD ["./mvnw", "-o", "-B", "test"]

FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S stecar \
    && adduser -S -G stecar stecar

WORKDIR /app
COPY --from=build /workspace/target/stecar-backend-0.0.1-SNAPSHOT.jar app.jar

USER stecar
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=5 \
    CMD wget -q -O /dev/null http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
