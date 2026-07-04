FROM eclipse-temurin:25-alpine

# Define build arguments
ARG USER_ID=1064
ARG GROUP_ID=1064
ARG USER_NAME=l64user
ARG GROUP_NAME=l64group

# Create group and user with the specified IDs
RUN addgroup -g ${GROUP_ID} ${GROUP_NAME} && \
    adduser -D -u ${USER_ID} -G ${GROUP_NAME} ${USER_NAME}

# OpenTelemetry Java Agent — bundled but inert. Activated at deploy time via
# JAVA_TOOL_OPTIONS=-javaagent:/otel/opentelemetry-javaagent.jar (observability is an
# infrastructure concern; the same image runs with and without it). See OBSERVABILITY.md.
ARG OTEL_JAVAAGENT_VERSION=2.29.0
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVAAGENT_VERSION}/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
RUN chmod 444 /otel/opentelemetry-javaagent.jar

# Set working directory
WORKDIR /home/${USER_NAME}

# Copy the jar file with appropriate ownership
COPY --chown=${USER_NAME}:${GROUP_NAME} target/*.jar /home/${USER_NAME}/app.jar

# Set user context
USER ${USER_NAME}

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -qO- http://127.0.0.1:8080/actuator/health || exit 1

# Default command
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /home/l64user/app.jar"]
