FROM ubuntu:latest

RUN apt-get update

RUN apt-get install -y maven  openjdk-21-jdk

WORKDIR /app

COPY . .

RUN mvn clean install

EXPOSE 8085

CMD ["java", "-jar", "./target/servicemain-0.0.1-SNAPSHOT.jar"]