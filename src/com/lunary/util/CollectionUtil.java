package com.lunary.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunary.util.factory.Factory;


/**
 * 
 * A simple utility class for altering the form of a Collection or Map. Mainly
 * used for SQL results
 * 
 * @author Steven
 * 
 */
@SuppressWarnings("unchecked")
public final class CollectionUtil {

  private final static Logger logger = LoggerFactory.getLogger(CollectionUtil.class);
  
  private CollectionUtil() {}

  /**
   * 
   * pre : resultSet must be a valid List&lt;Map&gt; <br>
   * post : a Map&lt;columnName, List of same columnName&gt;
   * 
   * @param <K>
   * @param resultSet
   *          result set from sql. no need order by
   * @param columnName
   *          the column name that need to group by
   * @return null if resultSet is null
   */
  @SuppressWarnings("rawtypes")
  public static <K> Map<K, List<Map>> sqlMapListToMap(List<Map> resultSet, String columnName) {

    if (resultSet == null)
      return null;

    Map<K, List<Map>> rs = new HashMap<K, List<Map>>();
    for (Map<?, ?> map : resultSet) {

      K colValue = (K) map.get(columnName);

      List<Map> inMap = rs.get(colValue);
      if (inMap == null)
        inMap = new ArrayList<Map>();
      inMap.add(map);
      rs.put(colValue, inMap);
    }
    return rs;
  }

  /**
   * 
   * Turns a List into Map of List arranged by the value of fieldName.
   * 
   * @param <K>
   * @param <V>
   * @param resultSet
   * @param fieldName
   * @return null if resultSet is null
   */
  public static <K, V> Map<K, List<V>> sqlListToMap(List<V> resultSet, String fieldName) {

    if (resultSet == null)
      return null;

    Map<K, List<V>> rs = new HashMap<K, List<V>>();
    for (V obj : resultSet) {

      try {
        K colValue = FieldMethodUtil.invokeGetter(obj, fieldName, false);
        List<V> inMap = rs.get(colValue);
        if (inMap == null) {
          inMap = new ArrayList<V>();
          rs.put(colValue, inMap);
        }
        inMap.add(obj);
      }
      catch (Exception e) {
        logger.debug("getting field error", e);
      }
    }
    return rs;
  }

  /**
   * 
   * Lunaspeed
   * 
   * @param <E>
   * @param <V>
   * @param list
   * @param fieldName
   * @return
   */
  public static <E, V> Map<E, V> turnListToMap(List<V> list, String fieldName) {
    return turnListToMap(list, fieldName, false);
  }

  /**
   * 
   * Turn list into Map, duplicated key will be replaced by later value
   * 
   * @param <E>
   * @param <V>
   * @param list
   * @param fieldName
   * @param turnEnumToString
   * @return
   */
  public static <E, V> Map<E, V> turnListToMap(List<V> list, String fieldName, boolean turnEnumToString) {

    Map<E, V> map = new HashMap<E, V>();
    for (V val : list) {
      try {
        Object key = FieldMethodUtil.invokeGetter(val, fieldName, false);
        if (key != null) {
          if (turnEnumToString && Enum.class.isAssignableFrom(key.getClass())) {
            key = key.toString();
          }
          map.put((E) key, val);
        }
      }
      catch (Exception e) {
        logger.debug("getting field error", e);
      }

    }
    return map;
  }

  /**
   * 
   * pre : resultSet must be a valid List&lt;Map&gt; <br>
   * post : a Map&lt;keyValue, Map&gt;
   * 
   * @param resultSet
   *          result set from sql. no need order by
   * @param key
   *          the key to get the value to be the new key for the returned map
   * @return null if resultSet is null
   */
  @SuppressWarnings("rawtypes")
  public static Map<Object, Map> sqlListToMapWithUniqueKey(List<Map> resultSet, String key) {

    if (resultSet == null)
      return null;

    Map<Object, Map> rs = new HashMap<Object, Map>(resultSet.size());
    for (Map map : resultSet) {
      rs.put(map.get(key), map);
    }
    return rs;
  }

  /**
   * pre: takes a valid resultSet List, a columnName to group by<br>
   * post: separates the list into a List of List with each sub-list having the
   * same columnName value sorted by the natural comparator
   * 
   * @param resultSet
   * @param columnName
   * @return null if resultSet is null
   */
  @SuppressWarnings("rawtypes")
  public static List<List<Map>> splitSqlList(List<Map> resultSet, String columnName) {
    return splitSqlList(resultSet, columnName, null);
  }

  /**
   * 
   * pre: takes a valid resultSet List, a columnName to group by, a valid
   * comparator for the columnName's value<br>
   * post: separates the list into a List of List with each sub-list having the
   * same columnName value sorted by the keyComparator
   * 
   * @param resultSet
   * @param columnName
   * @param keyComparator
   * @return null if resultSet is null
   */
  @SuppressWarnings("rawtypes")
  public static List<List<Map>> splitSqlList(List<Map> resultSet, String columnName, Comparator keyComparator) {

    if (resultSet == null)
      return null;

    Map<Object, List<Map>> map = sqlMapListToMap(resultSet, columnName);
    Set keys = map.keySet();
    Object[] keyArray = keys.toArray();
    if (keyComparator == null) {
      Arrays.sort(keyArray);
    }
    else {
      Arrays.sort(keyArray, keyComparator);
    }

    List<List<Map>> rs = new ArrayList<List<Map>>(keyArray.length);
    for (Object key : keyArray) {
      rs.add(map.get(key));
    }
    return rs;
  }

  /**
   * pre: takes a valid resultSet list that have used order by in the sql, and
   * the ordered by column name<br>
   * post: gives a List of List with each sub-list having the same ordered value
   * 
   * @param resultSet
   * @param orderedColumnName
   * @return null if resultSet is null
   */
  @SuppressWarnings("rawtypes")
  public static List<List<Map>> splitOrderedSqlList(List<Map> resultSet, String orderedColumnName) {

    if (resultSet == null)
      return null;

    List<List<Map>> rs = new ArrayList<List<Map>>();

    Object lastValue = null;
    List<Map> lastList = new ArrayList<Map>();

    for (Map map : resultSet) {

      Object thisValue = map.get(orderedColumnName);

      if (thisValue == null || (!thisValue.equals(lastValue) && lastValue != null)) {
        rs.add(lastList);
        lastList = new ArrayList<Map>();
      }
      lastList.add(map);
      lastValue = thisValue;
    }
    if (lastValue != null)
      rs.add(lastList);

    return rs;
  }

  /**
   * pre: a valid Collection&lt;E&gt; <br>
   * post: a String composed of all Strings in the Collection set separated by
   * commas
   * 
   * @param <E>
   *          Generic type of the collection
   * 
   * @param col
   *          a Collection of E
   * @param addSingleQuote
   *          wraps each element with a single quote
   * @return null if collection is null
   */
  public static <E> String collectionToString(Collection<E> col, boolean addSingleQuote) {

    return collectionToString(col, addSingleQuote, null);
  }

  /**
   * 
   * pre: a valid Collection&lt;E&gt; <br>
   * post: a String composed of all Strings in the Collection set separated by
   * commas
   * 
   * @param <E>
   *          Generic type of the collection
   * 
   * @param col
   *          a Collection of E
   * @param addSingleQuote
   *          wraps each element with a single quote
   * @param formatter
   *          a formatter to format every object in the collection to String, if
   *          formatter is null then this parameter is ignored
   * @return null if collection is null
   */
  public static <E> String collectionToString(Collection<? extends E> col, boolean addSingleQuote, ObjectToStringFormatter<E> formatter) {

    if (col == null)
      return null;

    StringBuilder str = new StringBuilder();
    for (E s : col) {

      if (str.length() > 0)
        str.append(",");

      if (addSingleQuote)
        str.append("'").append((formatter == null ? s : formatter.format(s))).append("'");
      else
        str.append(s);
    }
    return str.toString();
  }

  /**
   * 
   * Turn a field of the objects in the list to a comma separated string. This
   * is methos is for non-Map List.
   * 
   * @param <E>
   *          type of value in field {@code fieldName}
   * 
   * @param list
   * @param fieldName
   * @param addSingleQuote
   * @param formatter
   *          a formatter to format the value of specified field in every object
   *          (type E) in the collection to String, if formatter is null then
   *          this parameter is ignored
   * @return null if list is null
   * @throws RuntimeException
   *           is any error occurs while accessing list object with fieldName
   *           getter
   */
  public static <E> String turnListToString(List<?> list, String fieldName, boolean addSingleQuote, ObjectToStringFormatter<E> formatter) throws RuntimeException {

    if (list == null)
      return null;

    StringBuilder st = new StringBuilder();

    for (Object obj : list) {
      try {
        E val = FieldMethodUtil.invokeGetter(obj, fieldName, false);
        if (val != null) {

          if (st.length() > 0) {
            st.append(",");
          }

          if (addSingleQuote) {
            st.append("'").append((formatter == null ? val : formatter.format(val))).append("'");
          }
          else {
            st.append((formatter == null ? val : formatter.format(val)));
          }
        }
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return st.toString();
  }

  /**
   * 
   * Turn a field of the objects in the list to a comma separated string. This
   * is methos is for non-Map List.
   * 
   * @param list
   * @param fieldName
   * @param addSingleQuote
   * @return null if list is null
   * @throws BIRuntimeException
   *           is any error occurs while accessing list object with fieldName
   *           getter
   */
  public static String turnListToString(List<?> list, String fieldName, boolean addSingleQuote) throws RuntimeException {

    return turnListToString(list, fieldName, addSingleQuote, null);
  }

  /**
   * pre: a valid List&lt;Map&gt; <br>
   * post: a String constructed by the values of columnName, separated by commas
   * 
   * @param list
   * @param columnName
   * @param addSingleQuote
   * @return null if list is null
   */
  public static String turnMapListToString(List<? extends Map<?, ?>> list, String columnName, boolean addSingleQuote) {

    return turnMapListToString(list, columnName, addSingleQuote, null);
  }

  /**
   * 
   * pre: a valid List&lt;Map&gt; <br>
   * post: a String constructed by the values of columnName, separated by commas
   * 
   * @param <E>
   *          type of value in column {@code columnName}
   * 
   * @param list
   * @param columnName
   * @param addSingleQuote
   * @param formatter
   *          a formatter to format the value of specified column in every Map
   *          (type E) in the collection to String, if formatter is null then
   *          this parameter is ignored
   * @return null if list is null
   */
  public static <E> String turnMapListToString(List<? extends Map<?, ?>> list, String columnName, boolean addSingleQuote, ObjectToStringFormatter<E> formatter) {

    if (list == null)
      return null;

    StringBuilder str = new StringBuilder();
    for (Map<?, ?> map : list) {
      E obj = (E) map.get(columnName);
      if (obj != null) {
        if (str.length() > 0)
          str.append(",");

        if (addSingleQuote) {
          str.append("'").append((formatter == null ? obj : formatter.format(obj))).append("'");
        }
        else {
          str.append((formatter == null ? obj : formatter.format(obj)));
        }
      }
    }
    return str.toString();
  }

  /**
   * 
   * Turns a column of the resultset's value into Set
   * 
   * @param results
   * @param key
   *          key to get the value from the result set
   * @return null if results is null
   */
  public static Set<String> turnMapResultToSet(List<? extends Map<?, ?>> results, String key) {

    if (results == null)
      return null;

    Set<String> set = new HashSet<String>(results.size());
    for (Map<?, ?> map : results) {
      set.add((String) map.get(key));
    }
    return set;
  }

  /**
   * 
   * Return true if the supplied Collection is null or empty. Otherwise, return
   * false.
   * 
   * @param collection
   *          the Collection to check
   * @return whether the given Collection is empty
   */
  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  /**
   * 
   * Return true if the supplied Map is null or empty. Otherwise, return false.
   * 
   * @param map
   *          the Map to check
   * @return true if the given Map is empty
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  /**
   * 
   * Get a value V from a ConcurrentMap and create on with Factory if one
   * doesn't exist in a thread-safe manner. The key will be passed into the
   * Factory when calling create. <br/>
   * Note: a new object may be created multiple times when the specified object
   * is not int the map due to concurrency reason, but only the first inserted
   * into the map will be returned
   * 
   * @param <K>
   * @param <V>
   * @param map
   *          ConcurrentMap to get value from
   * @param key
   *          key for the corresponding value
   * @param factory
   *          Factory for create new value
   * @return value from map or the new instance created by Factory, which will
   *         then be stored in the Map
   */
  public static <K, V> V getFromConcurrentMap(ConcurrentMap<K, V> map, K key, Factory<V> factory) {

    V val = map.get(key);
    if (val == null) {
      V newVal = factory.create(key);
      val = map.putIfAbsent(key, newVal);
      if (val == null) {
        val = newVal;
      }
    }
    return val;
  }

  /**
   * 
   * Get a value V from a ConcurrentMap and create on with Factory if one
   * doesn't exist in a thread-safe manner. The key will be passed as the first
   * arugment followed by values in objects into the Factory when calling
   * create. <br/>
   * Note: a new object may be created multiple times when the specified object
   * is not int the map due to concurrency reason, but only the first inserted
   * into the map will be returned
   * 
   * @param <K>
   * @param <V>
   * @param map
   *          ConcurrentMap to get value from
   * @param key
   *          key for the corresponding value
   * @param factory
   *          Factory for create new value
   * @param objects
   *          extra values needed to create the value
   * @return value from map or the new instance created by Factory, which will
   *         then be stored in the Map
   */
  public static <K, V> V getFromConcurrentMap(ConcurrentMap<K, V> map, K key, Factory<V> factory, Object... objects) {

    V val = map.get(key);
    if (val == null) {
      V newVal = factory.create(ArrayUtils.add(objects, 0, key));
      val = map.putIfAbsent(key, newVal);
      if (val == null) {
        val = newVal;
      }
    }
    return val;
  }

  /**
   * 
   * Turn an object into Map with key equal to propery field name.
   * 
   * @param obj
   * @return populated map
   */
  public static Map<String, Object> turnEntityToMap(Object obj) {

    return turnEntityToMap(obj, false);
  }

  /**
   * 
   * Turn an object into Map with key equal to propery field name.
   * 
   * @param obj
   * @param turnEnumToString
   * @return populated map
   */
  public static Map<String, Object> turnEntityToMap(Object obj, boolean turnEnumToString) {
    return turnEntityToMap(obj, turnEnumToString, false);
  }

  /**
   * 
   * Turn an object into Map with key equal to propery field name.
   * 
   * @param obj
   * @param turnEnumToString
   * @param keyToUppercase
   *          whether to turn key to uppercase
   * @return populated map
   */
  public static Map<String, Object> turnEntityToMap(Object obj, boolean turnEnumToString, boolean keyToUppercase) {

    Map<String, Object> map = new CaseInsensitiveMap();
    putValuesIntoMap(map, obj, turnEnumToString, keyToUppercase);
    return map;
  }

  /**
   * 
   * Turns a column of the resultset's value into Set
   * 
   * @param <E>
   * 
   * @param results
   * @param key
   *          key to get the value from the result set
   * @return null if results is null
   */
  public static <E> Set<E> turnEntityResultToSet(List<?> results, String key) {

    if (results == null)
      return null;

    Set<E> set = new HashSet<E>(results.size());
    try {
      for (Object entity : results) {
        set.add((E) FieldMethodUtil.invokeGetter(entity, key, false));
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return set;
  }

  /**
   * 
   * Put attributes from object to map.
   * 
   * @param map
   *          target map to put the values
   * @param obj
   *          object to retrieve attributes
   * @param turnEnumToString
   *          whether to turn Enum to String
   */
  public static void putValuesIntoMap(Map<String, Object> map, Object obj, boolean turnEnumToString) {
    putValuesIntoMap(map, obj, turnEnumToString, false);
  }

  /**
   * 
   * Put attributes from object to map.
   * 
   * @param map
   *          target map to put the values
   * @param obj
   *          object to retrieve attributes
   * @param turnEnumToString
   *          whether to turn Enum to String
   * @param keyToUppercase
   *          whether to turn key to uppercase
   */
  public static void putValuesIntoMap(Map<String, Object> map, Object obj, boolean turnEnumToString, boolean keyToUppercase) {

    PropertyDescriptor[] pds = ReflectionUtil.retrievePropertyDescriptors(obj.getClass());
    try {
      for (PropertyDescriptor pd : pds) {
        Method rd = pd.getReadMethod();

        if (rd != null && pd.getWriteMethod() != null) {
          Object val = rd.invoke(obj);
          if (turnEnumToString && val != null && val.getClass().isEnum()) {
            val = val.toString();
          }
          map.put((keyToUppercase ? pd.getName().toUpperCase() : pd.getName()), val);
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 
   * Returns a new instance of clazz with value filled with data in map. Key
   * value of the map must match the object field name. Null values from map are
   * ignored.
   * 
   * @param <E>
   *          type of Class
   * @param clazz
   *          class to create a new instance from
   * @param map
   *          Map containing values and attributes
   * @return E a Class instantiated from clazz
   */
  public static <E> E putValuesFromMapToEntity(Class<E> clazz, Map<String, Object> map) {

    E obj = null;

    try {
      obj = clazz.newInstance();
    }
    catch (Exception e) {
      logger.error("Error occurred while instantiating class: " + clazz.getName(), e);
      return null;
    }
    putValuesFromMapToEntity(obj, map);
    return obj;
  }

  /**
   * 
   * Puts the value in the map into the obj. Key value of the map must match the
   * object field name. Null values from map are ignored.
   * 
   * @param obj
   * @param map
   */
  @SuppressWarnings("rawtypes")
  public static void putValuesFromMapToEntity(Object obj, Map<String, Object> map) {

    PropertyDescriptor[] pds = ReflectionUtil.retrievePropertyDescriptors(obj.getClass());
    try {
      for (PropertyDescriptor pd : pds) {
        Object value = map.get(pd.getName());
        if (value != null) {
          Method wm = pd.getWriteMethod();

          if (wm != null && wm.getParameterTypes().length == 1) {
            Class clazz = wm.getParameterTypes()[0];
            if (clazz.isEnum()) {
              try {
                value = Enum.valueOf(clazz, value.toString());
              }
              catch (Exception e) {
                // casting exception is ignored
                logger.warn("Error casting value: " + value + " to Enum: " + clazz, e);
              }
            }
            wm.invoke(obj, value);
          }
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * 
   * Merges two lists of type L and R into a new list B of size left.size.
   * Order and size are perserved according to left. If an element on right could not be matched, null will be passed to merger.
   * @param <L>
   * @param <R>
   * @param <B>
   * @param left list to be merged by right and produce a new list
   * @param right list to be merged into left
   * @param merger logic for merging two elements L and R
   * @return a new list of type B by merging L and R
   */
  public static <L, R, B> List<B> mergeLists(List<L> left, List<R> right, Merger<L, R, B> merger) {
    
    List<B> rst = new ArrayList<B>(left.size());
    Map<Object, R> map = new HashMap<Object, R>(right.size());
    for(R r : right) {
      map.put(merger.getRightMergeKey(r), r);
    }
    
    for(L l : left) {
      R r = map.get(merger.getLeftMergeKey(l));
      rst.add(merger.merge(l, r));
    }
    return rst;
  }

  
  /**
   * Get a <code>Comparator</code> comparator for comparing Maps on the value of
   * given key. The value must be a Comparable
   * 
   * @param key
   *          : valid key for comparing value in Maps
   * @param desc
   *          : sort descending or else (if value of the corresponding "key" is
   *          String.class, it uses opposite sorting order)
   * @return a comparator that allows sorting of a list of map by the value of
   *         "key"
   */
  public static Comparator<Map<String, Object>> mapComparator(final String key, final boolean desc) {

    return new Comparator<Map<String, Object>>() {

      @Override
      public int compare(Map<String, Object> m1, Map<String, Object> m2) {

        int comp = 0;
        Object b1 = m1.get(key);
        Object b2 = m2.get(key);
        if (b1 == null && b2 == null)
          return 0;
        if (b1 == null)
          return 1;
        if (b2 == null)
          return -1;

        if (b1 instanceof String) {

          String bs1 = (String) b1;
          String bs2 = (String) b2;
          comp = bs1.compareTo(bs2);
          if (!desc) {
            comp *= -1;
          }
        }
        else if (b1 instanceof Comparable<?>) {

          Comparable<Object> bc1 = (Comparable<Object>) b1;
          Comparable<Object> bc2 = (Comparable<Object>) b2;
          comp = bc1.compareTo(bc2);
          if (desc) {
            comp *= -1;
          }
        }
        return comp;
      }

    };

  }

  /**
   * Used to format Object to String
   * 
   * @author Steven
   * 
   * @param <E>
   *          type of Object to be formated
   */
  public static interface ObjectToStringFormatter<E> {

    /**
     * 
     * Format obj to String
     * 
     * @param obj
     * @return obj in specified String format
     */
    public String format(E obj);
  }
  
  /**
   * Used to merge two lists together.
   * @author Steven
   *
   * @param <L>
   * @param <R>
   * @param <B>
   */
  public static interface Merger<L, R, B> {
    
    /**
     * 
     * gets the key from L that will be used to match key from R
     * @param l element to produce key from
     * @return produced key from l
     */
    public Object getLeftMergeKey(L l);
    
    /**
     * gets the key from R that will be used to match key from L
     * @param r element to produce key from
     * @return produced key from r
     */
    public Object getRightMergeKey(R r);
    
    /**
     * 
     * merges left and right to a new object
     * @param left an object to be merged
     * @param right an object to be merged
     * @return new object produced by merging left and right
     */
    public B merge(L left, R right);
  }
}
