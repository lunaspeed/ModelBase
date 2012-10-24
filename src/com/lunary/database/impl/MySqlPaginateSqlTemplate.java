
package com.lunary.database.impl;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class MySqlPaginateSqlTemplate extends AbstractPaginateSqlTemplate {

  private final MessageFormat pformat = new MessageFormat("{0} ORDER BY {1} LIMIT {2} OFFSET {3} ");
  
  @Override
  protected String formatProcessedPaginateSqlFormat(String sql, String orderByColumns, int startRow, int endRow, boolean sqlWrapped) {
    
    int rowCnt = endRow - startRow + 1;
    return pformat.format(new String[]{sql, orderByColumns, String.valueOf(rowCnt), String.valueOf(startRow - 1)});
  }

  @Override
  protected Pattern getWrappingExpression() {
    return null;
  }

}
