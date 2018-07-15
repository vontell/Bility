# Bility

## Setup Instructions

0) QUICKSTART

Simply run the docker-compose file to startup all services:

```
docker-compose up -d
```

To see the status of each container, run the following:

```
docker-compose ps
```

To stop the network of containers, run the following:

```
docker-compose stop
```

1) Create and Start MongoDB Container

The central database for the Bility system is MongoDB, which holds build configurations, user information, build history, etc. The database server is a vanilla MongoDB Docker container, which can be built and run using the commands below:

```
docker pull mongo
docker run --name mongo-dev -d -p 27017:27017 mongo
```

You can now start a shell within this container, or view the logs, with these following commands:

```
docker exec -it mongo-dev bash
docker logs mongo-dev
```

See more here: https://hub.docker.com/_/mongo/

2) Create and Start Maven Container

In order to keep the Bility library private, we host is as a Maven artifict locally, which can be accessed internally. The Maven repo is hosted using the Artificatory dependency management system. This can be started with the commands below:

```
docker pull mattgruter/artifactory
docker run --name maven-local -d -p 8146:8080 mattgruter/artifactory
```

You can now view the logs, with the following command:

```
docker logs maven-local
```

You can also view the Artifactory homepage at http://localhost:8146/ (this will be useful for checking that your artifact is successfully loaded afterwards)

More information can be found here: https://hub.docker.com/r/mattgruter/artifactory/

3) Upload Bility Library to Maven Container

We now want to push the Bility libary to our local maven server, so that we can access it within any tested Android apps. Do this with the following commands:

```
cd BilityPrivate/
./gradlew :ama:uploadArchives
cd ..
```

There should now be an .aar file at http://localhost:8146/artifactory/webapp/browserepo.html within the `libs-release-local` dropdown.

3) Start the OCR service

```
docker pull tesseractshadow/tesseract4re
```

4) Start the WebServer

The webserver is written in Kotlin using Gradle and the Ktor library. Build and start the Docker container for this webserver with the following commands (remove the `it` to run in the background):

```
cd BilityWebServer/
./gradlew build
docker build -t bility-k-webserver .
docker run --name bility-k-webserver -d -p 8080:8080 --rm bility-k-webserver
```