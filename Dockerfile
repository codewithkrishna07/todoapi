# ─────────────────────────────────────────
# STAGE 1: Build the jar file
# ─────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
# ↑ Use an image that has both Maven and Java 17
#   "AS build" gives this stage a name so Stage 2 can reference it

WORKDIR /app
# ↑ All following commands run inside /app folder

COPY pom.xml .
# ↑ Copy pom.xml first (before copying source code)
#   This is a performance trick — explained below

RUN mvn dependency:go-offline -B
# ↑ Download all Maven dependencies
#   Docker caches this layer — if pom.xml hasn't changed,
#   Docker skips this step on the next build (much faster)

COPY src ./src
# ↑ Now copy your actual source code

RUN mvn package -DskipTests
# ↑ Build the jar file
#   -DskipTests because CI already ran tests

# ─────────────────────────────────────────
# STAGE 2: Run the jar file
# ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
# ↑ Much smaller image — only Java Runtime, no Maven
#   alpine = tiny Linux distro, only 5MB
#   This makes your final image small (~150MB vs ~500MB)

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
# ↑ Copy ONLY the jar from Stage 1
#   All the Maven build tools are left behind
#   Final image stays small and clean

EXPOSE 8080
# ↑ Documents that this container listens on port 8080
#   Does not actually open the port — that happens at runtime

ENTRYPOINT ["java", "-jar", "app.jar"]
# ↑ The command that runs when the container starts
#   Equivalent to: java -jar app.jar