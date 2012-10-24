package com.lunary.database;

public interface TopSqlTemplate {

    public String formatTopSql(String sql, int top);
}
