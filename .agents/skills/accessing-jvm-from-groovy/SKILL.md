---
name: accessing-jvm-from-groovy
description: 从 M8Test Groovy 脚本访问 JVM 类、调用成员、传递 callback 或使用宿主 wrappers 时使用。
---

# 从 Groovy 访问 JVM

Groovy 脚本在 JVM 上运行。先确认目标依赖已进入当前脚本 classloader；声明文件存在不代表运行时类可见。公共的宿主值/wrapper 语义见 `using-host-values-and-wrappers`。

```groovy
def builder = new java.lang.StringBuilder("M8Test")
builder.append(" Groovy")
assert builder.toString() == "M8Test Groovy"

def raw = $arrays.intArrayOf(3, 1, 2)
def values = $objectWrappers.wrapIntArray(raw)
values.set(0, 7)
assert values.getSize() == 3 && values.contains(7)
```

Groovy Closure 可传给匹配的 SAM 或 Kotlin function type，但 callback 参数、返回值与调用时机按目标签名验证。重载、primitive/boxed、null、数组、集合、varargs 和异常行为不能从 JavaScript 或其他语言推断。

修改互操作示例时更新 Groovy JVM interop Test Control 断言，并在目标设备验证；构建/源码生成成功不证明 Groovy 运行时转换成功。
