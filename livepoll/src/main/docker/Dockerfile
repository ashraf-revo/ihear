FROM openjdk:8-jre-alpine
MAINTAINER org.revo
EXPOSE ${project.port}
COPY maven /maven/
ENTRYPOINT ["java","-jar","/maven/${project.artifactId}-${project.version}.${packaging}"]
