FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
# O postinstall (quasar prepare) precisa do código-fonte; roda no build abaixo.
RUN npm ci --ignore-scripts

COPY . .
RUN npm run build

FROM nginx:1.27-alpine AS production

COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/spa /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
