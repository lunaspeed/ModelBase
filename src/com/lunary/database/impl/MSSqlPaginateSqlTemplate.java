package com.lunary.database.impl;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class MSSqlPaginateSqlTemplate extends AbstractPaginateSqlTemplate {

  private final MessageFormat pformat = new MessageFormat("SELECT * FROM ({0}) ta WHERE ta.rn BETWEEN {1} AND {2} ORDER BY rn ");
  private final MessageFormat overFormat = new MessageFormat(", ROW_NUMBER() OVER(ORDER BY {0}) rn FROM ");
  private final Pattern pattern = Pattern.compile(".*(\\sUNION\\s|\\sEXCEPT\\s|\\sINTERSECT\\s).*", Pattern.CASE_INSENSITIVE);
  private final Pattern commaPattern = Pattern.compile(",");
  private final Pattern fromPattern = Pattern.compile(" FROM ", Pattern.CASE_INSENSITIVE);

  @Override
  protected String formatProcessedPaginateSqlFormat(String sql, String orderByColumns, int startRow, int endRow, boolean sqlWrapped) {

    orderByColumns = sqlWrapped ? purifiedOrderBy(orderByColumns) : orderByColumns;
    String over = overFormat.format(new Object[] {orderByColumns});
    sql = fromPattern.matcher(sql).replaceFirst(over);
    return pformat.format(new Object[] {sql, String.valueOf(startRow), String.valueOf(endRow)});
  }

  @Override
  protected Pattern getWrappingExpression() {
    return pattern;
  }

  private String purifiedOrderBy(String orderByColumns) {

    String[] cs = commaPattern.split(orderByColumns);
    StringBuilder s = new StringBuilder();
    boolean begin = true;
    for (String c : cs) {
      if (!begin) {
        s.append(",");
      }
      else {
        begin = false;
      }
      if (c.contains(".")) {
        c = c.substring(c.indexOf(".") + 1);
      }
      s.append(c);
    }

    return s.toString();
  }

}
