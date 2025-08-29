FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.25-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /tmp/dist
RUN set -e; \
    rm -rf /usr/share/nginx/html/*; \
    appdir=$(find /tmp/dist -maxdepth 1 -mindepth 1 -type d | head -n1); \
    if [ -d "$appdir/browser" ]; then \
      cp -r "$appdir/browser/." /usr/share/nginx/html/; \
    else \
      cp -r "$appdir/." /usr/share/nginx/html/; \
    fi; \
    rm -rf /tmp/dist
