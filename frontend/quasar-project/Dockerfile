# syntax=docker/dockerfile:1.7
FROM node:24-alpine AS build

WORKDIR /app

COPY . .
RUN --mount=type=secret,id=corporate_ca,target=/run/secrets/corporate-ca.pem,required=false \
    if [ -s /run/secrets/corporate-ca.pem ]; then \
        NODE_EXTRA_CA_CERTS=/run/secrets/corporate-ca.pem npm ci; \
    else \
        npm ci; \
    fi

ARG VITE_API_URL=/api
ENV VITE_API_URL=${VITE_API_URL}
RUN npm run build

FROM nginx:1.29-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/spa /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --quiet --spider http://127.0.0.1/ || exit 1
