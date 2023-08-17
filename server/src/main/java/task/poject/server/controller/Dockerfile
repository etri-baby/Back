FROM openjdk:17
COPY build/lib/*.far app.jar
EXPOSE 23817
ENTRYPOINT ["java", "-jar", "app.jar"]