FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY ./gradle ./gradle
COPY ./src ./src
COPY ./build.gradle ./
COPY ./gradlew ./
COPY ./settings.gradle ./

RUN bash ./gradlew build -x test

EXPOSE 3001

CMD java -jar /app/build/libs/demo-0.0.1-SNAPSHOT.jar
