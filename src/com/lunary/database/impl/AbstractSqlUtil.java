
package com.lunary.database.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lunary.database.PageContainer;
import com.lunary.database.SqlUtil;
import com.lunary.database.exception.DatabaseException;
import com.lunary.database.handler.BeanPaginationHandler;
import com.lunary.database.handler.BeanTopHandler;
import com.lunary.database.handler.MapPaginationHandler;
import com.lunary.database.handler.MapTopHandler;
import com.lunary.database.util.StatementUtil;
import com.lunary.database.util.TableEntityUtil;
import com.lunary.model.TableEntity;

public abstract class AbstractSqlUtil implements SqlUtil {

  private final DataSource dataSource;
  private final QueryRunner queryRunner;
  private final RowProcessor rowProcessor;
  
  public AbstractSqlUtil(DataSource dataSource, RowProcessor rowProcessor) {
    this.dataSource = dataSource;
    this.rowProcessor = rowProcessor;
    this.queryRunner = new QueryRunner(dataSource);
  }
  
  protected QueryRunner getQueryRunner() {
    return queryRunner;
  }
  
  protected DataSource getDataSource() {
    return dataSource;
  }
  
  protected Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public <E> List<E> find(String sql, Class<E> clazz, Object... params) {

    ResultSetHandler<List<E>> rsHandler = new BeanListHandler<E>(clazz, rowProcessor);
    return query(sql, rsHandler, params);
  }
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public <E extends TableEntity> E findByKey(E entity) {

    List<Object> keyValues = new ArrayList<Object>();
    StringBuilder sql = new StringBuilder("SELECT * FROM ");
    sql.append(entity.getTableName()).append(" WHERE ");
    sql.append(StatementUtil.assembleKeyStatement(entity, keyValues));
    // if(AppStaticConfig.BASE_DEBUG) {
    // LoggerUtil.debug(sql.toString() + " params: " + keyValues.toString());
    // }
    return (E) this.findOne(sql.toString(), entity.getClass(), keyValues.toArray());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int findCount(String sql, Object... params) {

    // if (!sql.toUpperCase().startsWith("SELECT COUNT(")) {
    // throw translateException(new
    // SQLException("Sql does not contain COUNT() as the first function."));
    // }
    int cnt = 0;
    try {
      convertParams(params);
      Integer result = queryRunner.query(getConnection(), sql, new ScalarHandler<Integer>(), params);
      if(result != null) {
        cnt = ((Integer) result).intValue();//jdbcTemplate.queryForInt(sql, params);
      }
    }
    catch (SQLException e) {
      throw translateException(e);
    }
    return cnt;
  }

  @Override
  public boolean exists(String fromSql, Object... params) {
    return findTopWithMap("SELECT 1 " + fromSql, 1, params).size() > 0;
  }

  // /**
  // *
  // * {@inheritDoc}
  // */
  // public <E> Set<E> findColumn(String sql, String columnName, Object...
  // params) {
  //
  // ResultSetHandler<Set<E>> rsHandler = new ColumnSetHandler<E>(columnName);
  // return query(sql, rsHandler, params);
  // }

  // /**
  // *
  // * {@inheritDoc}
  // */
  // @Override
  // public <E, V> Map<E, V> findMap(String sql, String columnName, Class<V>
  // clazz, Object... params) {
  //
  // ResultSetHandler<Map<E, V>> rsHandler = new KeyedEntityHandler<E,
  // V>(columnName, clazz, rowProcessor);
  // return query(sql, rsHandler, params);
  // }

  // /**
  // *
  // *
  // * {@inheritDoc}
  // */
  // @SuppressWarnings("unchecked")
  // public <E> Map<E, Map<String, Object>> findMapWithMap(String sql, String
  // columnName, Object... params) {
  //
  // ResultSetHandler rsHandler = new KeyedHandler(columnName);
  // return (Map<E, Map<String, Object>>) query(sql, rsHandler, params);
  // }

  /**
   * {@inheritDoc}
   */
  @Override
  public <E> E findOne(String sql, Class<E> clazz, Object... params) {

    ResultSetHandler<E> rsHandler = new BeanHandler<E>(clazz, rowProcessor);
    return (E) this.query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> findOneWithMap(String sql, Object... params) {

    ResultSetHandler<Map<String, Object>> rsHandler = new MapHandler();
    return query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <E> List<E> findTop(String sql, int top, Class<E> clazz, Object... params) {

    ResultSetHandler<List<E>> rsHandler = new BeanTopHandler<E>(top, clazz, rowProcessor);
    return query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<String, Object>> findTopWithMap(String sql, int top, Object... params) {

    ResultSetHandler<List<Map<String, Object>>> rsHandler = new MapTopHandler(top);
    return query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<String, Object>> findWithMap(String sql, Object... params) {

    ResultSetHandler<List<Map<String, Object>>> rsHandler = new MapListHandler();
    return query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <E> PageContainer<E> findWithPagination(String sql, int page, int rowsPerPage, Class<E> clazz, Object... params) {

    if (page <= 0) {
      page = 1;
    }
    // if (rowsPerPage <= 0) {
    // rowsPerPage = AppStaticConfig.ROWS_PER_PAGE;
    // }

    ResultSetHandler<PageContainer<E>> rsHandler = new BeanPaginationHandler<E>(clazz, page, rowsPerPage, rowProcessor);
    return query(sql, rsHandler, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PageContainer<Map<String, Object>> findWithPaginationMap(String sql, int page, int rowsPerPage, Object... params) {

    if (page <= 0) {
      page = 1;
    }
    // if (rowsPerPage <= 0) {
    // rowsPerPage = AppStaticConfig.ROWS_PER_PAGE;
    // }

    ResultSetHandler<PageContainer<Map<String, Object>>> rsHandler = new MapPaginationHandler(page, rowsPerPage);
    return query(sql, rsHandler, params);
  }

  private <E> E query(String sql, ResultSetHandler<E> rsHandler, Object... params) {

    E obj = null;
    try {
      convertParams(params);
      obj = getQueryRunner().query(getConnection(), sql, rsHandler, params);
    }
    catch (SQLException e) {
      throw translateException(e);
    }
    return obj;
  }
  
  protected RuntimeException translateException(Exception e) {
    return new DatabaseException(e);
  }

  protected Object[] convertParams(Object[] params) {

    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        params[i] = convert(params[i]);
      }
    }
    return params;
  }

  protected Object convert(Object param) {

    return TableEntityUtil.convertToSqlObject(param.getClass(), param);
  }
}
