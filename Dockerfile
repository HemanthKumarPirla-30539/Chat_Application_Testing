# Stage 1: Build the application
# We use a Maven image that already has Java and Maven installed to compile the project.
FROM maven:3.9.6-eclipse-temurin-21 AS builder
# Set the working directory inside this temporary container
WORKDIR /build
# Copy everything from your local project into the container's /build folder
COPY . /build
# Run the Maven command to clean up and package the application into a JAR file.
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal image for running the application
# We use a smaller Java Runtime Environment (JRE) image to keep the final image size small.
FROM eclipse-temurin:21-jre-alpine
# Set the working directory for the running application
WORKDIR /app
# Copy the built JAR file from the 'builder' stage into the final image's /app folder.
# The JAR file is located in /build/target/ and typically named based on your project.
# The '*' ensures we catch the exact filename.
COPY --from=builder /build/target/*.jar /app/service.jar

# Define the command that runs when the container starts.
ENTRYPOINT ["java", "-jar", "/app/service.jar"]