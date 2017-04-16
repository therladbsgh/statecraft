package edu.brown.cs.ykim81.statecraft;

import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigDecimal;

/**
 * Created by therl on 4/16/2017.
 */
public class TaxListener implements Listener {

  private Database stateDatabase;

  public TaxListener(Database stateDatabase) {
    this.stateDatabase = stateDatabase;
  }

  @EventHandler
  public void onPlayerEarnMoney(UserBalanceUpdateEvent event) {
    double profit = event.getNewBalance().subtract(event.getOldBalance()).doubleValue();
    if (profit > 0) {
      String state = stateDatabase.getStateFromPlayer(event.getPlayer().getUniqueId().toString());
      if (state.length() > 0) {
        int tax = stateDatabase.getTaxOfState(state);
        if (tax > 0) {
          double moneyToTake = (profit * tax) /100.0;
          event.setNewBalance(event.getNewBalance().subtract(BigDecimal.valueOf(moneyToTake)));
          stateDatabase.updateMoneyOfState(moneyToTake, state);
          event.getPlayer().sendMessage("$" + moneyToTake + " has been taken as tax.");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    System.out.println("Player joined");
  }

}
