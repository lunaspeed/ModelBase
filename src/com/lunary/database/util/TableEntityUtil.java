package com.lunary.database.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;

import com.lunary.database.annotation.SqlExclude;
import com.lunary.model.TableEntity;
import com.lunary.util.DateUtil;
import com.lunary.util.FieldMethodUtil;
import com.lunary.util.ReflectionUtil;

/**
 * <pre>
 * For converting TableEntity field values into Map of key value pair.
 * SqlExclude is used to exclude fields in addition function specific.
 * Mainly for internal use only.
 * </pre>
 * 
 * @author Steven
 * @see com.lunary.database.annotation.SqlExclude
 */
public final class TableEntityUtil {

  private TableEntityUtil() {}

  public static Map<String, Object> convert(TableEntity entity) {

    Map<String, Object> values = new HashMap<String, Object>();

    List<Field> fields = ReflectionUtil.retrieveFields(entity.getClass());

    for (Field field : fields) {

      if (!field.isAnnotationPresent(SqlExclude.class)) {
        setKeyAndValues(values, field, entity);
      }
    }
    return values;
  }

  public static Map<String, Object> convertExcludeKey(TableEntity entity) {

    Map<String, Object> values = new HashMap<String, Object>();

    // List<Field> fields = new ArrayList<Field>();
    List<Field> fields = ReflectionUtil.retrieveFields(entity.getClass());
    // Set<String> excludeFields = entity.getExclusiveFields();
    Set<String> keys = entity.getKeyNames();
    for (Field field : fields) {

      if (!field.isAnnotationPresent(SqlExclude.class)) {

        String fieldName = field.getName();
        if (!keys.contains(fieldName)) {
          setKeyAndValues(values, field, entity);
        }
      }
    }
    return values;
  }

  private static void setKeyAndValues(Map<String, Object> values, Field field, TableEntity entity) {

    Class<?> fieldType = field.getType();
    boolean isBoolean = (fieldType == boolean.class || fieldType == Boolean.class);
    // prefix = IS;
    String fieldName = field.getName();
    String methodName = FieldMethodUtil.makeGetter(fieldName, isBoolean);

    try {
      Object fieldValue = MethodUtils.invokeMethod(entity, methodName, null);

      if (fieldValue != null) {

        fieldValue = convertToSqlObject(fieldType, fieldValue);

      }
      values.put(fieldName, fieldValue);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public static Object convertToSqlObject(Class<?> clazz, Object value) {

    if(value != null) {
      try {
        if (clazz == java.util.Date.class) {
          value = DateUtil.getTimestamp((java.util.Date) value);
        }
        else if (Enum.class.isAssignableFrom(clazz)) {
          value = value.toString();
        }
  
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return value;
  }

}
