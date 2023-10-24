FROM ubuntu:latest

WORKDIR /app

RUN apt-get update && apt-get install -y \
    openjdk-19-jdk-headless \
    sqlite3 \
    && rm -rf /var/lib/apt/lists/*

COPY ./appcesible .

ENV PORT=8080

EXPOSE 8080