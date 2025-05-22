package com.springboot.SoilDetection;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Processes the JdbiHandle annotation to automatically inject Jdbi repositories
 * into Spring beans.
 */
@Component
public class JdbiHandlePostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JdbiHandlePostProcessor.class);

    private final Jdbi jdbi;

    private final Map<Class<?>, Object> repositoryCache = new HashMap<>();

    @Autowired
    public JdbiHandlePostProcessor(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            JdbiHandle annotation = field.getAnnotation(JdbiHandle.class);
            if (annotation != null) {
                processJdbiHandleField(bean, field);
            }
        });
        return bean;
    }

    private void processJdbiHandleField(Object bean, Field field) {
        ReflectionUtils.makeAccessible(field);

        Class<?> repositoryType = field.getType();

        if (!repositoryType.isInterface()) {
            logger.error("@JdbiHandle can only be applied to interface fields: {} in {}",
                    field.getName(), bean.getClass().getName());
            return;
        }

        Object repository = repositoryCache.computeIfAbsent(repositoryType,
                type -> {
                    logger.debug("Creating on-demand Jdbi repository for: {}", type.getName());
                    return jdbi.onDemand(type);
                });

        try {
            field.set(bean, repository);
            logger.debug("Injected Jdbi repository {} into {}.{}",
                    repositoryType.getSimpleName(), bean.getClass().getSimpleName(), field.getName());
        } catch (IllegalAccessException e) {
            logger.error("Failed to inject Jdbi repository into {}.{}: {}",
                    bean.getClass().getName(), field.getName(), e.getMessage());
            throw new RuntimeException("Failed to inject Jdbi repository: " + e.getMessage(), e);
        }
    }
}