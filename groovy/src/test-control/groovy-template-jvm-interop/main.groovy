def builder = new java.lang.StringBuilder("M8Test")
builder.append(" Groovy")
if (builder.toString() != "M8Test Groovy") throw new AssertionError("StringBuilder")
def raw = $arrays.intArrayOf(3, 1, 2)
def values = $objectWrappers.wrapIntArray(raw)
if (values.getSize() != 3) throw new AssertionError("wrapped size")
values.set(0, 7)
if (!values.contains(7)) throw new AssertionError("wrapped array")
def callbackCalled = false
$logger.withContext { context -> callbackCalled = context != null }
if (!callbackCalled) throw new AssertionError("Kotlin function callback")
def list = $iterables.listOf("first", "second")
if (list.get(1) != "second") throw new AssertionError("host List factory")
$logger.info("groovy-template-jvm-interop:passed")
