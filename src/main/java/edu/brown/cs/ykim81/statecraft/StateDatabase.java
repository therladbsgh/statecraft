package edu.brown.cs.ykim81.statecraft;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

/**
 * Created by therl on 4/15/2017.
 */
public class StateDatabase extends Database {

  String dbName;
  String tableName = "state";

  public StateDatabase(Main instance) {
    super(instance);
    dbName = plugin.getConfig().getString("SQLite.Filename", "statecraft");
  }

  public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
          "`id` INTEGER NOT NULL," +
          "`name` TEXT NOT NULL," +
          "`money` REAL NOT NULL," +
          "`tax` INTEGER NOT NULL," +
          "PRIMARY KEY (`id`)" +
          ");";


  @Override
  public Connection getSqlConnection() {
    File dataFolder = new File(plugin.getDataFolder(), dbName + ".sqlite");
    if (!dataFolder.exists()){
      try {
        dataFolder.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().log(Level.SEVERE, "File write error: "+ dbName + ".sqlite");
      }
    }

    try {
      if(connection != null && !connection.isClosed()){
        return connection;
      }
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
      return connection;
    } catch (SQLException ex) {
      plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
    } catch (ClassNotFoundException ex) {
      plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
    }

    return null;
  }

  @Override
  public void load() {
    connection = getSqlConnection();
    try {
      Statement s = connection.createStatement();
      s.executeUpdate(SQLiteCreateTokensTable);
      s.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    initialize();
  }

  @Override
  public void initialize() {
    connection = getSqlConnection();
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", e);
    }
  }
}
