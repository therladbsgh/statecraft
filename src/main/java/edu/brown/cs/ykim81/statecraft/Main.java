package edu.brown.cs.ykim81.statecraft;

import edu.brown.cs.ykim81.statecraft.database.Database;
import edu.brown.cs.ykim81.statecraft.database.SqliteDb;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by therl on 4/15/2017.
 */
public class Main extends JavaPlugin {

  private Database db;

  @Override
  public void onEnable() {
    //Fired when the server enables the plugin
    try {
      if (!getDataFolder().exists()) {
        getDataFolder().mkdirs();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    this.db = new SqliteDb(this);
    this.db.load();

    getServer().getPluginManager().registerEvents(new TaxListener(db), this);

    this.getCommand("sc").setExecutor(new CommandCreate(db));
    getLogger().info("StateCraft Enabled.");
  }

  @Override
  public void onDisable() {
    //Fired when the server disables the plugin
    getLogger().info("StateCraft disabled.");
  }

  public Database getPluginDatabase() {
    return db;
  }

}
