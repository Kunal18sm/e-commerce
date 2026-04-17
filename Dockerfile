FROM maven:3.9.9-eclipse-temurin-8

WORKDIR /app

# Copy project sources
COPY . .

# Render provides PORT at runtime (default 10000)
ENV PORT=10000
EXPOSE 10000

# Start embedded Tomcat on Render port and bind to all interfaces.
# Use root context so Render base URL works without /ShopZone suffix.
CMD ["sh", "-c", "mvn -DskipTests tomcat7:run -Dmaven.tomcat.hostName=0.0.0.0 -Dmaven.tomcat.port=${PORT:-10000} -Dmaven.tomcat.path=/"]
