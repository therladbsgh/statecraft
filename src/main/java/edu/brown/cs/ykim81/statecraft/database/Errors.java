package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/15/2017.
 */
public class Errors {

  public static String sqlConnectionExecute(){
    return "Couldn't execute MySQL statement: ";
  }
  public static String sqlConnectionClose(){
    return "Failed to close MySQL connection: ";
  }
  public static String noSQLConnection(){
    return "Unable to retreive MYSQL connection: ";
  }
  public static String noTableFound(){
    return "Database Error: No Table Found";
  }

}
