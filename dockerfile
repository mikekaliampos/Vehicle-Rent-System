FROM tomcat:9.0-jdk11

# Install MySQL client for initialization
RUN apt-get update && apt-get install -y mysql-client && rm -rf /var/lib/apt/lists/*

# Copy web application to Tomcat
COPY webapp/ /usr/local/tomcat/webapps/ROOT/

# Copy libraries to Tomcat lib directory
COPY webapp/lib/mysql-connector-java-8.0.25.jar /usr/local/tomcat/lib/
COPY webapp/lib/gson-2.8.0.jar /usr/local/tomcat/lib/

# Copy and make initialization script executable
COPY init-database.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init-database.sh

# Compile Java classes manually
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/webapp/database
COPY webapp/database/*.java /tmp/src/
WORKDIR /tmp/src
RUN javac -cp "/usr/local/tomcat/lib/*" *.java && \
    mv *.class /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/webapp/database/

# Wait for database and initialize
CMD ["/bin/bash", "-c", "/usr/local/bin/init-database.sh"]