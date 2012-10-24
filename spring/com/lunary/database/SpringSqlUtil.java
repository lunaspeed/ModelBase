package com.lunary.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.lunary.database.exception.DatabaseException;
import com.lunary.database.impl.AbstractSqlUtil;
import com.lunary.database.util.StatementUtil;
import com.lunary.database.util.StatementUtil.SqlStatement;
import com.lunary.database.util.TableEntityUtil;
import com.lunary.model.IdKeyedTableEntity;
import com.lunary.model.TableEntity;
import com.lunary.util.CollectionUtil;
import com.lunary.util.factory.Factory;
/**
 * <pre>
 * This implementation mainly uses QueryRunner mechanism.
 * All result set handling logic class implements ResultSetHandler.
 * </pre>
 * 
 * @see org.apache.commons.dbutils.QueryRunner
 * @see org.apache.commons.dbutils.ResultSetHandler
 * @author Steven
 * 
 */
public class SpringSqlUtil extends AbstractSqlUtil {

  private final String autoGenerateColumnName = IdKeyedTableEntity.ID;
  private final JdbcTemplate jdbcTemplate;

  // private final Factory<SimpleJdbcCall> spFactory;
  // private final Factory<SimpleJdbcCall> resultSetSpFactory;

  private final ConcurrentMap<String, SimpleJdbcInsert> insertMap = new ConcurrentHashMap<String, SimpleJdbcInsert>();
  private Factory<SimpleJdbcInsert> insertFactory;
  //private final LobHandler lobHandler;

  // private static final Map<String, Object> emptyMap = Collections.emptyMap();

//  protected RowProcessor rowProcessor;
  
  public SpringSqlUtil(final JdbcTemplate jdbcTemplate, DataSource dataSource, LobHandler lobHandler) {
    
    super(dataSource, new BasicRowProcessor(new SpringBeanProcessor(lobHandler)));
    if (jdbcTemplate == null) {
      throw new NullPointerException("jdbcTemplate cannot be null");
    }
    //this.lobHandler = lobHandler;
    this.jdbcTemplate = jdbcTemplate;
    
    this.insertFactory = new Factory<SimpleJdbcInsert>() {

      @Override
      public SimpleJdbcInsert create(Object... objects) {

        String tableName = (String) objects[0];
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName(tableName).usingGeneratedKeyColumns(autoGenerateColumnName);

        //sji.compile();
        return sji;
      }
    };
  }
  
  public SpringSqlUtil(JdbcTemplate jdbcTemplate, DataSource dataSource) {
    this(jdbcTemplate, dataSource, new DefaultLobHandler());
  }

  protected JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int update(String sql, Object... params) {

    convertParams(params);
    return updateWithoutParamCheck(sql, params);
  }

  private int updateWithoutParamCheck(String sql, Object... params) {
    
    try {
      return jdbcTemplate.update(sql, params);
    }
    catch (DataAccessException e){
      throw new DatabaseException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int delete(TableEntity entity) {

    SqlStatement sset = StatementUtil.buildDeleteStatement(entity);

    return this.update(sset.getSql(), sset.getParams());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int insert(TableEntity entity) {

    String tableName = entity.getTableName();
    if(tableName == null) throw new NullPointerException("TableEntity tableName cannot be null.");
    
    SimpleJdbcInsert insert = CollectionUtil.getFromConcurrentMap(insertMap, tableName, insertFactory);
    Map<String, Object> params = TableEntityUtil.convert(entity);

    int cnt = 0;
    if(entity instanceof IdKeyedTableEntity) {
      try {
        // insert.usingColumns((String[]) params.keySet().toArray());
        Number id = insert.executeAndReturnKey(params);
        ((IdKeyedTableEntity) entity).setId(id.longValue());
        cnt = 1;
      }
      finally {
        // Currently SimpleJdbcInsert doesn't call
        // StatementCreatorUtils.cleanupParameters() automatically
        StatementCreatorUtils.cleanupParameters(params.values());
      }
    }
    else {
      cnt = insert.execute(params);
    }
    return cnt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int update(TableEntity entity) {

    // entity.setModify_dt(DateUtil.getTimestamp());
    SqlStatement sset = StatementUtil.buildPreparedUpdateStatement(entity, false);
    // if(AppStaticConfig.BASE_DEBUG) {
    // LoggerUtil.debug(sset.toString());
    // }
    int cnt = this.updateWithoutParamCheck(sset.getSql(), sset.getParams());

    return cnt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int updateWithNull(TableEntity entity) {

    SqlStatement sset = StatementUtil.buildPreparedUpdateStatement(entity, true);
    return this.updateWithoutParamCheck(sset.getSql(), sset.getParams());
  }

}
