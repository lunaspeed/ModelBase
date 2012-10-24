package com.lunary.database;

public interface PaginateSqlTemplate {

    public String formatPaginateSql(String sql, String orderByColumns, int page, int rowsPerPage);
    
    public String formatCountSql(String sql);
}
