
package com.lunary.dao;

import java.util.List;

import com.lunary.database.SqlUtil;
import com.lunary.model.TableEntity;


public abstract class AbstractDao<T extends TableEntity> {

  private SqlUtil sqlUtil;
  
  protected SqlUtil getSqlUtil() {
    return sqlUtil;
  }
  
  public int insert(T model) {
    return sqlUtil.insert(model);
  }
  
  public int delete(T model) {
    return sqlUtil.delete(model);
  }
  
  public int update(T model) {
    return sqlUtil.update(model);
  }
  
  public int updateWithNull(T model) {
    return sqlUtil.updateWithNull(model);
  }
  
  public List<T> findAll() {
    return sqlUtil.find("SELECT * FROM "+getInstance().getTableName(), getModelClass());
  }
 
  protected T getInstance() {
    try {
      return getModelClass().newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException("error instantiating class", e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException("error instantiating class with illegal access", e);
    }
  }
  
  protected abstract Class<T> getModelClass();
}
