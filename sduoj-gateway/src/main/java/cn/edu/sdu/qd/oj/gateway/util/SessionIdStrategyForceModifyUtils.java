/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Affero General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/agpl-3.0.en.html
 */

package cn.edu.sdu.qd.oj.gateway.util;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.WebSession;

import java.lang.reflect.Method;


/**
 * @Description 通过 javassist 暴力修改 org.springframework.session.MapSession 字节码来达到客制化 session id 生成策略的目的
 * @Author zhangt2333
 **/
@Slf4j
public class SessionIdStrategyForceModifyUtils {

    public static Method changeSessionIdMethod;

    /**
     * 修改 org.springframework.session.Session 增加接口方法 String changeSessionId(String userId)
     * 修改 org.springframework.session.MapSession 增加上述接口方法实现
     * 修改 org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository.RedisSession 增加上述接口方法实现
     * 修改 org.springframework.web.server.WebSession 增加接口方法 void changeSessionId(String userId)
     * 修改 org.springframework.session.web.server.session.SpringSessionWebSessionStore.SpringSessionWebSession 增加上述接口方法实现
     * 修改 org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository 的 save 方法来临时修复
     * org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository#save(org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository.RedisSession)
     * 中的 sessionRedisOperations.hasKey(String) 方法不返回空的 bug
     **/
    public static void modifyIdStrategyByByteCode() {
        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new ClassClassPath(SessionIdStrategyForceModifyUtils.class));

            CtClass sessionClass = pool.get("org.springframework.session.Session");
            sessionClass.addMethod(CtMethod.make("String changeSessionId(String userId);", sessionClass));
            sessionClass.toClass();

            CtClass mapSessionClass = pool.get("org.springframework.session.MapSession");
            mapSessionClass.addMethod(CtMethod.make("public String changeSessionId(String userId) { String changedId=userId + '-' + generateId(); setId(changedId); return changedId; }", mapSessionClass));
            mapSessionClass.toClass();

            CtClass redisSessionClass = pool.get("org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$RedisSession");
            redisSessionClass.addMethod(CtMethod.make("public String changeSessionId(String userId) { return this.cached.changeSessionId(userId); }", redisSessionClass));
            redisSessionClass.getDeclaredMethod("save").setModifiers(AccessFlag.PUBLIC);
            redisSessionClass.toClass();

            CtClass webSessionClass = pool.get("org.springframework.web.server.WebSession");
            webSessionClass.addMethod(CtMethod.make("void changeSessionId(String userId);", webSessionClass));
            webSessionClass.toClass();

            CtClass springSessionWebSessionClass = pool.get("org.springframework.session.web.server.session.SpringSessionWebSessionStore$SpringSessionWebSession");
            springSessionWebSessionClass.addMethod(CtMethod.make("public void changeSessionId(String userId) { this.session.changeSessionId(userId); save().subscribe(); }", springSessionWebSessionClass));
            springSessionWebSessionClass.toClass();

            CtClass repositoryClass = pool.get("org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository");
            CtMethod saveMethod = repositoryClass.getDeclaredMethod("save", new CtClass[]{redisSessionClass});
            repositoryClass.removeMethod(saveMethod);
            repositoryClass.addMethod(CtMethod.make("public reactor.core.publisher.Mono/*<Void>*/ save(org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository$RedisSession session) { return session.save(); }", repositoryClass));
            repositoryClass.toClass();

            changeSessionIdMethod = WebSession.class.getDeclaredMethod("changeSessionId", String.class);
        } catch (Exception e) {
            log.error("", e);
            System.exit(-1);
        }
    }

    public static void changeSessionId(WebSession webSession, Long userId) {
        try {
            changeSessionIdMethod.invoke(webSession, userId.toString());
        } catch (Exception e) {
            log.error("", e);
            System.exit(-1);
        }
    }
}

