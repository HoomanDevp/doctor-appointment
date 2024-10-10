package com.stts.doctorappointment.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services, and REST controllers.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    /**
     * Advice that logs methods before execution.
     *
     * @param joinPoint join point for advice
     */
    @Before("springBeanPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    /**
     * Advice that logs methods after returning.
     *
     * @param joinPoint join point for advice
     * @param result the result of the method execution
     */
    @AfterReturning(pointcut = "springBeanPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} returned with value {}", joinPoint.getSignature().getName(), result);
    }

    /**
     * Advice that logs methods after throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param exception the exception thrown by the method
     */
    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Method {} threw exception: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }
}