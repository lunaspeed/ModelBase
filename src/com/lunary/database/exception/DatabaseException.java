
package com.lunary.database.exception;

public class DatabaseException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -7614759730166083557L;

  public DatabaseException(Throwable e) {
    super(e);
  }
  
  public DatabaseException(String message, Exception e) {
    super(message, e);
  }
  
  public DatabaseException(String message) {
    super(message);
  }

}
