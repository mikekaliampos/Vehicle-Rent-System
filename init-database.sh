#!/bin/bash

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
while ! mysqladmin ping -h"db" -u"root" -p"rootpassword" --silent; do
    echo "MySQL not ready yet... waiting"
    sleep 2
done

echo "MySQL is ready! Initializing database..."

# Give MySQL a bit more time to fully start
sleep 5

# Run InitDatabase (που είναι ήδη compiled από το Dockerfile)
cd /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
java -cp "/usr/local/tomcat/lib/*:." webapp.database.InitDatabase

# Check if initialization was successful
if [ $? -eq 0 ]; then
    echo "✅ Database initialization completed successfully!"
    echo "🚀 Starting Tomcat..."
    catalina.sh run
else
    echo "❌ Database initialization failed!"
    echo "🔄 Retrying in 10 seconds..."
    sleep 10
    java -cp "/usr/local/tomcat/lib/*:." webapp.database.InitDatabase
    if [ $? -eq 0 ]; then
        echo "✅ Database initialization completed on retry!"
        echo "🚀 Starting Tomcat..."
        catalina.sh run
    else
        echo "❌ Database initialization failed after retry. Starting Tomcat anyway..."
        catalina.sh run
    fi
fi