# Stage 1: Build
FROM node:24-bookworm-slim AS builder

# Set working directory
WORKDIR /app

# Copy dependencies and install
COPY package*.json ./
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the app
RUN npm run build

# Stage 2: Nginx runtime (non-root, unprivileged — runs as UID 101, listens on 8080)
FROM nginxinc/nginx-unprivileged:1.30-alpine-slim

ARG GIT_REVISION=unknown

LABEL org.opencontainers.image.title="Labs64.IO :: Checkout UI" \
      org.opencontainers.image.description="Whitelabel checkout frontend for the Labs64.IO Ecosystem" \
      org.opencontainers.image.url="https://labs64.io" \
      org.opencontainers.image.source="https://github.com/Labs64/labs64.io-checkout" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.revision="${GIT_REVISION}" \
      io.labs64.edition="ce"

# File operations need root; the base image drops back to the unprivileged user for runtime.
USER root

# Copy custom Nginx config (listens on 8080 — see nginx.conf)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Remove default static files and copy the built SPA
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist /usr/share/nginx/html

# Drop back to the non-root user for runtime
USER 101

# Expose non-privileged HTTP port
EXPOSE 8080

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
