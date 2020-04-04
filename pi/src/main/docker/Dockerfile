FROM openjdk:8-jre-alpine
MAINTAINER org.revo
EXPOSE ${project.port}
COPY maven /maven/
ENTRYPOINT ["java","-jar","-Dspring.cloud.kubernetes.enabled=true -Dspring.profiles.active=cloud","/maven/${project.artifactId}-${project.version}.${packaging}"]