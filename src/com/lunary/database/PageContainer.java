
package com.lunary.database;

import java.util.List;

/**
 * A basic interface for containing paginated result of type E set information.
 * 
 * @author Steven
 * 
 * @param <E>
 *            type of Bean contained
 */
public interface PageContainer<E> extends Iterable<E> {

    /**
     * 
     * @return total number of pages
     */
    public int getTotalPages();

    /**
     * Sets the total number of pages
     * 
     * @param totalPages
     */
    public void setTotalPages(int totalPages);

    /**
     * 
     * @return the result in current page
     */
    public List<E> getRows();

    /**
     * Sets the result in current page
     * 
     * @param rows
     */
    public void setRows(List<E> rows);

    /**
     * 
     * @return total number of record in the original result
     */
    public int getTotalRows();

    /**
     * Sets the total number of record in the original result
     * 
     * @param totalRows
     */
    public void setTotalRows(int totalRows);
}
