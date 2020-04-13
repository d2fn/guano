FROM openjdk:8 as guano-package-builder

WORKDIR /usr/app/guano

COPY . .

RUN apt-get update; \
    apt-get install --yes maven; \
    mvn clean package


FROM java:8-jdk-alpine

RUN mkdir /usr/app

COPY --from=guano-package-builder /usr/app/guano/target/guano-0.1a.jar /usr/app
COPY ./docker-entrypoint.sh /usr/app

WORKDIR /usr/app

ENTRYPOINT [ "./docker-entrypoint.sh"]
