package com.lunary.database.impl;

import java.text.MessageFormat;

import com.lunary.database.TopSqlTemplate;

public class OracleTopSqlTemplate implements TopSqlTemplate {

    private MessageFormat topFormat = new MessageFormat("SELECT * FROM ({0}) a WHERE ROWNUM <= {1} ");
    
    @Override
    public String formatTopSql(String sql, int top) {
        return topFormat.format(new Object[] {sql, String.valueOf(top)});
    }

}
