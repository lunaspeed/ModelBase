package com.lunary.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;

/**
 * Simple utility for provide easy access to fields using getter and setter convention
 * @author Steven
 *
 */
public class FieldMethodUtil {

    private static final String GET = "get";
    private static final String SET = "set";
    private static final String IS = "is";

    private FieldMethodUtil() {}

    /**
     * 
     * Make a field's setter method name
     * 
     * @param fieldName
     * @return the name of the setter method
     */
    public static String makeSetter(String fieldName) {

        return makeMethod(SET, fieldName);
    }

    /**
     * <pre>
     * Make a field's getter method name.
     * If the field is boolean then method starts with &quot;is&quot;.
     * </pre>
     * 
     * @param fieldName
     * @param isBoolean
     *            is the field a boolean
     * @return the name of the getter method
     */
    public static String makeGetter(String fieldName, boolean isBoolean) {

        return makeMethod(isBoolean ? IS : GET, fieldName);
    }

    private static String makeMethod(String action, String fieldName) {

        StringBuilder s = new StringBuilder();
        return s.append(action).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
    }

    /**
     * 
     * Invoke the getter for a field on the object and return the value
     * 
     * @param <E>
     *            field type
     * @param object
     *            object to invoke the getter on
     * @param fieldName
     *            invoke getter for this field
     * @param isBoolean
     *            is the field boolean
     * @return object type O returned by getter
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <V> V invokeGetter(Object object, String fieldName, boolean isBoolean) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeWithGetterMethodName(object, makeGetter(fieldName, isBoolean));
    }
    
    /**
     * 
     * Invoke a setter function of an object of field {@code fieldName} and sets {@code value}
     * @param object object to invoke setter on
     * @param fieldName invode setter for this field
     * @param value the value to be set into the field
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void invokeSetter(Object object, String fieldName, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MethodUtils.invokeMethod(object, makeSetter(fieldName), value);
    }
    /**
     * 
     * Invokes a getter method on object to retrieve value
     * @param <V> type of value from getter
     * @param object object to get value from
     * @param getterMethodName the actual full getter method name
     * @return value from object using getterMethodName
     * @throws NoSuchMethodException if method does not exists on object
     * @throws IllegalAccessException 
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public static <V> V invokeWithGetterMethodName(Object object, String getterMethodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (V) MethodUtils.invokeMethod(object, getterMethodName, null);
    }

}
