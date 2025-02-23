# Use OpenJDK 8 as the base image
FROM eclipse-temurin:8

# Set working directory
WORKDIR /app

# Copy the project files
COPY . .

# Give execution permission to Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build

# Run the application
CMD ["java", "-jar", "/app/build/libs/TicTacToeServer-v0.1.jar"]