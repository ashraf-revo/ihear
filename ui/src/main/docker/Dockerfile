FROM openjdk:8-jre-alpine
MAINTAINER org.revo
EXPOSE ${project.port}
COPY maven /maven/
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=kubernetes","/maven/${project.artifactId}-${project.version}.${packaging}"]
