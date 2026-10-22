plugins {
    // 使用 m8test groovy gradle 插件, 表示这是一个 m8test groovy 项目
    alias(m8test.plugins.m8test.gradle.script.groovy)
}
// 在gradle.properties中定义的变量, 用于指定m8test版本
val m8testVersion: String by project
val composeExtensionVersion: String by project
// 在gradle.properties中定义的变量, 用于指定无障碍插件版本
val accessibilityVersion: String by project
// 在gradle.properties中定义的变量, 用于指定图色插件版本
val opencvVersion: String by project
// 在gradle.properties中定义的变量, 用于指定文字识别插件版本
val ocrVersion: String by project
// 语言插件版本
val languagePluginVersion: String by project
// m8testGroovy 闭包用于配置 m8test groovy 项目
m8testGroovy {
    apkBuild {
        isEnabled = true
        launchMode = "automatic"
        signingMode = providers.gradleProperty("m8test.apkSigningMode").orElse("development").get()
        releaseKeyStore = providers.gradleProperty("m8test.releaseKeyStore").orNull?.let(::file)
        releaseKeyAlias = providers.gradleProperty("m8test.releaseKeyAlias").orNull
        releaseStorePassword = providers.gradleProperty("m8test.releaseStorePasswordEnv").orNull?.let(providers::environmentVariable)
        releaseKeyPassword = providers.gradleProperty("m8test.releaseKeyPasswordEnv").orNull?.let(providers::environmentVariable)
    }
    // 可选：显式指定宿主工具路径；未配置时从 Android SDK、PATH 和环境变量自动发现
//    properties["ADB_PATH"] = "/opt/android-sdk/platform-tools/adb"
//    properties["SCRCPY_PATH"] = "/usr/bin/scrcpy"
    // debugger 闭包用于配置调试器(安卓设备)的信息
    debugger {
        adbDeviceSerial = providers.gradleProperty("adbDeviceSerial").orNull
        // adb 设备序列, 如果您通过数据线连接则需要填写adb设备序列, 例如 emulator-5554, 如果设置了此属性的话并且不为null的话 adbPort 和 deviceIp 会被忽略
//        adbDeviceSerial = "emulator-5554"
        // 安卓设备的ip地址, 这里是局域网的ip地址, 如果是云手机的话可能需要内网穿透, 这里的ip就需要填写有公网ip的服务器的ip地址
        deviceIp = "192.168.31.13"
        // M8Test脚本项目根路径, 电脑端构建好的项目会推送到该路径对应的目录
        workspace = "/sdcard/M8Test/project"
        // 安卓设备的adb调试端口, 如果是云手机的话就需要填写内网穿透时映射的adb端口
        adbPort = 5555
        // 安卓设备调试器服务启动的端口
        debuggerPort = 5354
        // M8Test 开发工具配置闭包
        developmentKitConfig {
            // 开发工具的包名, 使用默认就好
            packageName = "com.m8test.app.developmentkit"
            // 开发工具版本
            version = m8testVersion
            localApk = providers.gradleProperty("developmentKitApk").orNull?.let(::file)
            // api文档根目录
            docsRoot = "https://sdk-docs.m8test.com/sdk"
        }
    }
    // 脚本项目配置闭包
    projectConfig {
        requireComponent("com.m8test.extension", "compose", composeExtensionVersion)
        // 是否将插件(语言/组件)打包进脚项目中, 如果不将插件打包进脚本项目中，那么运行时会从网络下载需要的语言以及组件，但是需要确保能正常访问外网，这种方式打包速度快，推荐开发时使用这种方式;如果是打包成apk，推荐将插件打包进脚项目中, 这样交付给用户之后就可以直接使用，而无需从网络下载插件, 但是这样安装包会更大
        isPluginsBundled = false
        // 相对于res目录的资源文件,会打包到apk的assets目录下
        // 这个文件表示apk启动时自动执行的脚本, 只能是true或者false
        addAsset("auto_start_spa.txt")
        // 这个文件表示脚本项目官网,可以是一个网址
        addAsset("official_website.txt")
        // 这个文件表示脚本项目版权信息
        addAsset("copyright.txt")
        // 脚本项目入口文件, 这里是相对于src目录的路径
        entry = "com/example/script/primary.groovy"
        // 辅助脚本，会在入口文件中自动启动这些脚本, 需要是相对于src目录的路径
        sides = mutableListOf("com/example/script/side1.groovy", "com/example/script/side2.groovy")
        // 脚本项目包名, 和java包名规则一样
        name = providers.gradleProperty("appName").orElse("Groovy Script").get()
        packageName = "com.example.script.groovy"
        versionName = providers.gradleProperty("appVersionName").orElse("1.0.1").get()
        versionCode = providers.gradleProperty("appVersionCode").map { it.toInt() }.orElse(1).get()
        // 脚本项目logo配置, 这里是相对于 res 目录的路径
        logo = "logo.png"
        // 在脚本项目中引用 M8Test 组件
//        requireComponent {
//            // 无障碍组件
//            name = "YumiMiyamotoAccessibility"
//            // 组件版本
//            version = accessibilityVersion
//            // 组件下载地址
//            url =
//                "https://github.com/YumiMiyamoto/accessibility-release/releases/download/Accessibility-$version/com.m8test.accessibility-release_$version.apk"
//            // api文档根目录
//            docsRoot = "https://yumimiyamoto.github.io/accessibility-release/app"
//        }
//        // 在脚本项目中引用 M8Test 组件
//        requireComponent {
//            // ocr组件
//            name = "YumiMiyamotoOcr"
//            // 组件版本
//            version = ocrVersion
//            // 组件下载地址
//            url =
//                "https://github.com/YumiMiyamoto/ocr-release/releases/download/ocr-$version/com.m8test.ocr-release_$version.apk"
//            // api文档根目录
//            docsRoot = "https://yumimiyamoto.github.io/ocr-release/ocr"
//        }
//        // 在脚本项目中引用 M8Test 组件
//        requireComponent {
//            // opencv 图色组件
//            name = "YumiMiyamotoOpencv"
//            // 组件版本
//            version = opencvVersion
//            // 组件下载地址
//            url =
//                "https://github.com/YumiMiyamoto/opencv-release/releases/download/opencv-$version/com.m8test.image-release_$version.apk"
//            // api文档根目录
//            docsRoot = "https://yumimiyamoto.github.io/opencv-release/image"
//        }
    }
    // 工具相关配置
    tools {
        // 测试文件的配置
        test {
            // key 为需要测试的文件路径，相对于模块根目录, value 为测试文件在设备中的路径
            paths["build/test/test.groovy"] = "/sdcard/M8Test/test.groovy"
            paths["build/test/test1.groovy"] = "/sdcard/M8Test/test1.groovy"
        }
        // 端口转发，可以将安卓设备端的端口映射到电脑端
        portForwarding {
            // 将安卓设备的5001端口映射到本地的5000端口, 可以通过 localhost:5000 访问到安卓设备端的5001端口
            add(5000, 5001)
        }
    }
    // 构建脚本配置, 一般不会用到
    buildScriptConfig {
//        // 在构架脚本中引入插件
//        requirePlugin {
//            name = "Debugger"
//            version = "0.1.2"
//            url = "https://github.com/m8test/debugger/releases/download/$version/debugger-release-unsigned.apk"
//        }
    }
    settingsConfig {
        language {
            groupId = "com.m8test.language"
            artifactId = "groovy"
            entryId = "groovy"
            version = languagePluginVersion
        }
    }
}
