def scope = $coroutines.newScope(null)
def timer = $script.getThreads().getMain().getTimer()
scope.launch({ context ->
    context.setName("script-main")
    context.setDispatcher({ dispatchers -> dispatchers.getScriptMain() })
    if (context.getName() != "script-main") throw new RuntimeException("coroutine context name mismatch")
}, null, { s ->
    if (!$script.getThreads().getCurrent().isCurrent()) throw new RuntimeException("script-main dispatcher mismatch")
    $logger.info("coroutine:groovy:script-main-verified")
    null
})
def capturedScriptValue = "groovy-captured"
def otherTaskProgress = [false]
timer.setTimeout({ params -> otherTaskProgress[0] = true; $logger.info("coroutine:groovy:other-task-progressed"); null }, 50L)
scope.launch({ context -> context.setDispatcher({ dispatchers -> dispatchers.getIO() }) }, null, { s ->
    if (capturedScriptValue != "groovy-captured") throw new RuntimeException("captured script value mismatch")
    $logger.info("coroutine:groovy:io-captured-value-verified")
    null
})
scope.delay(200L).onSuccess({ value ->
    if (capturedScriptValue != "groovy-captured") throw new RuntimeException("delayed script value mismatch")
    if (!otherTaskProgress[0]) throw new RuntimeException("other task did not progress during delay")
    $logger.info("coroutine:groovy:delayed-captured-value-verified")
})
scope.launch(null, null, { s -> $logger.info("coroutine:groovy:launch") })
scope._async(null, null, { s -> $logger.info("coroutine:groovy:async"); 7 }).onSuccess({ value ->
    if (value != 7) throw new RuntimeException("async value mismatch: " + value)
    $logger.info("coroutine:groovy:async-success")
})
scope.withContext({ context -> }, { s -> $logger.info("coroutine:groovy:with-context"); 9 })
$coroutines.getFlows().of(1, 2).map({ s, value -> $logger.info("coroutine:groovy:flow:" + value); s.resolve(value * 2) })
    .toList(scope).onSuccess({ values ->
        if (values.size() != 2 || values.get(0).toString() != "2" || values.get(1).toString() != "4")
            throw new RuntimeException("flow mapped values mismatch")
        $logger.info("coroutine:groovy:flow-complete")
    })
scope.delay(5000L).onComplete({ $logger.info("coroutine:groovy:cancelled-work-complete") })
timer.setTimeout({ params -> $logger.info("coroutine:groovy:gate"); scope.cancel("done") }, 1500L)
