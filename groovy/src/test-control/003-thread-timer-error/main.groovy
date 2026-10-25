def timer = $script.getThreads().getMain().getTimer()
timer.setImmediate({ params ->
    $logger.info("thread-context:groovy:timer-error")
    throw new RuntimeException("expected timer callback failure")
})
