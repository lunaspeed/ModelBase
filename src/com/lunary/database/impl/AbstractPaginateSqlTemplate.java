package com.lunary.database.impl;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import com.lunary.database.PaginateSqlTemplate;

public abstract class AbstractPaginateSqlTemplate implements PaginateSqlTemplate {

  private final MessageFormat distinctFormat = new MessageFormat("SELECT ptemplate.* FROM ({0}) ptemplate ");
  private final MessageFormat countFormat = new MessageFormat("SELECT COUNT(1) FROM ({0}) a ");
  private final MessageFormat wrapperFormat = new MessageFormat("SELECT b.* FROM ({0}) b ");
  private final Pattern distinctPattern = Pattern.compile(".*\\sDISTINCT\\s.*", Pattern.CASE_INSENSITIVE);

  @Override
  public String formatCountSql(String sql) {
    return countFormat.format(new Object[] {sql});
  }

  @Override
  public String formatPaginateSql(String sql, String orderByColumns, int page, int rowsPerPage) {

    boolean wrapSql = false;
    // if (containsDistinct(sql)) {
    if (distinctPattern.matcher(sql).matches()) {
      sql = distinctFormat.format(new Object[] {sql});
      wrapSql = true;
    }

    if (getWrappingExpression() != null && getWrappingExpression().matcher(sql).matches()) {
      sql = wrapperFormat.format(new Object[] {sql});
      wrapSql = true;
    }
    return formatProcessedPaginateSqlFormat(sql, orderByColumns, calStartRow(page, rowsPerPage), calEndRow(page, rowsPerPage), wrapSql);
  }

  // protected boolean containsDistinct(String sql) {
  // return sql.contains(" DISTINCT ");
  // }

  protected int calStartRow(int page, int rowsPerPage) {
    return ((page - 1) * rowsPerPage) + 1;
  }

  protected int calEndRow(int page, int rowsPerPage) {
    return (page * rowsPerPage);
  }

  protected abstract String formatProcessedPaginateSqlFormat(String sql, String orderByColumns, int startRow, int endRow, boolean sqlWrapped);

  protected abstract Pattern getWrappingExpression();
}
