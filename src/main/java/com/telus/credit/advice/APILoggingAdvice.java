package com.telus.credit.advice;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

//import io.opencensus.trace.Tracer;
//import io.opencensus.trace.Tracing;
//import io.opencensus.trace.samplers.Samplers;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class APILoggingAdvice {
    private static Logger logger = LoggerFactory.getLogger(APILoggingAdvice.class);
    private static final String LOG_MESSAGE_FORMAT = "%s.%s execution time: %dms";
    private static final String LOG_TRACE_FORMAT = ".%s.%s call";
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    //private static final Tracer tracer = Tracing.getTracer();

    @Before("execution(* com.telus.credit.controllers.*.*(..))")
    public void beforeAdviceForControllerLayer(JoinPoint jp) {
        logEntry(jp);
    }

    @After("execution(* com.telus.credit.controllers.*.*(..))")
    public void afterAdviceForControllerLayer(JoinPoint jp) {
        logExit(jp);
    }

    @Pointcut("execution(* com.telus.credit.controllers.*.*(..))")
    public void restPointCut() {
        logger.debug("Rest Layer pointcut for elapsed time");
    }

    @Around("restPointCut()")
    public Object timeProfileRestLayer(ProceedingJoinPoint pjp) throws Throwable {
        return calculateElapsedTime(pjp);
    }

    @Pointcut("execution(* com.telus.credit.firestore.*.*(..))")
    public void fireStorePointCut() {
        logger.debug("Firestore pointcut for elapsed time");
    }

    @Around("fireStorePointCut()")
    public Object timeProfileFireStoreLayer(ProceedingJoinPoint pjp) throws Throwable {
        return calculateElapsedTime(pjp);
    }
    
    @Before("execution(* com.telus.credit.firestore.*.*(..))")
    public void beforeAdviceForFirestoreLayer(JoinPoint jp) {
        logEntry(jp);
    }

    @After("execution(* com.telus.credit.firestore.*.*(..))")
    public void afterAdviceForFirestorerLayer(JoinPoint jp) {
        logExit(jp);
    }
    /**
     * @param pjp
     * @return
     * @throws Throwable
     */
    private Object calculateElapsedTime(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object retVal = null;

        retVal = pjp.proceed();

        stopWatch.stop();
        logExecutionTime(pjp, stopWatch);
        return retVal;
    }

    private void logExecutionTime(ProceedingJoinPoint joinPoint, StopWatch stopWatch) {
        String logMessage = String.format(LOG_MESSAGE_FORMAT, joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(), stopWatch.getTime());
        logger.debug(logMessage);
    }

    private void logEntry(JoinPoint joinPoint) {
        String logMessage = String.format(LOG_TRACE_FORMAT, joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName());
        
        try {
			//tracer.spanBuilder(activeProfile + logMessage).setSampler(Samplers.alwaysSample()).startScopedSpan();
			//tracer.spanBuilder(activeProfile + logMessage).startScopedSpan();
		} catch (Throwable e) {
			logger.warn("tracer.spanBuilder failed", e);
		}
        
        logger.trace("trace start:{} {}",activeProfile, logMessage);
        logger.info("start:{}", logMessage);
    }

    private void logExit(JoinPoint joinPoint) {
        String logMessage = String.format(LOG_TRACE_FORMAT, joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName());
        //tracer.getCurrentSpan().end();
        logger.trace("trace end:{} {}",activeProfile, logMessage);
        logger.info("end:{}", logMessage);
    }
}
