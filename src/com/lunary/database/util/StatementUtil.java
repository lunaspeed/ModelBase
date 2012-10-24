package com.lunary.database.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;

import com.lunary.model.TableEntity;
import com.lunary.util.FieldMethodUtil;

/**
 * Turning {@link TableEntity} to StatementSet.<br>
 * All {@link com.lunary.database.annotation.SqlExclude} fields are excluded except for
 * constructing key
 * 
 * @author Steven
 * 
 */
public final class StatementUtil {

    private StatementUtil() {}

    /**
     * Turning {@link TableEntity} to insert statement
     * 
     * @param entity
     * @return StatementSet with insert statement
     */
    public static SqlStatement buildPreparedInsertStatement(TableEntity entity) {

        StringBuilder stmt = new StringBuilder();
        stmt.append("INSERT INTO ").append(entity.getTableName()).append(" (");

        StringBuilder fieldNames = new StringBuilder();
        StringBuilder fieldMarks = new StringBuilder();
        List<Object> fieldValues = new ArrayList<Object>();
        Map<String, Object> fieldsAndValues = TableEntityUtil.convert(entity);
        Set<Entry<String, Object>> entries = fieldsAndValues.entrySet();
        for (Iterator<Entry<String, Object>> i = entries.iterator(); i.hasNext();) {
          Entry<String, Object> entry = i.next();
          if (entry.getValue() != null) {
            fieldNames.append(entry.getKey()).append(",");
            fieldMarks.append("?,");
            fieldValues.add(entry.getValue());
          }
        }
        fieldNames.deleteCharAt(fieldNames.length() - 1);
        fieldMarks.deleteCharAt(fieldMarks.length() - 1);

        stmt.append(fieldNames).append(") VALUES (");
        stmt.append(fieldMarks).append(")");

        SqlStatement statementSet = new SqlStatement(stmt.toString(), fieldValues.toArray());

        return statementSet;
    }

    /**
     * Turning {@link TableEntity} to insert statement
     * 
     * @param entity
     *            at TableEntity to be converted to sql
     * @param updateNull
     *            if true will include null value columns in sql and update them
     *            to null
     * @return StatementSet containing the sql and corresponding parameters
     * @throws NullPointerException
     *             if any key value is null
     */
    public static SqlStatement buildPreparedUpdateStatement(TableEntity entity, boolean updateNull) {

        StringBuilder stmt = new StringBuilder();
        stmt.append("UPDATE ").append(entity.getTableName()).append(" SET ");

        Map<String, Object> fieldsAndValues = TableEntityUtil.convertExcludeKey(entity);
        Set<Entry<String, Object>> entries = fieldsAndValues.entrySet();
        List<Object> fieldValues = new ArrayList<Object>();
        for (Iterator<Entry<String, Object>> i = entries.iterator(); i.hasNext();) {

            Entry<String, Object> entry = i.next();

            if (!updateNull && entry.getValue() == null) {
                continue;
            }
            stmt.append(entry.getKey()).append("=?,");
            fieldValues.add(entry.getValue());

        }
        stmt.deleteCharAt(stmt.length() - 1);
        stmt.append(" WHERE ");
        stmt.append(assembleKeyStatement(entity, fieldValues));

        SqlStatement statementSet = new SqlStatement(stmt.toString(), fieldValues.toArray());

        return statementSet;
    }

    /**
     * Turning {@link TableEntity} to delete statement
     * 
     * @param entity
     * @return StatementSet with delete statement
     * @throws NullPointerException
     *             if any key value is null
     */
    public static SqlStatement buildDeleteStatement(TableEntity entity) {

        List<Object> fieldValues = new ArrayList<Object>();
        StringBuilder stmt = new StringBuilder();
        stmt.append("DELETE FROM ").append(entity.getTableName()).append(" WHERE ");
        stmt.append(assembleKeyStatement(entity, fieldValues));

        return new SqlStatement(stmt.toString(), fieldValues.toArray());
    }

    /**
     * Creates the sql statement for the WHERE (no included) as key
     * {@link com.lunary.database.annotation.SqlExclude} annotated fields will not be
     * excluded
     * 
     * @param entity
     * @param fieldValues
     * @return part of the sql for WHERE and parameters are added into {@code
     *         fieldValues}
     * @throws NullPointerException
     *             if any key value is null
     * @see TableEntity#getKeyNames()
     */
    public static StringBuilder assembleKeyStatement(TableEntity entity, List<Object> fieldValues) {

        StringBuilder keyStmt = new StringBuilder();
        Set<String> keyEntries = entity.getKeyNames();
        for (Iterator<String> k = keyEntries.iterator(); k.hasNext();) {
            String key = (String) k.next();
            keyStmt.append(key).append("=?").append(" AND ");
            fieldValues.add(getKey(entity, key));
        }
        keyStmt.delete(keyStmt.lastIndexOf("AND"), keyStmt.length());

        return keyStmt;
    }

    /**
     * 
     * This assumes no key is of boolean type
     * 
     * @param entity
     * @param key
     * @return the value in the <code>key</code> property
     * @throws NullPointerException
     *             if the key value is null
     */
    private static Object getKey(TableEntity entity, String key) {

        Object value = null;
        String methodName = FieldMethodUtil.makeGetter(key, false);
        try {
            value = MethodUtils.invokeMethod(entity, methodName, null);
            if (value == null) {
                throw new NullPointerException("Table key cannot be null");
            }
            value = TableEntityUtil.convertToSqlObject(value.getClass(), value);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }
    
    /**
     * Holds information for sql statement and parameters to bind with it
     * 
     * @author Steven
     * 
     */
    public static final class SqlStatement {

        private final String sql;
        private final Object[] params;

        /**
         * Creates a StatementSet
         * 
         * @param sql
         * @param params
         */
        public SqlStatement(String sql, Object[] params) {

            this.sql = sql;
            this.params = params;
        }

        /**
         * 
         * @return sql
         */
        public String getSql() {

            return sql;
        }

        /**
         * 
         * @return parameters
         */
        public Object[] getParams() {

            return params;
        }

        /**
         * @return sql + params
         */
        @Override
        public String toString() {
            return sql + " params: " + Arrays.toString(this.params);
        }
    }
}
