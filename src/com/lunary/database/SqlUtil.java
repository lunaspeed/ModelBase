
package com.lunary.database;

import java.util.List;
import java.util.Map;

import com.lunary.model.TableEntity;


/**
 * Provides basic operations to access database
 * 
 * @author Steven
 * 
 */
public interface SqlUtil {

    /**
     * Gets a List of E with sql
     * 
     * @param <E>
     *            The type of class will be generated in the list
     * @param sql
     *            The sql to be executed for the result set
     * @param clazz
     *            The class to populate the list with
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a List of objects of clazz. If non-selected, an empty List will
     *         be returned
     * 
     */
    public <E> List<E> find(String sql, Class<E> clazz, Object... params);

    /**
     * Gets the integer count returned by the sql with COUNT()<br>
     * COUNT() must be the first column
     * 
     * @param sql
     *            The sql to be executed for the count
     * @param params
     *            parameters to be bind with the sql, optional
     * @return the count
     */
    public int findCount(String sql, Object... params);
    
    /**
     * 
     * Whether there exists a record according to the sql and parameters
     * @param fromSql a SQL starting with FROM. "SELECT" is not needed
     * @param params parameters to be bind with the sql, optional
     * @return true if the sql selects one or more record, false otherwise
     */
    public boolean exists(String fromSql, Object... params);

//    /**
//     * 
//     * Gets distinct value of one specific column and return as a Set.<br>
//     * Does not support Enum type as key E.
//     * 
//     * @param <E>
//     *            The type of class in the Set
//     * @param sql
//     *            The sql to be executed for the set
//     * @param column
//     *            the column to get the values from
//     * @param params
//     *            optional
//     * @return a Set of the column's value
//     */
//    public <E> Set<E> findColumn(String sql, String column, Object... params);

//    /**
//     * Return the result set as a Map of Map with value of columnName as the key
//     * and the Map (rest of the row) as the value. Duplicated rows will be
//     * discarded by the definition of Map. Does not support Enum type as key E.
//     * 
//     * @param <E>
//     * @param sql
//     *            The sql to be executed for the result set
//     * @param columnName
//     *            use this column's value to be the key of the map
//     * @param params
//     *            Parameters to be bind to the SQL, optional
//     * @return a Map of Map representing the sql result set with columnName's
//     *         value as key
//     */
//    public <E> Map<E, Map<String, Object>> findMapWithMap(String sql, String columnName, Object... params);

//    /**
//     * Return the result set as a Map of V with value of columnName as the key
//     * and the V (rest of the row) as the value. Duplicated rows will be
//     * discarded by the definition of Map. Does not support Enum type as key E.
//     * 
//     * @param <E>
//     *            type of the key in the Map
//     * @param <V>
//     *            type of the value in the Map
//     * @param sql
//     *            The sql to be executed for the result set
//     * @param columnName
//     *            use this column's value to be the key of the map
//     * @param clazz
//     *            The class to populate the Map's value with
//     * @param params
//     *            Parameters to be bind to the SQL, optional
//     * @return a Map of V representing the sql result set with columnName's
//     *         value as key
//     */
//    public <E, V> Map<E, V> findMap(String sql, String columnName, Class<? extends V> clazz, Object... params);

    /**
     * Gets the first row of the result set and turn it to Object of class
     * <code>clazz</code> type E
     * 
     * @param <E>
     *            The type of class will be generated
     * @param sql
     *            The sql to be executed for the result set
     * @param clazz
     *            The class of type E to generate
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a Object of class E, returns null if none found
     */
    public <E> E findOne(String sql, Class<E> clazz, Object... params);

    /**
     * 
     * Gets the first row of the result set and turn it as a Map. <br/>
     * For code clarity, {@link #findOne(String, Class, Object...)} is prefered.
     * 
     * @param sql
     *            The sql to be executed for the result set
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a Map, returns null if none found
     */
    public Map<String, Object> findOneWithMap(String sql, Object... params);

    /**
     * 
     * Gets the top rows from the sql result
     * 
     * @param <E>
     *            The type of class will be generated in the list
     * @param sql
     *            The sql to be executed for the result set
     * @param top
     *            The number of rows the get from the top of the result set
     * @param clazz
     *            The class to populate the list with
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a List of objects of clazz. If non-selected, an empty List will
     *         be returned
     */
    public <E> List<E> findTop(String sql, int top, Class<E> clazz, Object... params);

    /**
     * 
     * Gets the top rows from the sql result. <br/>
     * For code clarity, {@link #findTop(String, int, Class, Object...)} is
     * prefered.
     * 
     * @param sql
     *            The sql to be executed for the result set
     * @param top
     *            The number of rows the get from the top of the result set
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a List of Map representing the sql result set
     */
    public List<Map<String, Object>> findTopWithMap(String sql, int top, Object... params);

    /**
     * 
     * Gets a List of Map from the sql result. For code clarity,
     * {@link #find(String, Class, Object...)} is prefered.
     * 
     * @param sql
     *            The sql to be executed for the result set
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a List of Map representing the sql result set
     */
    public List<Map<String, Object>> findWithMap(String sql, Object... params);

    /**
     * 
     * Gets the list of E restricted by page and rowsPerPage. And all
     * information provided by a PageContainer.
     * 
     * @see com.bi.base.database.PageContainer
     * @param <E>
     *            The type of class will be generated in the list
     * @param sql
     *            The sql to be executed for the result set
     * @param page
     *            the page to get
     * @param rowsPerPage
     *            how many rows in a page
     * @param clazz
     *            The class of type E to populate the list with
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a PageContainer containing results
     */
    public <E> PageContainer<E> findWithPagination(String sql, int page, int rowsPerPage, Class<E> clazz, Object... params);

    /**
     * 
     * Gets the list of Map restricted by page and rowsPerPage. And all
     * information provided by a PageContainer. <br/>
     * For code clarity,
     * {@link #findWithPagination(String, int, int, Class, Object...)} is
     * prefered.
     * 
     * @see com.bi.base.database.PageContainer
     * @param sql
     *            The sql to be executed for the result set
     * @param page
     *            the page to get
     * @param rowsPerPage
     *            how many rows in a page
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return a PageContainer containing results
     */
    public PageContainer<Map<String, Object>> findWithPaginationMap(String sql, int page, int rowsPerPage, Object... params);

    /**
     * 
     * Executes the sql
     * 
     * @param sql
     *            The sql to be executed
     * @param params
     *            Parameters to be bind to the SQL, optional
     * @return number of rows updated
     */
    public int update(String sql, Object... params);

    /**
     * 
     * Insert the date delivered by the entity
     * 
     * @param entity
     * @return number of rows inserted
     */
    public int insert(TableEntity entity);

//    /**
//     * 
//     * Insert the date delivered by the entity. Also updates the id field with
//     * value from DB.
//     * 
//     * @param entity
//     * @return 1 if successful, otherwise throws exception
//     */
//    public int insertRetrieveId(BaseTableObject entity);

    /**
     * 
     * Update the row represented by the TableEntity. All Null columns will be
     * ignored
     * 
     * @param entity
     * @return number of rows updated
     * @throws NullPointerException
     *             if any key is null
     */
    public int update(TableEntity entity);

    /**
     * 
     * Update the row represented by the TableEntity. All Null columns will be
     * updated
     * 
     * @param entity
     * @return number of rows updated
     * @throws NullPointerException
     *             if any key is null
     */
    public int updateWithNull(TableEntity entity);

    /**
     * 
     * Deletes the row represented by the TableEntity
     * 
     * @param entity
     * @return number of rows deleted
     * @throws NullPointerException
     *             if any key is null
     */
    public int delete(TableEntity entity);

    /**
     * 
     * Finds a row of data in the form of {@code <E extends TableEntity> } from
     * Database with the key values in entity
     * 
     * @param <E>
     *            extends TableEntity
     * @param entity
     *            an entity extending {@code TableEntity } with its key filled
     *            with values
     * @return an object same type as entity
     * @throws NullPointerException
     *             if any key is null
     */
    public <E extends TableEntity> E findByKey(E entity);

   
}
