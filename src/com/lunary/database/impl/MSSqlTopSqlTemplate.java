package com.lunary.database.impl;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import com.lunary.database.TopSqlTemplate;

public class MSSqlTopSqlTemplate implements TopSqlTemplate {

  private MessageFormat topFormat = new MessageFormat("SELECT TOP {0} ");
  private Pattern topPattern = Pattern.compile("^\\s*SELECT ", Pattern.CASE_INSENSITIVE);
  private MessageFormat topDistinctFormat = new MessageFormat("SELECT DISTINCT TOP {0} ");
  private Pattern topDistinctPattern = Pattern.compile("^\\s*SELECT DISTINCT ", Pattern.CASE_INSENSITIVE);
  private Pattern topDistinctMatchPattern = Pattern.compile("^\\s*SELECT DISTINCT .+", Pattern.CASE_INSENSITIVE);

  @Override
  public String formatTopSql(String sql, int top) {

    if (topDistinctMatchPattern.matcher(sql).matches()) {
      sql = topDistinctPattern.matcher(sql).replaceFirst(topDistinctFormat.format(new Object[] {top}));
    }
    else {
      sql = topPattern.matcher(sql).replaceFirst(topFormat.format(new Object[] {top}));
    }
    return sql;
  }

  // public static void main(String[] arg) {
  //
  // MSSqlTopSqlTemplate t = new MSSqlTopSqlTemplate();
  // String s =
  // "SELECT DISTINCT F.LIPPER_ID, F.FUND_NAME, FUND_CNAME FROM VF_FUND_COMPANY FC INNER JOIN VF_COMPANY C ON FC.COMPANY_CODE = C.COMPANY_CODE INNER JOIN VF_FUND F ON FC.LIPPER_ID = F.LIPPER_ID WHERE C.COMPANY_CODE = ? ORDER BY F.FUND_CNAME";
  // System.out.println(t.topDistinctMatchPattern.matcher(s).matches());
  // System.out.println(t.topDistinctPattern.matcher(s).replaceFirst(t.topDistinctFormat.format(new
  // Object[]{10})));
  // System.out.println(t.topPattern.matcher(s).matches());
  // System.out.println(t.topPattern.matcher(s).replaceFirst(t.topFormat.format(new
  // Object[]{10})));
  // char[] cs = "09aA".toCharArray();
  // for(char c : cs) {
  // //Character cc = c;
  // System.out.println((int) c);
  // }
  // }
}
