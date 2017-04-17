package edu.brown.cs.ykim81.statecraft.database;

import edu.brown.cs.ykim81.statecraft.Main;

import java.util.logging.Level;

/**
 * Created by therl on 4/15/2017.
 */
public class Error {

  public static void execute(Main plugin, Exception ex){
    plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
  }
  public static void close(Main plugin, Exception ex){
    plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
  }

}
