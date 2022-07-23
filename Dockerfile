FROM maven:3.8.6-eclipse-temurin-11-alpine

WORKDIR /application

COPY src ./src
COPY pom.xml ./pom.xml
COPY local-repo ./local-repo

RUN mvn clean package

WORKDIR /application/target/classes

CMD java -cp ".:./lib/*" com.cyecize.domainrouter.AppStartUp
