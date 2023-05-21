#Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk-jammy

#Information around who maintains the image
MAINTAINER Diamantopoulos Konstantinos

# Add the application's jar to the container
COPY target/micro_epoweredmove_chargingstation-0.0.1-SNAPSHOT.jar micro_epoweredmove_chargingstation-0.0.1-SNAPSHOT.jar

#execute the application
ENTRYPOINT ["java","-jar","/micro_epoweredmove_chargingstation-0.0.1-SNAPSHOT.jar"]