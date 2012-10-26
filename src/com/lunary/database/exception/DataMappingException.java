
package com.lunary.database.exception;

public class DataMappingException extends RuntimeException {

  
  /**
   * 
   */
  private static final long serialVersionUID = -8274230762705970568L;

  public DataMappingException(Throwable e) {
    super(e);
  }
  
  public DataMappingException(String message, Exception e) {
    super(message, e);
  }
  
  public DataMappingException(String message) {
    super(message);
  }
}
