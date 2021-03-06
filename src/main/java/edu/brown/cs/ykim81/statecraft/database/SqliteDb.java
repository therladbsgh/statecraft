package edu.brown.cs.ykim81.statecraft.database;

import edu.brown.cs.ykim81.statecraft.Main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

/**
 * Created by therl on 4/15/2017.
 */
public class SqliteDb extends Database {

  String dbName;

  public SqliteDb(Main instance) {
    super(instance);
    dbName = plugin.getConfig().getString("SQLite.Filename", "statecraft");
  }

  public String SQLiteCreateStatesTable = "CREATE TABLE IF NOT EXISTS states (" +
          "`id` INTEGER NOT NULL," +
          "`name` TEXT NOT NULL," +
          "`money` REAL NOT NULL," +
          "`tax` INTEGER NOT NULL," +
          "PRIMARY KEY (`id`)" +
          ");";

  public String SQLiteCreatePlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
          "`id` TEXT NOT NULL," +
          "`state` INTEGER NOT NULL," +
          "`leader` INTEGER NOT NULL," +
          "PRIMARY KEY (`id`)" +
          "FOREIGN KEY (`state`) REFERENCES state(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE" +
          ");";

  public String SQLiteCreateCitiesTable = "CREATE TABLE IF NOT EXISTS cities (" +
          "`id` INTEGER NOT NULL," +
          "`name` TEXT NOT NULL," +
          "`state` INTEGER NOT NULL," +
          "PRIMARY KEY (`id`)" +
          "FOREIGN KEY (`state`) REFERENCES state(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE" +
          ");";

  public String SQLiteCreateChunksTable = "CREATE TABLE IF NOT EXISTS chunks (" +
          "`id` INTEGER NOT NULL," +
          "`x` REAL NOT NULL," +
          "`z` REAL NOT NULL," +
          "`state` INTEGER NOT NULL," +
          "`city` INTEGER NOT NULL," +
          "`district` TEXT NOT NULL," +
          "PRIMARY KEY (`id`)" +
          "FOREIGN KEY (`state`) REFERENCES state(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE," +
          "FOREIGN KEY (`city`) REFERENCES cities(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE" +
          ");";

  public String SQLiteCreateChunkBuildTable = "CREATE TABLE IF NOT EXISTS chunkbuilds (" +
          "`userId` TEXT NOT NULL," +
          "`chunkId` INTEGER NOT NULL," +
          "FOREIGN KEY (`userId`) REFERENCES players(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE," +
          "FOREIGN KEY (`chunkId`) REFERENCES chunks(id)" +
          "ON DELETE CASCADE ON UPDATE CASCADE" +
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
      s.executeUpdate(SQLiteCreateStatesTable);
      s.executeUpdate(SQLiteCreatePlayersTable);
      s.execute(SQLiteCreateChunksTable);
      s.execute(SQLiteCreateChunkBuildTable);
      s.execute(SQLiteCreateCitiesTable);
      s.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    initialize();
  }

  @Override
  public void initialize() {
    connection = getSqlConnection();
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM states LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection for states", e);
    }

    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM players LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection for players", e);
    }

    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM chunks LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection for names", e);
    }

    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM chunkbuilds LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection for chunkbuilds", e);
    }

    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM cities LIMIT 1;")) {
      try (ResultSet rs = ps.executeQuery()) {

      }
    } catch (SQLException e) {
      plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection for cities", e);
    }
  }
}
