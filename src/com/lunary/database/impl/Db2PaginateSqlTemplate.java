package com.lunary.database.impl;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class Db2PaginateSqlTemplate extends AbstractPaginateSqlTemplate {

  private final MessageFormat pformat = new MessageFormat("SELECT * FROM ({0}) ta WHERE ta.rn BETWEEN {1} AND {2} ");
  private final MessageFormat overFormat = new MessageFormat(", ROWNUMBER() OVER(ORDER BY {0}) rn FROM ");
  private final Pattern pattern = Pattern.compile(".*(\\sUNION\\s|\\sEXCEPT\\s|\\sINTERSECT\\s).*", Pattern.CASE_INSENSITIVE);
  private final Pattern fromPattern = Pattern.compile(" FROM ", Pattern.CASE_INSENSITIVE);

  @Override
  protected String formatProcessedPaginateSqlFormat(String sql, String orderByColumns, int startRow, int endRow, boolean sqlWrapped) {

    String over = overFormat.format(new Object[] {orderByColumns});
    // sql = sql.replaceFirst(" FROM", over);
    sql = fromPattern.matcher(sql).replaceFirst(over);
    return pformat.format(new Object[] {sql, String.valueOf(startRow), String.valueOf(endRow)});
  }

  @Override
  protected Pattern getWrappingExpression() {
    return pattern;
  }

}
