
package com.lunary.model;

import java.util.Set;


public interface TableEntity {

  public String getTableName();
  
  public Set<String> getKeyNames();
}
