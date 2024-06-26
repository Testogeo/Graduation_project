FROM node:latest
WORKDIR /opt/app
COPY ./simulator .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999

