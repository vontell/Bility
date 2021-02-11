FROM openjdk:8

ENV ANDROID_SDK_TOOLS_VERSION="4333796" \
    ANDROID_NDK_VERSION="17b" \
    APP_ALLOW_MISSING_DEPS=true

ENV SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_TOOLS_VERSION}.zip" \
    NDK_URL="http://dl.google.com/android/repository/android-ndk-r${ANDROID_NDK_VERSION}-linux-x86_64.zip" \
    ANDROID_HOME="/usr/local/android-sdk" \
    ANDROID_NDK="/usr/local/android-ndk-r$ANDROID_NDK_VERSION" \
    TEMP_HOME="/usr/local" \
    MINICAP_HOME="/usr/local/minicap" \
    MINICAP_REPO="https://github.com/openstf/minicap.git"

# Install OS tools
RUN dpkg --add-architecture i386 && apt-get update -yqq && apt-get install -y \
    make

# Download Android SDK and NDK
RUN mkdir "$ANDROID_HOME" "$ANDROID_NDK" .android \
    && echo '### User Sources for Android SDK Manager' > ".android/repositories.cfg" \
    # First download the SDK
    && echo "Downloading Android SDK..." \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip $SDK_URL \
    && unzip sdk.zip \
    && rm sdk.zip \
    && yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses \
    # Then download the NDK
    && echo "Downloading Android NDK..." \
    && cd "$ANDROID_NDK" \
    && curl -o ndk.zip $NDK_URL \
    && unzip ndk.zip -d "$TEMP_HOME" \
    && rm ndk.zip

# Install Android Build Tool and Libraries
RUN $ANDROID_HOME/tools/bin/sdkmanager --update
RUN $ANDROID_HOME/tools/bin/sdkmanager \
    # First platforms
    "platforms;android-27" \
    "platforms;android-26" \
    "platforms;android-25" \
    "platform-tools" \
    # Then build tools
    "build-tools;27.0.3" "build-tools;27.0.2" "build-tools;27.0.1" \
    "build-tools;26.0.2" "build-tools;26.0.1" "build-tools;26.0.0" \
    "build-tools;25.0.3" "build-tools;25.0.2" \
    "build-tools;25.0.1" "build-tools;25.0.0" \
    # Then emulator tools
    "emulator"

# Now install Minicap
RUN mkdir "$MINICAP_HOME" \
    && echo "Downloading and installing Minicap" \
    && cd "$MINICAP_HOME" \
    && git clone $MINICAP_REPO . \
    && git submodule init \
    && git submodule update \
    && $ANDROID_NDK/ndk-build -C .

RUN mkdir /webserver
WORKDIR /webserver