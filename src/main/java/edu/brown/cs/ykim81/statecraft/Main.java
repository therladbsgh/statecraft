package edu.brown.cs.ykim81.statecraft;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by therl on 4/15/2017.
 */
public class Main extends JavaPlugin {

  private Database stateDatabase;

  @Override
  public void onEnable() {
    //Fired when the server enables the plugin
    getLogger().info("StateCraft Enabled.");
    this.getCommand("sc").setExecutor(new CommandCreate());

    try {
      if (!getDataFolder().exists()) {
        getDataFolder().mkdirs();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    this.stateDatabase = new StateDatabase(this);
    this.stateDatabase.load();
  }

  @Override
  public void onDisable() {
    //Fired when the server disables the plugin
    getLogger().info("StateCraft disabled.");
  }

  public Database getStateDatabase() {
    return stateDatabase;
  }
}
