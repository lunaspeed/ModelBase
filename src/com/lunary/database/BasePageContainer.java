
package com.lunary.database;

import java.util.Iterator;
import java.util.List;

import com.lunary.util.CollectionUtil;

/**
 * Simple implementation of PageContainer
 * 
 * @author Steven
 * 
 * @param <E>
 *          type of Bean contained
 */
public class BasePageContainer<E> implements PageContainer<E> {

  private List<E> rows;
  private int totalPages;
  private int totalRows;

  @Override
  public List<E> getRows() {
    return rows;
  }

  @Override
  public void setRows(List<E> rows) {
    this.rows = rows;
  }

  @Override
  public int getTotalPages() {
    return totalPages;
  }

  @Override
  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public int getTotalRows() {
    return totalRows;
  }

  @Override
  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  @Override
  public Iterator<E> iterator() {

    Iterator<E> ite = null;
    if (!CollectionUtil.isEmpty(rows)) {
      ite = rows.iterator();
    }
    return ite;
  }

}
