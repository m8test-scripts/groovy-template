---
name: accessing-jvm-from-groovy
description: 当 Groovy 脚本需要访问 JVM 类、调用 Java/Kotlin 成员或传递函数 callback 时使用。
---

# 从 Groovy 访问 JVM

Groovy 脚本在 JVM 上运行，可按 Groovy/Java 语法访问当前 classloader 可见的类。模板 primary.groovy 展示了 Groovy closure 作为 Kotlin callback 的用法。

## 类与对象

```groovy
def builder = new java.lang.StringBuilder("M8Test")
builder.append(" Groovy")
assert builder.toString() == "M8Test Groovy"
```

先确认依赖已加载到当前脚本 classloader。声明文件或代码补全存在不等于运行时类可见。方法重载、静态字段、嵌套类、异常等边界按目标语言的生成代码和实际设备行为验证。

## 函数 callback

Groovy Closure 按目标签名传给 SAM 或 Kotlin 函数类型。使用真实 API 调用和设备断言确认 callback 的参数、返回值及调用时机。

## 函数 callback 示例

Groovy Closure 可以按目标 Kotlin 函数类型传入 callback。模板 Test Control 通过 Logger.withContext 实际验证 callback 被调用并获得参数：

```groovy
boolean[] called = [false] as boolean[]
$logger.withContext { context -> called[0] = context != null }
assert called[0]
```

## 类型转换边界

Groovy 动态值转换、null、数值、varargs 与重载由 Groovy 和目标 JVM 签名共同决定；不套用 Rhino 或其他解释器的结论。

逐项记录已测试的 primitive/boxed、字符串、nullable、数组、集合、varargs、重载和异常行为。未经执行验证的转换写为待验证，不根据其他脚本语言推断。

## 验证

本模板的 groovy-template-jvm-interop Test Control 场景会实际调用 JVM StringBuilder，并验证宿主数组工厂和 wrapper。运行当前项目支持的选中 Test Control 任务并检查 summary 中该 requestId 为 passed；若当前模板没有注册相应任务，使用模板设备运行任务并确认成功日志。Gradle 使用 -q。
