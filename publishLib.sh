#!/bin/bash +x

#sed -i -e "s/\/\/ classpath 'com.novoda:bintray-release:0.8.1'/classpath 'com.novoda:bintray-release:0.8.1'/" build.gradle

if [[ $# != 0 ]]; then
    RELEASE_TASK="bintrayUpload"
    echo "发布到 JCenter"
else
    RELEASE_TASK="uploadArchives"
    echo "发布到本地测试"
fi

# 发布 Library 到仓库
./gradlew :RouterCommon:clean :RouterCommon:build :RouterCommon:${RELEASE_TASK} -PpublishLib
#./gradlew :RouterCommon:clean :RouterCommon:build :RouterCommon:uploadArchives -PpublishLib

if [[ $# != 0 ]]; then
    sleep 10
fi

./gradlew :RouterApi:clean :RouterApi:build :RouterApi:${RELEASE_TASK} -PpublishLib
#./gradlew :RouterApi:clean :RouterApi:build :RouterApi:uploadArchives -PpublishLib
./gradlew :RouterCompiler:clean :RouterCompiler:build :RouterCompiler:${RELEASE_TASK} -PpublishLib
#./gradlew :RouterCompiler:clean :RouterCompiler:build :RouterCompiler:uploadArchives -PpublishLib

if [[ $# != 0 ]]; then
    sleep 10
fi

sed -i -e "s/DEBUG_ROUTER_APT=true/DEBUG_ROUTER_APT=false/" gradle.properties
sed -i -e "s/UPLOAD_ROUTER_PLUGIN=false/UPLOAD_ROUTER_PLUGIN=true/" gradle.properties
./gradlew :RouterPlugin:clean :RouterPlugin:build :RouterPlugin:${RELEASE_TASK} -PpublishLib
#./gradlew :RouterPlugin:clean :RouterPlugin:build :RouterPlugin:uploadArchives -PpublishLib

sed -i -e "s/UPLOAD_ROUTER_PLUGIN=true/UPLOAD_ROUTER_PLUGIN=false/" gradle.properties

if [[ $# != 0 ]]; then
    sleep 10
fi

# 打包并启动 demo
./gradlew clean assembleDebug
adb install -r demo/demoapp/build/outputs/apk/debug/demoapp-debug.apk
adb shell am start -W -n com.sankuai.waimai.router.demo/com.sankuai.waimai.router.demo.basic.MainActivity

sed -i -e "s/DEBUG_ROUTER_APT=false/DEBUG_ROUTER_APT=true/" gradle.properties
