package com.lunary.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provide simple reflection functions
 * 
 * @author Steven
 * 
 */
public final class ReflectionUtil {

  // private static final Logger logger = LoggerUtil.BASE_LOGGER;
  private static final ConcurrentMap<Class<?>, List<Field>> classFieldsMap = new ConcurrentHashMap<Class<?>, List<Field>>();

  // private static final Factory<List<Field>> CLASS_FIELD_FACTORY = new
  // FieldRetrieverFactory();

  private ReflectionUtil() {}

  /**
   * 
   * Clears all cached Class fields
   */
  public static void flushCache() {
    classFieldsMap.clear();
  }

  /**
   * 
   * Clear cached fields for the Class clazz
   * 
   * @param clazz
   *          class fields to be cleared from cache
   * @throws NullPointerException
   *           when clazz is null
   */
  public static void flushFromCache(Class<?> clazz) throws NullPointerException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    classFieldsMap.remove(clazz);
  }

  /**
   * Copies values of same attribute from {@code from} to {@code to}.
   * 
   * @param <F>
   * @param <T>
   * @param from
   * @param to
   * @param excludes
   *          is a Set of String which is the setter method name not to copy
   *          value
   */
  public static <F, T extends F> void copyValue(F from, T to, Set<String> excludes) {

    PropertyDescriptor[] fromPds = retrievePropertyDescriptors(from.getClass());
    PropertyDescriptor[] toPds = retrievePropertyDescriptors(to.getClass());

    if (excludes == null) {
      excludes = Collections.emptySet();
    }

    try {
      for (PropertyDescriptor fromPd : fromPds) {

        PropertyDescriptor toPd = null;
        if (!excludes.contains(fromPd.getName()) && (toPd = getSamePropertyDescriptor(fromPd, toPds)) != null) {

          Method wm = toPd.getWriteMethod();
          Method rm = fromPd.getReadMethod();
          if (wm != null && rm != null) {
            wm.invoke(to, rm.invoke(from));
          }
        }

      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Copies values of same attribute from {@code from} to {@code to}.
   * 
   * @param from
   * @param to
   * @param excludes
   *          is a Set of String which is the setter method name not to copy
   *          value
   */
  public static void copyValueForDifferentObj(Object from, Object to, Set<String> excludes) {

    PropertyDescriptor[] fromPds = retrievePropertyDescriptors(from.getClass());
    PropertyDescriptor[] toPds = retrievePropertyDescriptors(to.getClass());

    if (excludes == null) {
      excludes = Collections.emptySet();
    }

    try {
      for (PropertyDescriptor fromPd : fromPds) {

        PropertyDescriptor toPd = null;
        if (!excludes.contains(fromPd.getName()) && (toPd = getSameNamePropertyDescriptor(fromPd, toPds)) != null) {

          Method wm = toPd.getWriteMethod();
          Method rm = fromPd.getReadMethod();
          if (wm != null && rm != null) {
            wm.invoke(to, rm.invoke(from));
          }
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static PropertyDescriptor getSameNamePropertyDescriptor(PropertyDescriptor fromPd, PropertyDescriptor[] toPds) {

    PropertyDescriptor retPd = null;
    for (PropertyDescriptor cpd : toPds) {
      if (cpd.getName().equals(fromPd.getName()) && cpd.getPropertyType().equals(fromPd.getPropertyType())) {
        retPd = cpd;
        break;
      }
    }
    return retPd;
  }

  private static PropertyDescriptor getSamePropertyDescriptor(PropertyDescriptor pd, PropertyDescriptor[] pds) {

    PropertyDescriptor retPd = null;
    for (PropertyDescriptor cpd : pds) {
      if (cpd.equals(pd)) {
        retPd = cpd;
        break;
      }
    }
    return retPd;
  }

  /**
   * 
   * Copies a Java instance into a new Object of the same type. <br/>
   * Note: the underlying properties value are still referring to the same
   * instances.
   * 
   * @param <T>
   * @param src
   *          object to be copied
   * @return a new Object T with same values as {@code src}
   */
  @SuppressWarnings("unchecked")
  public static <T> T copyInstance(T src) {

    if (src == null) {
      throw new NullPointerException("src cannot be null");
    }
    T copyInstance = null;
    Class<T> srcClass = (Class<T>) src.getClass();
    PropertyDescriptor[] props = retrievePropertyDescriptors(srcClass);

    try {
      copyInstance = (T) srcClass.newInstance();
      for (PropertyDescriptor pd : props) {
        Method rm = pd.getReadMethod();
        Method wm = pd.getWriteMethod();
        if (wm != null && rm != null) {
          wm.invoke(copyInstance, rm.invoke(src));
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return copyInstance;
  }

  // public static void main(String[] args) {
  //
  // SomeObj so = new SomeObj();
  // SomeObj1 so1 = new SomeObj1();
  //
  // List<Field> sof = getf(so.getClass());
  // sof = getf(so1.getClass());
  // sof.get(0);
  // // S s = new S();
  // // s.setOne("ONE");
  // // s.setTwo(new Integer(2));
  // // s.setYesNo(YesNo.N);
  // //
  // // PropertyDescriptor[] pds = retrievePropertyDescriptors(s.getClass());
  // // for(PropertyDescriptor pd : pds) {
  // // System.out.println(pd.getValue("one"));
  // // System.out.println(pd.getName());
  // // }
  // }
  //
  // public static class S {
  // private String one;
  //
  // private Integer two;
  //
  // private YesNo yesNo;
  //
  // public String getOne() {
  // return one;
  // }
  //
  // public void setOne(String one) {
  // this.one = one;
  // }
  //
  // public Integer getTwo() {
  // return two;
  // }
  //
  // public void setTwo(Integer two) {
  // this.two = two;
  // }
  //
  // public YesNo getYesNo() {
  // return yesNo;
  // }
  //
  // public void setYesNo(YesNo yesNo) {
  // this.yesNo = yesNo;
  // }
  //
  //
  // }

  /**
   * Retrieves the PropertyDescriptor from the clazz
   * 
   * @param clazz
   * @return PropertyDescriptors
   * @throws RuntimeException
   *           if error occurs while retrieving PropertyDescriptor
   */
  public static PropertyDescriptor[] retrievePropertyDescriptors(Class<?> clazz) throws RuntimeException {

    // Introspector caches BeanInfo classes for better performance
    BeanInfo beanInfo = null;
    try {
      beanInfo = Introspector.getBeanInfo(clazz);
    }
    catch (IntrospectionException e) {
      throw new RuntimeException("Bean introspection failed: " + e.getMessage());
    }

    return beanInfo.getPropertyDescriptors();
  }

  /**
   * Retrieve a list of Field for the class given. If any super field is
   * overwritten by subclasses, subclasses' field will be used.
   * 
   * @param clazz
   *          the class to retrieve fields from
   * @return unmodifiableList of Field from the clazz
   * @throws NullPointerException
   *           if clazz is null
   */
  public static List<Field> retrieveFields(Class<?> clazz) throws NullPointerException {
    return retrieveFields(clazz, true);
  }

  /**
   * 
   * Retrieve a list of Field for the class given. If any super field is
   * overwritten by subclasses, subclasses' field will be used.
   * 
   * @param clazz
   *          the class to retrieve fields from
   * @param cache
   *          whether to cache the current clazz, will cache all super classes
   * @return unmodifiableList of Field from the clazz
   * @throws NullPointerException
   *           if clazz is null
   */
  public static List<Field> retrieveFields(Class<?> clazz, boolean cache) throws NullPointerException {

    if (clazz == null) {
      throw new NullPointerException();
    }
    List<Field> returnFields = classFieldsMap.get(clazz);

    if (returnFields == null) {
      List<Field> superFields;
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null && superClass != Object.class) {
        // cache only apply to the current Class
        superFields = retrieveFields(superClass, true);
      }
      else {
        superFields = Collections.emptyList();
      }

      Field[] currentFields = clazz.getDeclaredFields();
      superFields = intersectFields(currentFields, superFields);
      if (cache) {
        returnFields = classFieldsMap.putIfAbsent(clazz, superFields);
      }
      if (returnFields == null) {
        returnFields = superFields;
      }
    }

    return returnFields;
  }

  /**
   * 
   * Merge super class fields with current class fields and remove duplicated
   * ones.
   * 
   * @param currentFields
   * @param superFields
   * @return unmodifiableList of merged Fields
   */
  private static List<Field> intersectFields(Field[] currentFields, List<Field> superFields) {

    List<Field> newFields = new ArrayList<Field>();

    // add super class fields that has not appeared in the sub-class into
    // the list
    for (Field superField : superFields) {

      boolean overwritten = false;
      for (Field curField : currentFields) {
        if (superField.getName().equals(curField.getName())) {
          overwritten = true;
          break;
        }
      }
      if (overwritten == false) {
        newFields.add(superField);
      }
    }
    // add current class fields in
    for (Field f : currentFields) {
      int modifier = f.getModifiers();
      if (!Modifier.isStatic(modifier) && !Modifier.isFinal(modifier)) {
        newFields.add(f);
      }
    }
    return Collections.unmodifiableList(newFields);
  }
  
}
