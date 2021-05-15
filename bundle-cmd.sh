#!/bin/bash +x

rm -f testApks/my_app.apks

gradlew bundleDebug

#--local-testing
#--mode=universal

java -jar buildTools/bundletool-all-1.6.0.jar build-apks --mode=universal --bundle=demo/demoapp/build/outputs/bundle/debug/demoapp-debug.aab --output=testApks/my_app.apks --ks=demo/demoapp/keystore/TestKeyStore.jks --ks-pass=pass:testkey --ks-key-alias=testkey --key-pass=pass:testkey

java -jar buildTools/bundletool-all-1.6.0.jar install-apks --apks=testApks/my_app.apks

adb shell am start -n "com.sankuai.waimai.router.demo/com.sankuai.waimai.router.demo.basic.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
