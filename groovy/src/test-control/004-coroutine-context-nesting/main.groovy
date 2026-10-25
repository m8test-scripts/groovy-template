def scope = $coroutines.newScope(null)
def timer = $script.getThreads().getMain().getTimer()
def captured = "groovy-nested"
def otherTaskProgress = [false]
timer.setTimeout({ params -> otherTaskProgress[0] = true; $logger.info("nest:groovy:other-task-progressed"); null }, 50L)
scope.launch({ context -> context.setDispatcher({ dispatchers -> dispatchers.getIO() }); null }, null, { outer ->
    if (captured != "groovy-nested") throw new RuntimeException("outer capture unavailable")
    $logger.info("nest:groovy:outer-started")
    scope.launch({ context -> context.setDispatcher({ dispatchers -> dispatchers.getScriptMain() }); null }, null, { inner ->
        if (captured != "groovy-nested") throw new RuntimeException("nested capture unavailable")
        $logger.info("nest:groovy:inner-callback")
        null
    })
    if (captured != "groovy-nested") throw new RuntimeException("outer capture not restored")
    $logger.info("nest:groovy:outer-resumed")
    null
})
scope.delay(200L).onSuccess({ value ->
    if (!otherTaskProgress[0] || captured != "groovy-nested") throw new RuntimeException("delay state mismatch")
    $logger.info("nest:groovy:delay-resumed")
})
timer.setTimeout({ params -> $logger.info("nest:groovy:gate"); scope.cancel("done"); null }, 1500L)
