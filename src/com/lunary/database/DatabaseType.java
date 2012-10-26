package com.lunary.database;

/**
 * Provides the type of databases supported and checked by the system.
 * 
 * @author Steven
 * 
 */
public enum DatabaseType {
    /** MS SQL Server */
    MSSQL95,
    MSSQL2008,
    /** IBM DB2 */
    DB2,
    /** Oracle */
    ORACLE,
    /** Sybase SQL Server */
    SYBASE,
    /** MySQL */
    MYSQL;
}
