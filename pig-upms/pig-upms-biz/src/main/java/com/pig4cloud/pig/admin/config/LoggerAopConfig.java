package com.pig4cloud.pig.admin.config;

import cn.hutool.json.JSONUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Aspect
@Configuration
public class LoggerAopConfig {
    private static final Logger log = LoggerFactory.getLogger(LoggerAopConfig.class);

    private long startTime;//方法开始时间
    private long endTime;//方法结束时间

    /**
     * 定义拦截规则：拦截cn.g2link.efm.controller包下面的所有类中，有@RequestMapping注解的方法。
     */
    @Pointcut("execution( * com.pig4cloud.pig.admin.controller..*(..))&@annotation(org.springframework.web.bind.annotation.*)")
    public void executeService() {
    }

    @Before("execution( * com.pig4cloud.pig.admin.controller..*(..))&@annotation(org.springframework.web.bind.annotation.*)")
    public void invokeBefore(JoinPoint point) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = System.currentTimeMillis();
        log.info("start | " + format.format(startTime) + " | " + getRealClassName(point) + " | " + getMethodName(point) + " | " + getMethodArgs(point));
    }

    @After("execution( * com.pig4cloud.pig.admin.controller..*(..))&@annotation(org.springframework.web.bind.annotation.*)")
    public void invokeAfter(JoinPoint point) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        endTime = System.currentTimeMillis();
        log.info("end | " + format.format(endTime) + " | " + getRealClassName(point) + " | " + getMethodName(point) + " | " + getMethodArgs(point) + " | " + (endTime - startTime) + "ms");
    }

    @AfterThrowing(value = "executeService()", throwing = "e")
    public void afterThrowing(JoinPoint point, Throwable e) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.error("Exception | " + format.format(System.currentTimeMillis()) + " | " + getRealClassName(point) + " | " + getMethodName(point) + " | " + getMethodArgs(point) + " | " + e);
    }

    /**
     * 获取被代理对象的真实类全名
     *
     * @param point 连接点对象
     * @return 类全名
     */
    private String getRealClassName(JoinPoint point) {
        return point.getTarget().getClass().getName();
    }

    /**
     * 获取代理执行的方法名
     *
     * @param point 连接点对象
     * @return 调用方法名
     */
    private String getMethodName(JoinPoint point) {
        return point.getSignature().getName();
    }

    /**
     * 获取代理执行的方法的参数
     *
     * @param point
     * @return 调用方法的参数
     */
    private String getMethodArgs(JoinPoint point) {
        try {
            Object[] args = point.getArgs();
            return JSONUtil.toJsonStr(args);
        } catch (Exception e) {
        }
        return null;
    }
}
