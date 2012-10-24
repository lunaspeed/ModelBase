
package com.lunary.database.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.lunary.database.util.StatementUtil;
import com.lunary.database.util.StatementUtil.SqlStatement;
import com.lunary.model.IdKeyedTableEntity;
import com.lunary.model.TableEntity;

public class DefaultSqlUtil extends AbstractSqlUtil {

  private final String[] ID_FIELD = new String[]{IdKeyedTableEntity.ID};
  
  public DefaultSqlUtil(DataSource dataSource, RowProcessor rowProcessor) {
    super(dataSource, rowProcessor);
  }

  @Override
  public int update(String sql, Object... params) {
    return convertAndUpdate(sql, params);
  }

  @Override
  public int insert(TableEntity entity) {
    
    SqlStatement sset = StatementUtil.buildPreparedInsertStatement(entity);
    int cnt = 0;
    if(entity instanceof IdKeyedTableEntity) {
      try {
        PreparedStatement stmt = getQueryRunner().getDataSource().getConnection().prepareStatement(sset.getSql(), ID_FIELD); 
        getQueryRunner().fillStatement(stmt, sset.getParams());
        cnt = stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        ResultSetHandler<Long> rsh = new ScalarHandler<Long>();
        ((IdKeyedTableEntity) entity).setId(rsh.handle(rs));
      }
      catch(SQLException e) {
        throw translateException(e);
      }
    }
    else {
      cnt = convertAndUpdate(sset.getSql(), sset.getParams());
    }
    return cnt;
  }

  @Override
  public int update(TableEntity entity) {
    SqlStatement sset = StatementUtil.buildPreparedUpdateStatement(entity, false);
    return convertAndUpdate(sset.getSql(), sset.getParams());
  }

  @Override
  public int updateWithNull(TableEntity entity) {
    SqlStatement sset = StatementUtil.buildPreparedUpdateStatement(entity, true);
    return convertAndUpdate(sset.getSql(), sset.getParams());
  }

  @Override
  public int delete(TableEntity entity) {
    
    SqlStatement sset = StatementUtil.buildDeleteStatement(entity);
    return convertAndUpdate(sset.getSql(), sset.getParams());
  }
  
  private int convertAndUpdate(String sql, Object[] params) {
    int cnt = 0;
    try {
      convertParams(params);
      cnt = getQueryRunner().update(sql, params);
    }
    catch(SQLException e) {
      throw translateException(e);
    }
    return cnt;
  }

}
