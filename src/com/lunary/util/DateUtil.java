package com.lunary.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common util for Date
 * 
 * @author Steven
 * 
 */
public class DateUtil {

  private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);
  /**
   * Date pattern in yyyy/MM
   */
  public static final String PATTERN_YYYY_MM = "yyyy/MM";
  /**
   * Date pattern in yyyy/MM/dd
   */
  public static final String PATTERN_YYYY_MM_DD = "yyyy/MM/dd";
  /**
   * Date pattern in yyyy-MM-dd HH:mm:ss
   */
  public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_DASH = "yyyy-MM-dd HH:mm:ss";
  /**
   * Date pattern in yyyy/MM/dd HH:mm:ss
   */
  public static final String PATTERN_YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
  /**
   * Date pattern in yyyyMMdd
   */
  public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
  private static final Locale DEFAULT_LOCALE = Locale.US;

  public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";//AppStaticConfig.DATE_FORMAT;

  //private static final FastDateFormat defaultDateFormat = FastDateFormat.getInstance(DEFAULT_DATE_FORMAT);
  private static final DateTimeFormatter defaultDateFormat = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT);

  public static Date getQuarterFirstDate() {
    return getQuarterFirstDate(null);
  }

  public static Date getQuarterFirstDate(Date date) {

    Calendar cal = now();
    if (date != null) {
      cal.setTime(date);
    }
    cal.set(Calendar.DATE, 1);
    int month = cal.get(Calendar.MONTH) + 1;
    int quarterMonth = 0;

    if (month > 3 && month < 7) quarterMonth = 3;
    else if (month > 6 && month < 10) quarterMonth = 6;
    else if (month > 9) quarterMonth = 9;

    cal.set(Calendar.MONTH, quarterMonth);
    return cal.getTime();
  }

  /**
   * Create a Date with day of month = 1
   * 
   * @return today with Date field as 1.
   */
  public static Date getFirstDayOfCurrentMonth() {

    Calendar now = now();
    now.set(Calendar.DATE, 1);
    return now.getTime();
  }

  /**
   * 
   * Format the date using default system date format
   * {@link com.bi.base.config.AppStaticConfig#DATE_FORMAT}
   * 
   * @param date
   * @return string date
   */
  public static String defaultDateFormater(Date date) {
    if (date == null) return null;
    return defaultDateFormat.print(date.getTime());
  }

  /**
   * 
   * Parse the date in default system date format<br/>
   * 
   * @param date
   * @return Date
   * @throws IllegalArgumentException
   *           if date is not in default system date format
   *           {@link com.bi.base.config.AppStaticConfig#DATE_FORMAT}
   */
  public static Date parseDefaultDate(String date) throws IllegalArgumentException {

    if (date == null) return null;
    return defaultDateFormat.parseDateTime(date).toDate();//new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(date);
  }

  /**
   * 
   * Format a date with pattern with default Locale of US
   * 
   * @param date
   * @param pattern
   * @return String representation of the date
   */
  public static String dateFormater(Date date, String pattern) {

    return dateFormater(date, pattern, null);
  }

  /**
   * 
   * Format a date with pattern with default Locale of US if locale not given
   * 
   * @param date
   * @param pattern
   * @param locale
   * @return String representation of the date
   */
  public static String dateFormater(Date date, String pattern, Locale locale) {

    String formatedDate = null;
    if (DEFAULT_DATE_FORMAT.equals(pattern)) {
      formatedDate = defaultDateFormater(date);
    }
    else if (date != null) {
      if (locale == null) locale = DEFAULT_LOCALE;
      SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
      formatedDate = formatter.format(date);
    }
    return formatedDate;
  }

  /**
   * 
   * Format a Calendar date with pattern with default Locale of US
   * 
   * @param date
   * @param pattern
   * @return String representation of the date
   */
  public static String calendarFormater(Calendar date, String pattern) {
    return calendarFormater(date, pattern, null);
  }

  /**
   * 
   * Format a Calendar date with pattern with default Locale of US if locale not
   * given
   * 
   * @param date
   * @param pattern
   * @param locale
   * @return String representation of the date
   */
  public static String calendarFormater(Calendar date, String pattern, Locale locale) {

    return dateFormater(date.getTime(), pattern, locale);
  }

  /**
   * 
   * Get a Calendar that has current time
   * 
   * @return GregorianCalendar
   */
  public static Calendar now() {
    return GregorianCalendar.getInstance();
  }

  /**
   * 
   * Construct a new Calendar based on parameters given
   * 
   * @param year
   * @param month
   * @param day
   * @return Calendar
   */
  public static Calendar getDate(int year, int month, int day) {

    month = month - 1; // month is 0 based
    return new GregorianCalendar(year, month, day);
  }

  // public static String getChineseDate() {
  //
  // StringBuffer date = new StringBuffer();
  // Calendar calendar = GregorianCalendar.getInstance();
  // String y = StrUtil.fillZero(String.valueOf(calendar.get(Calendar.YEAR) -
  // 1911), 3);
  // String m = StrUtil.fillZero(String.valueOf(calendar.get(Calendar.MONTH) +
  // 1), 2);
  // String d =
  // StrUtil.fillZero(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);
  // date.append(y).append(m).append(d);
  // return date.toString();
  // }

  // public static String getChineseDateWithoutDay() {
  //
  // String dateString = getChineseDate();
  // return dateString.substring(0, dateString.length() - 2);
  // }

  /**
   * Add days to the date given If date is null, use current time instead
   * automatically
   * 
   * @param date
   * @param day
   * @return a new Date with date equals {@code date + day days}
   */
  public static Date addDate(Date date, int day) {

    Calendar calendar = GregorianCalendar.getInstance();
    if (date != null) calendar.setTime(date);
    calendar.add(Calendar.DATE, day);
    return calendar.getTime();
  }

  /**
   * 
   * Check the gap between 2 Dates
   * 
   * @param type
   *          supports Calendar.HOUR, Calendar.DATE, Calendar.MONTH,
   *          Calendar.YEAR
   * @param startDate
   *          start date
   * @param endDate
   *          end date
   * @return the gap between 2 dates, unit based on "type" given
   */
  public static long checkGap(int type, Date startDate, Date endDate) {

    long gap = 0;
    Calendar start = new GregorianCalendar();
    start.setTime(startDate);
    Calendar end = new GregorianCalendar();
    end.setTime(endDate);

    // long millis = start.getTimeInMillis() - end.getTimeInMillis();
    BigDecimal millis = new BigDecimal(start.getTimeInMillis() - end.getTimeInMillis());
    switch (type) {
      case Calendar.HOUR:
        gap = millis.divide(new BigDecimal(1000 * 60 * 60), BigDecimal.ROUND_DOWN).longValue();
        break;
      case Calendar.DATE:
        // gap = millis / (1000 * 60 * 60 * 24);
        gap = millis.divide(new BigDecimal(1000 * 60 * 60 * 24), BigDecimal.ROUND_DOWN).longValue();
        break;
      case Calendar.MONTH:
        // gap = millis / (1000 * 60 * 60 * 24 * 30);
        gap = millis.divide(new BigDecimal(1000 * 60 * 60 * 24 * 30), BigDecimal.ROUND_CEILING).longValue();
        break;
      case Calendar.YEAR:
        // gap = millis / (1000 * 60 * 60 * 24 * 30 * 365);
        gap = millis.divide(new BigDecimal(1000 * 60 * 60 * 24 * 30 * 365), BigDecimal.ROUND_DOWN).longValue();
        break;
      default:
        throw new UnsupportedOperationException("checkGap type is not one of Calendar.{HOUR, DATE, MONTH, YEAR}");
    }

    if (gap < 0) gap = 0 - gap;

    return gap;
  }

  /**
   * 
   * Calculates the age of the given birthDay
   * 
   * @param birthDay
   * @return age
   * @throws NullPointerException
   *           - if birthDay is null
   */
  public static int calculateAge(Date birthDay) {

    if (birthDay == null) {
      throw new NullPointerException("birthDay cannot be null");
    }

    Calendar birth_c = Calendar.getInstance();
    birth_c.setTime(birthDay);
    return calculateAge(birth_c);

    //
    // Date today = new Date(System.currentTimeMillis());
    // Calendar c2 = Calendar.getInstance();
    // c2.setTime(today);
    //
    // long remaindar = c2.getTimeInMillis() - birth_c.getTimeInMillis();
    //
    // long aYear = (long) 1000 * (long) 60 * (long) 60 * (long) 24 * (long)
    // 365;
    //
    // int age = (int) (remaindar / aYear);
    // return age;
  }

  /**
   * 
   * Calculates the age of the given birthDay
   * 
   * @param birthDay
   * @return age
   * @throws NullPointerException
   *           - if birthDay is null
   */
  public static int calculateAge(Calendar birthDay) {

    if (birthDay == null) {
      throw new NullPointerException("birthDay cannot be null");
    }

    return calculateAge(birthDay.get(Calendar.YEAR), birthDay.get(Calendar.MONTH) + 1, birthDay.get(Calendar.DATE));
  }

  /**
   * 
   * Calculates the age of the given birthDay
   * 
   * @param bdYear
   *          year of birth
   * @param bdMonth
   *          month of birth, 1 based month
   * @param bdDate
   *          date of birth
   * @return age
   */
  public static int calculateAge(int bdYear, int bdMonth, int bdDate) {

    Calendar now = now();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH) + 1;
    int date = now.get(Calendar.DATE);
    int age = year - bdYear + 1;

    if (month < bdMonth) {
      --age;
    }
    else if (bdMonth == month && date < bdDate) {
      --age;
    }

    return age;
  }

  /**
   * parses a <code>String</code> into <code>Date</code>
   * 
   * @param rawDate
   *          String date to be parsed
   * @param pattern
   *          a pattern that the String date follows
   * @return date <code>Date</code>, null if rawDate is null
   * @throws IllegalArgumentException - if the given pattern is invalid
   *           NullPointerException - if the given pattern is null
   */
  public static Date parseDate(String rawDate, String pattern) throws IllegalArgumentException {

    Date date = null;
    // if (AppStaticConfig.DATE_FORMAT.equals(pattern)) {
    // date = parseDefaultDate(rawDate);
    // }
    // else
    if(pattern == null) throw new NullPointerException("Pattern is null");
    if (rawDate != null) {
      //SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
      date = DateTimeFormat.forPattern(pattern).parseDateTime(rawDate).toDate();
    }
    return date;
  }

  /**
   * 
   * Compares 2 dates without time, using Comparator rules.<br>
   * Null value is treated as smaller.
   * 
   * @param date1
   *          first date
   * @param date2
   *          second date
   * @return -1: first ealier, 0: equal, 1: first later
   * @throws NullPointerException
   *           if both dates are null
   */
  public static int compareDatesWithoutTime(Date date1, Date date2) throws NullPointerException {

    if (date1 == null && date2 == null) throw new NullPointerException("Cannot compare 2 null dates.");
    if (date1 == null) return -1;
    if (date2 == null) return 1;
    Calendar cal1 = clearTime(date1);
    Calendar cal2 = clearTime(date2);
    return cal1.compareTo(cal2);
  }

  /**
   * 
   * Clears the time fields of the date.
   * 
   * @param date
   *          Date to be cleared
   * @return a new instance of Calendar representation of date with time fields
   *         equal to 0
   */
  public static Calendar clearTime(Date date) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }

  /**
   * get the last date of the month year is required for leap years
   * 
   * @param year
   * @param month
   * @return date
   */
  public static int getLastDateOfMonth(int year, int month) {

    Calendar c = getDate(year, month, 1);
    c.add(Calendar.MONTH, 1);
    c.add(Calendar.DATE, -1);
    return c.get(Calendar.DATE);
  }

  /**
   * 
   * Get current java.sql.Date
   * 
   * @return Date
   */
  public static java.sql.Date getSqlDate() {
    return new java.sql.Date(System.currentTimeMillis());
  }

  /**
   * 
   * Converts java.util.Date to java.sql.Date
   * 
   * @param date
   * @return null if date is null
   */
  public static java.sql.Date getSqlDate(Date date) {
    if (date == null) return null;
    return new java.sql.Date(date.getTime());
  }

  /**
   * Get current Timestamp
   * 
   * @return Timestamp
   */
  public static Timestamp getTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * 
   * Turn java.util.Date to Timestamp
   * 
   * @param date
   * @return null if date is null
   */
  public static Timestamp getTimestamp(Date date) {
    if (date == null) return null;
    return new Timestamp(date.getTime());
  }
  
  @SuppressWarnings("serial")
  public static class JodaDateFormat extends DateFormat {

    private final DateTimeFormatter formatter;
    
    public JodaDateFormat(String pattern) {
      this(DateTimeFormat.forPattern(pattern));
    }
    
    public JodaDateFormat(DateTimeFormatter formatter) {
      this.formatter = formatter;
    }
    
    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
      return toAppendTo.append(formatter.print(date.getTime()));
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
      
      Date d = null;
      try {
        d = formatter.parseDateTime(source).toDate();
        pos.setIndex(source.length() - 1);
      }
      catch(IllegalArgumentException e) {
        pos.setIndex(0);
        logger.debug("error parsing date:" + source, e);
      }
      
      return d;
    }
    
    @Override
    public Object clone() {
      return this;
    }
  };
}
