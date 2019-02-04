# echo Starting Bility services

source ~/.bility
MINICAP_REPO=git@github.com:openstf/minicap.git

# Export for other scripts
export PATH=$PATH:$NDK:$ADB

# First, download dependencies
if [ ! -d "dependencies/" ]; then
  # Control will enter here if $DIRECTORY doesn't exist.
  mkdir dependencies
fi

if [ ! -d "dependencies/minicap" ]; then
  # Control will enter here if $DIRECTORY doesn't exist.
  echo Installing Minicap... this may take a minute...
  cd dependencies
  git clone $MINICAP_REPO minicap
  cd minicap
  git submodule init
  git submodule update
  ndk-build -C . > log.txt
  cd ../../
  echo Minicap build finished!
else
  echo Minicap already installed
fi

# Startup the emulator if not already started
echo Starting up emulator...
STARTEMCOMM="$EM -avd $EM_TO_USE -netdelay none -netspeed full -verbose -no-window"
$EM -avd $EM_TO_USE -netdelay none -netspeed full & > log.txt
# possible -no-window

trap 'adb kill-server; echo Killed ABD server' 0

A=$(adb shell getprop sys.boot_completed | tr -d '\r')
while [ "$A" != "1" ]; do
        printf '.'
        sleep 2
        A=$(adb shell getprop sys.boot_completed | tr -d '\r')
done

echo Emulator ready!

# Start running the minicap service
echo Starting up minicap...

cd dependencies/minicap/
./run.sh -P 1080x1920@810x1440/0 & > log.txt

echo Minicap started! Forwarding feed...
adb forward tcp:1717 localabstract:minicap > log.txt

cd example/
echo Starting minicap casting server...
node app.js &

until $(curl --output /dev/null --silent --head --fail localhost:9002); do
    printf '.'
    sleep 1
done

echo Minicap casting server started

cd ../../../

echo Minicap started

# Start running the frontend service
echo Starting Bility frontend site
cd bility-frontend
npm install > log.txt
npm run dev & > log.txt

trap 'kill -9 $(lsof -t -i:3000); echo Killed Bility frontend server' 0

until $(curl --output /dev/null --silent --head --fail localhost:3000); do
    printf '.'
    sleep 1
done

echo Bility frontend started!
cd ..

# Starting ktor test server
echo Starting Android test server
cd AndroidServer
./gradlew run & > log.txt

trap 'kill -9 $(lsof -t -i:8080); echo Killed Bility backend server' 0

until $(curl --output /dev/null --silent --head --fail localhost:8080); do
    printf '.'
    sleep 1
done

echo Press CTRL-C to quit...