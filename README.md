# [M8Test](https://dev-docs.m8test.com) Groovy Gradle 模板

> 如有疑问，请加入我们的官方 QQ 群：[749248182](https://qm.qq.com/q/jVVADJCAI8) 或 QQ
> 频道：[m8testofficial](https://pd.qq.com/s/8pidhywcy)

---

## 📦 Gradle 任务说明

### 任务分组

1. **`m8test-build`**  
   M8Test构建相关任务组，包含`buildGroovy`（构建Groovy项目）、`runGroovy`（运行项目）等任务。

2. **`m8test-code-completion`**  
   M8Test代码补全相关任务组，包含`generateGroovyGlobalVariables`（生成全局变量代码补全文件）等任务。

3. **`m8test-developmenet-environment`**  
   M8Test开发环境相关任务组，包含`installDevelopmentEnvironment`（安装开发环境）、`installGroovyPlugin`（安装语言插件）等任务。

4. **`m8test-download`**  
   M8Test资源下载相关任务组，包含`downloadGroovyCodeTemplate`（下载示例代码模板）、`downloadGroovyDocs`（下载文档）等任务。

> 实际开发中主要关注构建任务和代码补全任务即可。

### 常用任务

| 任务名称                            | 任务分组                     | 功能描述                                                           |
|---------------------------------|--------------------------|----------------------------------------------------------------|
| `generateGroovyGlobalVariables` | `m8test-code-completion` | 生成Groovy全局变量代码补全文件，提供IDE代码提示功能。需连接安卓设备，建议编写代码前执行一次，依赖更新后需重新执行。 |
| `buildGroovy`                   | `m8test-build`           | 构建M8Test Groovy脚本项目源码（不执行），构建结果位于`build/project`目录。            |
| `runGroovy`                     | `m8test-build`           | 构建并将项目推送到已连接的安卓设备上运行。                                          |
| `buildGroovyApk`                | `m8test-build`           | 将M8Test Groovy脚本项目打包成APK文件。                                    |

---

## 🚀 执行任务的方法

### 方法一：使用快捷搜索

1. 双击`Ctrl`键打开全局搜索框
2. 输入`gradle 任务名`（如`gradle runGroovy`）
3. 按回车执行

![方法一示例](images/1.png)

### 方法二：通过Gradle面板

1. 点击IDE右侧Gradle面板图标
2. 依次展开`groovy` > `Tasks` > `m8test`
3. 双击需要执行的任务

![方法二示例](images/2.png)

### 方法三：使用终端命令

1. 按`Alt + F12`打开终端
2. 输入命令`./gradlew 任务名`（如`./gradlew runGroovy`）
3. 按回车执行

![方法三示例](images/3.png)

---

## ⚙️ 配置脚本项目

脚本项目配置位于`groovy/build.gradle.kts`文件，可根据需要修改设备IP、ADB端口等参数，文件内包含详细注释可供参考。

---

## 🛠️ 项目开发流程

1. **生成全局变量**  
   执行`generateGroovyGlobalVariables`任务生成代码补全文件，提供IDE代码提示功能。

2. **连接日志服务**  
   按`Alt + T`，依次选择`M8Test` > `连接日志服务`。

3. **编写并运行脚本**  
   编写代码保存后，执行`runGroovy`任务在安卓设备上运行项目（需提前开启ADB调试），运行日志可在M8Test日志面板查看。

   `runGroovy` 会先生成并推送 `build/project`（BuildProjectSource），等待 BuildScript 完成后，再启动独立的 Groovy ProjectScript。模板声明的 Extension 版本必须与 Groovy 工程最近一次 `debugReinstallExtension` 写入 `~/.m8test/cache/apk/extension` 的版本一致。

4. **打包Apk**  
   所有的脚本开发工作都完整后, 如果你需要打包成独立的apk可以执行 `buildGroovyApk` 任务。

## BuildProjectSource 与外置 Groovy Extension

Groovy 模板使用 `isPluginsBundled = false` 控制常规项目构建时的依赖复制。APK 的 Extension 打包则由 `apkBuild.bundleExtensions` 单独控制，默认启用。
`com.m8test.capability.language.groovy` 是运行时 capability ID；它与 Extension
ID 的作用不同，不能互换。

`runGroovy` 会把项目转换为 BuildProjectSource，执行 `settings.groovy`、
`init.build.groovy` 和 `build.groovy`，再生成 `project.config.json`。Build 完成后
才启动独立的 Groovy ProjectScript。入口使用当前 Compose bridge，side 使用当前
事件 scope/payload API；脚本日志统一通过 `$logger` 输出六级日志。

`.groovy` 是 Groovy 文件类型 descriptor；最终 SPA 根目录直接包含
`project.config.json`、`src/`、`init/`、`lib/`、`res/`、`webview/` 和可选的
`extension/`，`build/spa/files` 只是打包 staging 目录。

Groovy 模板的主脚本、side 脚本和工具类都使用 `$logger`/`Logger`，不再依赖旧的
`$console`、`Console` 或 `.log` 入口。


默认会把项目声明的 Extension 依赖及其传递依赖一并嵌入 APK，安装后无需联网即可运行。若要减小 APK 体积，可在 `apkBuild` 中设置 `bundleExtensions = false`；此时运行环境必须已安装所需 Extension，或能够从配置的 Maven 仓库下载。

## APK 启动策略

应用身份在 `projectConfig` 中设置：`name` 是显示名称，`packageName` 是唯一包名，`versionName` 与 `versionCode` 控制版本，`logo` 指向 `res` 下的图标。构建生成的脚本配置会将这些值应用到 APK。`appName`、`appVersionName` 和 `appVersionCode` Gradle 属性可覆盖名称和版本。默认安装后自动运行脚本。需要由用户点击运行时，在语言配置块中选择手动启动：

```kotlin
m8testGroovy {
    apkBuild {
        launchMode = "manual"
    }
}
```

`launchMode` 只接受 `"automatic"` 或 `"manual"`。手动模式显示启动页，由用户决定何时运行。运行环境入口默认随 `launchMode` 显示：手动启动时显示，自动启动时隐藏。可在 `apkBuild` 中设置 `runtimeEnvironmentVisible = true` 或 `false` 显式覆盖；启动失败时仍提供恢复入口。
## 发布签名

APK 默认使用开发签名。发布时在 `m8testGroovy { apkBuild { ... } }` 中设置 `signingMode = "release"`、密钥库路径和别名，并通过 `providers.environmentVariable("M8TEST_RELEASE_STORE_PASSWORD")` 与 `providers.environmentVariable("M8TEST_RELEASE_KEY_PASSWORD")` 提供密码。发布 APK 在本机签名并验证，输出到 `build/apk/`。密钥库和密码不会进入生成源码或设备项目；缺少发布签名配置时构建会失败。请把密钥库保存在受控位置并排除在版本控制之外，JDK `keytool` 可用于生成、导入和导出密钥材料。
