# FROM openjdk:8-jre-alpine
# #COPY ./build/libs/bility-server.jar /root/bility-server.jar

# # Copy the folders needed to run the project

# # Install basic dependencies
# RUN apk add curl

# # Install Gradle
# WORKDIR /gradle
# RUN curl -L https://services.gradle.org/distributions/gradle-6.5.1-bin.zip -o gradle-6.5.1-bin.zip && \
#     unzip gradle-6.5.1-bin.zip && \
#     rm gradle-6.5.1-bin.zip
# ENV GRADLE_HOME=/gradle/gradle-6.5.1
# ENV PATH=$PATH:$GRADLE_HOME/bin
# RUN gradle --version

# COPY . /build

# WORKDIR /build
# ARG adsad
# RUN ls
# RUN gradle bility-server:build
# RUN ls

# CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "bility-server.jar"]


FROM openjdk:8-jre-alpine
COPY ./build/libs/bility-server-0.0.1.jar /root/bility-server.jar

WORKDIR /root

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "bility-server.jar"]