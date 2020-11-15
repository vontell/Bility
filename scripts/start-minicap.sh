echo Starting up minicap...

cd ../dependencies/minicap/
./run.sh -P 1080x1920@810x1440/0 >> ../../log.txt &

echo Minicap started! Forwarding feed...
adb forward tcp:1717 localabstract:minicap >> ../../log.txt

cd example/
echo Starting minicap casting server...
node app.js & >> ../../../log.txt

until $(curl --output /dev/null --silent --head --fail localhost:9002); do
    printf '.'
    sleep 1
done

echo Minicap casting server started