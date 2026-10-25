def a=$coroutines.newScope(null)
def b=$coroutines.newScope(null)
def c=$coroutines.newScope(null)
def d=$coroutines.newScope(null)
def e=$coroutines.newScope(null)
def timer=$script.getThreads().getMain().getTimer()
a.withTimeout(75L,{s->s.delay(1000L)}).catchError({s,err->s.resolve("fallback")}).onError({err->$logger.info("coroutine:groovy:timeout-cancellation-preserved")})
b.withTimeoutOrNull(75L,{s->s.delay(1000L)}).onSuccess({v->if(v!=null)throw new RuntimeException("expected null");$logger.info("coroutine:groovy:timeout-or-null-ok")}).onError({err->throw new RuntimeException("timeoutOrNull failed: "+err)}).onComplete({$logger.info("coroutine:groovy:timeout-or-null-complete")})
def timed=[0]
c.loopWithTimeout(75L,{s->timed[0]++;s.delay(1000L).then({inner,v->inner.resolve(true)})}).onError({err->if(timed[0]!=1)throw new RuntimeException("timed loop count");$logger.info("coroutine:groovy:loop-timeout-cancelled-wait")})
def count=[0]
d.loop({s->count[0]++;$logger.info("coroutine:groovy:loop-iteration:"+count[0]);s.resolve(count[0]<3)}).onComplete({if(count[0]!=3)throw new RuntimeException("loop count");$logger.info("coroutine:groovy:loop-completed-count:3")})
e.withTimeoutOrNull(500L,{s->s.delay(25L).then({inner,v->inner.resolve(23)})}).onSuccess({v->if(v==null||v.toString()!="23")throw new RuntimeException("composed value");$logger.info("coroutine:groovy:composed-wait-ok")}).onError({err->throw new RuntimeException("composed failed: "+err)}).onComplete({$logger.info("coroutine:groovy:composed-wait-complete")})
timer.setTimeout({p->a.cancel("done");$logger.info("coroutine:groovy:cancellation-gate")},3000L)
