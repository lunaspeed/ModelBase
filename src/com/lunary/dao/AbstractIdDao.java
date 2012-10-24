
package com.lunary.dao;

import com.lunary.model.IdKeyedTableEntity;

public abstract class AbstractIdDao<T extends IdKeyedTableEntity> extends AbstractDao<T> {
  
  
  public T findById(Long id) {
    return getSqlUtil().findOne("SELECT * FROM "+getInstance().getTableName()+" WHERE "+IdKeyedTableEntity.ID+" = ?", getModelClass(), id);
  }
  
  public int deleteById(Long id) {
    return getSqlUtil().update("DELETE FROM "+getInstance().getTableName()+" WHERE "+IdKeyedTableEntity.ID+" = ?", id);
  }
  
}
