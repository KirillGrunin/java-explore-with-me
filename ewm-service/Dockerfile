FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5004
COPY target/*.jar ewm_service.jar
ENTRYPOINT ["java","-jar","/ewm_service.jar"]