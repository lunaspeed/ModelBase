
package com.lunary.database.impl;

import java.text.MessageFormat;

import com.lunary.database.TopSqlTemplate;

public class MySqlTopSqlTemplate implements TopSqlTemplate {

  private MessageFormat topFormat = new MessageFormat("{0} limit {1}");
  
  @Override
  public String formatTopSql(String sql, int top) {
    return topFormat.format(new Object[] {sql, String.valueOf(top)});
  }

}
