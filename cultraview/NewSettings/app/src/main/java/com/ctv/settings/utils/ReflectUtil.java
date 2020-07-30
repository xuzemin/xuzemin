package com.ctv.settings.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/9/20 18:43
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/20 18:43
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ReflectUtil {

    public static Object getField(String clazzName, Object target, String name) throws Exception {
        return getField(Class.forName(clazzName), target, name);
    }

    public static Object getField(Class clazz, Object target, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * 反射获得属性
     * @param clazzName
     * @param target
     * @param name
     * @return
     */
    public static Object getFieldNoException(String clazzName, Object target, String name) {
        try {
            return getFieldNoException(Class.forName(clazzName), target, name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldNoException(Class clazz, Object target, String name) {
        try {
            return ReflectUtil.getField(clazz, target, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到某类的静态公共属性
     *
     * @param className
     *            类名
     * @param fieldName
     *            属性名
     * @return 该属性对象
     * @throws Exception
     */
    public static Object getStaticField(String className, String fieldName) throws Exception {
        Class<?> ownerClass = Class.forName(className);

        Field field = ownerClass.getField(fieldName);

        Object property = field.get(ownerClass);

        return property;
    }

    public static void setField(String clazzName, Object target, String name, Object value) throws Exception {
        setField(Class.forName(clazzName), target, name, value);
    }

    public static void setField(Class clazz, Object target, String name, Object value) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * 反射设置属性
     * @param clazzName
     * @param target
     * @param name
     * @param value
     */
    public static void setFieldNoException(String clazzName, Object target, String name, Object value) {
        try {
            setFieldNoException(Class.forName(clazzName), target, name, value);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setFieldNoException(Class clazz, Object target, String name, Object value) {
        try {
            ReflectUtil.setField(clazz, target, name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射调用方法
     * @param clazzName
     * @param target
     * @param name
     * @param args
     * @return
     * @throws Exception
     */
    public static Object invoke(String clazzName, Object target, String name, Object... args)
            throws Exception {
        return invoke(Class.forName(clazzName), target, name, args);
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(Class clazz, Object target, String name, Object... args)
            throws Exception {
        Class[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }

        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    public static Object invoke(String clazzName, Object target, String name, Class[] parameterTypes, Object... args)
            throws Exception {
        return invoke(Class.forName(clazzName), target, name, parameterTypes, args);
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(Class clazz, Object target, String name, Class[] parameterTypes, Object... args)
            throws Exception {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    public static Object invokeNoException(String clazzName, Object target, String name, Class[] parameterTypes, Object... args) {
        try {
            return invokeNoException(Class.forName(clazzName), target, name, parameterTypes, args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object invokeNoException(Class clazz, Object target, String name, Class[] parameterTypes, Object... args) {
        try {
            return invoke(clazz, target, name, parameterTypes, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 执行某类的静态方法
     *
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param args
     *            参数数组
     * @return 执行方法返回的结果
     * @throws Exception
     */
    public static Object invokeStaticMethod(String className, String methodName, Object... args) throws Exception {
        Class<?> ownerClass = Class.forName(className);

        Class<?>[] argsClass = new Class[args.length];

        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    /**
     * 新建实例
     *
     * @param className
     *            类名
     * @param args
     *            构造函数的参数 如果无构造参数，args 填写为 null
     * @return 新建的实例
     * @throws Exception
     */
    public static Object newInstance(String className, Object[] args) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return newInstance(className, args, null);

    }

    /**
     * 新建实例
     *
     * @param className
     *            类名
     * @param args
     *            构造函数的参数 如果无构造参数，args 填写为 null
     * @return 新建的实例
     * @throws Exception
     */
    public static Object newInstance(String className, Object[] args, Class<?>[] argsType) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> newoneClass = Class.forName(className);

        if (args == null) {
            return newoneClass.newInstance();

        }
        else {
            Constructor<?> cons;
            if (argsType == null) {
                Class<?>[] argsClass = new Class[args.length];

                for (int i = 0, j = args.length; i < j; i++) {
                    argsClass[i] = args[i].getClass();
                }

                cons = newoneClass.getConstructor(argsClass);
            }
            else {
                cons = newoneClass.getConstructor(argsType);
            }
            return cons.newInstance(args);
        }

    }

    /**
     * 是不是某个类的实例
     *
     * @param obj
     *            实例
     * @param cls
     *            类
     * @return 如果 obj 是此类的实例，则返回 true
     */
    public static boolean isInstance(Object obj, Class<?> cls) {
        return cls.isInstance(obj);
    }
}
