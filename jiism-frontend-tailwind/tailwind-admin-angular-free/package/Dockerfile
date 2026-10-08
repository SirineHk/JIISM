# ===== Build =====
FROM node:22-alpine AS build
WORKDIR /build
COPY package*.json ./
RUN npm ci
COPY . .
RUN node node_modules/@angular/cli/bin/ng.js build --configuration production

# ===== Runtime =====
FROM nginxinc/nginx-unprivileged:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /build/dist/TailwindAdmin/browser /usr/share/nginx/html

EXPOSE 8080