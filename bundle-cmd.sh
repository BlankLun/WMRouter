#!/bin/bash +x

rm -f testApks/my_app.apks

java -jar buildTools/bundletool-all-1.6.0.jar build-apks --bundle=demo/demoapp/build/outputs/bundle/debug/demoapp-debug.aab --output=testApks/my_app.apks --ks=demo/demoapp/keystore/TestKeyStore.jks --ks-pass=pass:testkey --ks-key-alias=testkey --key-pass=pass:testkey --mode=universal

java -jar buildTools/bundletool-all-1.6.0.jar install-apks --apks=testApks/my_app.apks

