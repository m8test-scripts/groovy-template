def timer = $script.getThreads().getMain().getTimer()
def timeoutArgs = [false]
def immediateDone = [false]
def worker = [false]
timer.setImmediate({ params -> immediateDone[0] = true; $logger.info("thread-context:groovy:immediate") })
timer.setTimeout({ params ->
    if (params.length != 2 || params[0] != "payload" || params[1] != null)
        throw new RuntimeException("timeout arguments mismatch")
    timeoutArgs[0] = true
    $logger.info("thread-context:groovy:timeout")
}, 100L, "payload", null)
def cancelledTimeout = timer.setTimeout({ params -> throw new RuntimeException("cleared timeout ran") }, 1000L)
timer.clearTimeout(cancelledTimeout)
def cancelledImmediate = timer.setImmediate({ params -> throw new RuntimeException("cleared immediate ran") })
timer.clearImmediate(cancelledImmediate)
def count = [0]
def interval = [0]
interval[0] = timer.setInterval({ params ->
    if (params.length != 1 || params[0] != "tick") throw new RuntimeException("interval arguments mismatch")
    count[0]++
    if (count[0] == 2) {
        timer.clearInterval(interval[0])
        $logger.info("thread-context:groovy:interval")
    }
}, 75L, "tick")
$script.getThreads().onStart({ thread ->
    if (!thread.isCurrent() || thread.isMain() || !thread.isAlive() || thread.isInterrupted() || thread.getName() != "thread-context-worker")
        throw new RuntimeException("thread start context mismatch")
    $logger.info("thread-context:groovy:thread-start")
})
$script.getThreads().onExit({ thread ->
    if (!thread.isCurrent() || thread.isMain() || !thread.isAlive() || thread.isInterrupted() || thread.getName() != "thread-context-worker")
        throw new RuntimeException("thread exit context mismatch")
    $logger.info("thread-context:groovy:thread-exit")
})
$script.getThreads().start("thread-context-worker", { thread -> worker[0] = true; $logger.info("thread-context:groovy:thread") })
timer.setTimeout({ params ->
    if (!timeoutArgs[0] || !immediateDone[0] || !worker[0] || count[0] != 2)
        throw new RuntimeException("timer callback state mismatch")
    $logger.info("thread-context:groovy:verified")
}, 500L)
