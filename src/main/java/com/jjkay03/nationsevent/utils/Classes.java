package com.jjkay03.nationsevent.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Classes {

    // Returns the wrapper class of the given primitive class
    public static Class<?> primitiveToWrapper(Class<?> clazz) {
        if (clazz == null || !clazz.isPrimitive()) return clazz;

        HashMap<Class<?>, Class<?>> primiviteToWrapperMap = new HashMap<>(8);
        primiviteToWrapperMap.put(boolean.class, Boolean.class);
        primiviteToWrapperMap.put(byte.class, Byte.class);
        primiviteToWrapperMap.put(char.class, Character.class);
        primiviteToWrapperMap.put(double.class, Double.class);
        primiviteToWrapperMap.put(float.class, Float.class);
        primiviteToWrapperMap.put(int.class, Integer.class);
        primiviteToWrapperMap.put(short.class, Short.class);

        return primiviteToWrapperMap.get(clazz);
    }

    // Checks if a class is an interface of another class
    public static boolean isInterfaceOf(Class<?> clazz, Class<?> interfaze) {
        if (clazz == null || interfaze == null || !interfaze.isInterface()) {return false;}

        List<Class<?>> list1 = new ArrayList<>(List.of(clazz.getInterfaces()));
        List<Class<?>> list2 = new ArrayList<>();

        while (true) {
            for (Class<?> c : list1) {if (c == interfaze) {return true;} list2.addAll(List.of(c.getInterfaces()));}

            list1.clear();
            list1.addAll(list2);
            list2.clear();

            if (list1.isEmpty()) {return false;}
        }
    }
}
