package com.nature.kline.common.ioc.starter;

import com.nature.kline.common.ioc.annotation.Injection;
import com.nature.kline.common.ioc.annotation.TaskMethod;
import com.nature.kline.common.ioc.config.InjectClasses;
import com.nature.kline.common.util.InstanceHolder;
import com.nature.kline.common.util.TaskHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ComponentStarter {

    private ComponentStarter() {
    }

    private static ComponentStarter instance;

    public static ComponentStarter getInstance() {
        if (instance == null) {
            synchronized (ComponentStarter.class) {
                if (instance == null) {
                    instance = new ComponentStarter();
                }
            }
        }
        return instance;
    }

    public void start() {
        for (Class<?> cls : InjectClasses.CLASSES) {
            this.inject(cls, InstanceHolder.get(cls));
        }
    }

    private void inject(Class<?> cls, Object o) {
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            TaskMethod annotation = m.getAnnotation(TaskMethod.class);
            if (annotation == null) {
                continue;
            }
            TaskHolder.put(annotation.value(), m, o);
            TaskHolder.add(annotation.value(), annotation.name());
        }

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Injection.class)) {
                Class<?> type = field.getType();
                field.setAccessible(true);
                try {
                    field.set(o, InstanceHolder.get(type));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Class<?> sc = cls.getSuperclass();
        if (!sc.equals(Object.class)) {
            this.inject(sc, o);
        }
    }
}
