FROM maven:3.9.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn -DskipTests clean package

FROM tomcat:9.0-jdk8-temurin

# Deploy as ROOT.war so app opens at "/" on Render
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/runtime_build_v2/ShopZone.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 10000

# Bind Tomcat connector to Render's PORT (default 10000)
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-10000}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]
