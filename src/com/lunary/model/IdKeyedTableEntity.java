package com.lunary.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This provided the basic implementation of TableEntity and contains the basic
 * fields that most tables have.
 * 
 * @author Steven
 * 
 */
public abstract class IdKeyedTableEntity implements TableEntity {

  public static final String ID = "id";
  
  @SuppressWarnings("serial")
  private static final Set<String> BASE_KEYS = Collections.unmodifiableSet(new HashSet<String>(){
    {add(ID);}
  });

  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  @Override
  public Set<String> getKeyNames() {
    return BASE_KEYS;
  }

  /**
   * 
   * create a Set with String "id" in it for easier extension to add key names
   * 
   * @return a Set with only the String "id" in it
   */
  protected static Set<String> getModifiableKeysWithIdOnly() {

    Set<String> key = new HashSet<String>();
    key.add("id");
    return key;
  }

}
