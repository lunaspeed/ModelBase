
package com.lunary.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lunary.database.exception.DataMappingException;

public interface ColumnMapper {

  public static final Object UNKNOWN = new Object();
  
  /**
   * 
   * Maps a column value in the ResultSet to the right instance of the Class 
   * @param rs ResultSet to retrieve the value from
   * @param index the index of the column to retrieve value for
   * @param propType the Class to be mapped to
   * @return a mapped value of Class propType. UNKNOWN if cannot handle the mapping
   * @throws SQLException thrown when error occurs when working with ResultSet
   * @throws DataMappingException thrown when and expected mapping goes wrong. ie. invalid Enum value in DB
   */
  public Object toObject(ResultSet rs, int index, Class<?> propType) throws SQLException, DataMappingException;
  
}
