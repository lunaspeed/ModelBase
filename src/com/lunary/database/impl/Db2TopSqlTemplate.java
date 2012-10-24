package com.lunary.database.impl;

import java.text.MessageFormat;

import com.lunary.database.TopSqlTemplate;

public class Db2TopSqlTemplate implements TopSqlTemplate {

    private MessageFormat topFormat = new MessageFormat("{0} fetch first {1} rows only ");
    
    @Override
    public String formatTopSql(String sql, int top) {
        return topFormat.format(new Object[] {sql, String.valueOf(top)});
    }

}
