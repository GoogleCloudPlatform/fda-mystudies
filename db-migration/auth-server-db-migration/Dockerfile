# Get image "flyway" from Flyway's repository
FROM flyway/flyway

WORKDIR /flyway 

# Database credentials
COPY flyway.properties /flyway/conf

# Add the scripts I've pushed to my project folder to the Docker image
ADD db/migration /flyway/sql

# Execute the command migrate
CMD [ "migrate" ]
