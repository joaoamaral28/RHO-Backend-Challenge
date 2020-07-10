FROM openjdk:8
ADD target/rho-challenge.jar rho-challenge.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rho-challenge.jar"]