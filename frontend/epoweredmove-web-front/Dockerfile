FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/* && rm -rf /etc/nginx/nginx.conf && rm -rf /etc/nginx/default.conf
COPY ./nginx-custom.conf /etc/nginx/nginx.conf
COPY ./default-custom.conf /etc/nginx/default.conf
COPY ./dist/epoweredmove-web-front ./usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]