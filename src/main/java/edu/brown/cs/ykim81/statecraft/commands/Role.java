package edu.brown.cs.ykim81.statecraft.commands;

/**
 * Created by therl on 4/30/2017.
 */
public enum Role {

  KING ("King"),
  LEADER ("Leader"),
  BUILDER ("Builder"),
  CITIZEN ("Citizen"),
  BANDIT ("Bandit");

  private String s;

  Role(String s) {
    this.s = s;
  }

}
