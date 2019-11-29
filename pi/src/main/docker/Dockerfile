FROM openjdk:12
MAINTAINER org.revo
EXPOSE ${project.port}
COPY maven /maven/
ENTRYPOINT ["java","-jar","/maven/${project.artifactId}-${project.version}.${packaging}"]
