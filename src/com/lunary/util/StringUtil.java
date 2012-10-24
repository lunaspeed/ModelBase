package com.lunary.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for processing String
 * 
 * @author Steven
 * 
 */
public final class StringUtil {

  private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
  
  private StringUtil() {

  }

  /**
   * 
   * Trims a String with leading and trailing whitespace omitted with null
   * check.
   * 
   * @param s
   *          String to be trimmed
   * @return Returns a copy of the string, with leading and trailing whitespace
   *         omitted. Null if s is null.
   */
  public static String trim(String s) {

    if (s == null) return null;
    return s.trim();
  }

  public static String[] split(String str, char delim) {

    int n = 1;
    int index = -1;
    while (true) {
      index = str.indexOf(delim, index + 1);
      if (index == -1) break;
      ++n;
    }
    String[] strs = new String[n];
    index = -1;
    for (int i = 0; i < n - 1; ++i) {
      int nextIndex = str.indexOf(delim, index + 1);
      strs[i] = str.substring(index + 1, nextIndex);
      index = nextIndex;
    }
    strs[n - 1] = str.substring(index + 1);
    return strs;
  }

  public static String[] split(String str, String delimiter) {

    int n = 1;
    int index = -1;
    while (true) {
      index = str.indexOf(delimiter, index + delimiter.length());
      if (index == -1) break;
      ++n;
    }
    String[] strs = new String[n];
    index = -1;
    for (int i = 0; i < n - 1; ++i) {
      int nextIndex = str.indexOf(delimiter, index + delimiter.length());
      strs[i] = str.substring(index + delimiter.length(), nextIndex);
      index = nextIndex;
    }
    strs[n - 1] = str.substring(index + delimiter.length());
    return strs;
  }

  public static Set<String> splitCommas(String str) {

    if (isEmpty(str)) {
      return Collections.emptySet();
    }
    StringTokenizer tokens = new StringTokenizer(str, ",");
    Set<String> elements = new HashSet<String>();
    while (tokens.hasMoreTokens()) {
      String token = tokens.nextToken();
      token = token.trim();
      if (!StringUtil.isEmpty(token)) {
        elements.add(token.trim());
      }
    }
    return elements;
  }

  /**
   * Capitalize a <code>String</code>, changing the first letter to upper case
   * as per {@link Character#toUpperCase(char)}. No other letters are changed.
   * 
   * @param str
   *          the String to capitalize, may be <code>null</code>
   * @return the capitalized String, <code>null</code> if null
   */
  public static String capitalize(String str) {
    return changeFirstCharacterCase(str, true);
  }

  /**
   * Uncapitalize a <code>String</code>, changing the first letter to lower case
   * as per {@link Character#toLowerCase(char)}. No other letters are changed.
   * 
   * @param str
   *          the String to uncapitalize, may be <code>null</code>
   * @return the uncapitalized String, <code>null</code> if null
   */
  public static String uncapitalize(String str) {
    return changeFirstCharacterCase(str, false);
  }

  private static String changeFirstCharacterCase(String str, boolean capitalize) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder buf = new StringBuilder(str.length());
    if (capitalize) {
      buf.append(Character.toUpperCase(str.charAt(0)));
    }
    else {
      buf.append(Character.toLowerCase(str.charAt(0)));
    }
    buf.append(str.substring(1));
    return buf.toString();
  }

  /**
   * 
   * 
   * @param strs
   *          array to be joined
   * @param delimiter
   *          a String to be join in between elements
   * @return a joined String from {@code strs Array}
   */
  public static String join(String[] strs, String delimiter) {

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strs.length; ++i) {
      if (i > 0) sb.append(delimiter);
      sb.append(strs[i]);
    }

    return sb.toString();
  }

  /**
   * 
   * Test whether a String is null or ""
   * 
   * @param str
   * @return boolean
   */
  public static boolean isEmpty(String str) {

    boolean empty = false;
    if (str == null || str.trim().equals("")) {
      empty = true;
    }
    return empty;
  }

  /**
   * 
   * Fill up zero {@code "0"} in-front of the number until length is reached
   * 
   * @param number
   * @param length
   * @return filled String
   */
  public static String fillZero(String number, int length) {

    if (number == null) return null;
    StringBuilder s = new StringBuilder(number);
    int srcLength = number.length();
    for (int i = 0; i < (length - srcLength); i++) {
      s.insert(0, "0");
    }
    return s.toString();
  }

  /**
   * 
   * Fill up zero {@code "0"} in-front of the number until length is reached
   * 
   * @param number
   * @param length
   * @return filled String
   */
  public static String fillZero(Number number, int length) {

    if (number == null) return null;
    return fillZero(String.valueOf(number), length);
  }

  /**
   * 
   * Fill spaces on the left
   * 
   * @param len
   * @param val
   * @return filled String
   */
  public static String fillLeftSpace(int len, String val) {

    if (len < 0) return val;

    StringBuffer sb_val = new StringBuffer("");
    try {
      for (int i = 0; i < len - val.getBytes().length; i++)
        sb_val.append(" ");
    }
    catch (Exception e) {
      logger.debug("Error in fillLeftSpace: " + e.getMessage(), e);
      return "";
    }

    sb_val.append(val);
    return sb_val.toString();
  }

  /**
   * 
   * Fill spaces on the right
   * 
   * @param len
   * @param val
   * @return filled String
   */
  public static String fillRightSpace(int len, String val) {

    if (len < 0) return val;

    StringBuffer sb_val = new StringBuffer(val);
    try {
      for (int i = 0; i < len - val.getBytes("Big5").length; i++)
        sb_val.append(" ");
    }
    catch (Exception e) {
      logger.debug("Error in fillRightSpace: " + e.getMessage(), e);
      return "";
    }
    return sb_val.toString();
  }

//  /**
//   * Turn 西元年 to 民國年
//   * 
//   * @param d
//   * @param length
//   * @return String
//   */
//  public static String getTaiwanYear(Date d, int length) {
//
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(d);
//    int y = cal.get(Calendar.YEAR) - 1911;
//    String ret = "  " + y;
//    return ret.substring(ret.length() - length);
//  }

  /**
   * 
   * Turn a String into Integer
   * 
   * @param value
   *          String to be converted to Integer
   * @return value as Integer, null if value is empty
   * @throws NumberFormatException
   *           if value is not a valid representation of an Integer.
   * @see StringUtil#isEmpty
   */
  public static Integer asInteger(String value) {
    return asInteger(value, null);
  }

  /**
   * 
   * Turn a String into Integer
   * 
   * @param value
   *          String to be converted to Integer
   * @param defaultValue
   *          default value if value is empty
   * @return value as Integer, or defaultValue if value is empty
   * @throws NumberFormatException
   *           if value is not a valid representation of an Integer.
   * @see StringUtil#isEmpty
   */
  public static Integer asInteger(String value, Integer defaultValue) throws NumberFormatException {

    Integer val = null;
    if (!isEmpty(value)) {
      val = new Integer(value);
    }
    else {
      val = defaultValue;
    }
    return val;
  }

  /**
   * 
   * Turn a String into BigDecimal
   * 
   * @param value
   *          String to be converted to BigDecimal
   * @return value as BigDecimal, null if value is empty
   * @throws NumberFormatException
   *           if value is not a valid representation of a BigDecimal.
   * @see StringUtil#isEmpty
   */
  public static BigDecimal asBigDecimal(String value) {
    return asBigDecimal(value, null);
  }

  /**
   * 
   * Turn a String into BigDecimal
   * 
   * @param value
   *          String to be converted to BigDecimal
   * @param defaultValue
   *          default value if value is empty
   * @return value as BigDecimal, or defaultValue if empty
   * @throws NumberFormatException
   *           if value is not a valid representation of a BigDecimal.
   * @see StringUtil#isEmpty
   */
  public static BigDecimal asBigDecimal(String value, BigDecimal defaultValue) throws NumberFormatException {

    BigDecimal val = null;
    if (!isEmpty(value)) {
      val = new BigDecimal(value);
    }
    else {
      val = defaultValue;
    }
    return val;
  }

  // /**
  // *
  // * Turn a String into YesNo
  // * @param value String to be converted to YesNo
  // * @return value as YesNo, null if value is empty
  // * @throws IllegalArgumentException if the value is not a legal
  // representation of YesNo
  // * @see StrUtil#isEmpty
  // */
  // public static YesNo asYesNo(String value) {
  // return asYesNo(value, null);
  // }
  //
  // /**
  // *
  // * Turn a String into YesNo
  // * @param value String to be converted to YesNo
  // * @param defaultValue default value if value is empty
  // * @return value as YesNo or defaultValue if is empty
  // * @throws IllegalArgumentException if the value is not a legal
  // representation of YesNo
  // * @see StrUtil#isEmpty
  // */
  // public static YesNo asYesNo(String value, YesNo defaultValue) throws
  // IllegalArgumentException {
  //
  // YesNo val = null;
  // if(!isEmpty(value)) {
  // val = Enum.valueOf(YesNo.class, value);
  // }
  // else {
  // val = defaultValue;
  // }
  // return val;
  // }

//  /**
//   * 
//   * Escape string for JavaScript
//   * 
//   * @param str
//   *          string to be escaped
//   * @return escaped str, null if str is null
//   */
//  public static String escapeJavaScript(String str) {
//
//    if (str == null) {
//      return null;
//    }
//    else {
//      return StringEscapeUtils.escapeJavaScript(str);
//    }
//  }
//  private final static Pattern userAgentPattern = Pattern.compile("(.*Googlebot.*)|(.*www\\.xml-sitemaps\\.com.*)");//TODO add other SE pattern
//  public static void main(String[] args) {
//    System.out.println(userAgentPattern.matcher("Mozilla/5.0 (compatible; XML Sitemaps Generator; http://www.xml-sitemaps.com) Gecko XML-Sitemaps/1.0").matches());
//    System.out.println(userAgentPattern.matcher("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").matches());
//    System.out.println(userAgentPattern.matcher("Googlebot/2.1 (+http://www.googlebot.com/bot.html)").matches());
//    System.out.println(userAgentPattern.matcher(" Googlebot/2.1 (+http://www.google.com/bot.html)").matches());
//    System.out.println(userAgentPattern.matcher(" Googebotxxx").matches());
//  }
}