package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/17/2017.
 */
public class PlayerProxy {

  private String id;
  private int state;
  private int leader;

  public PlayerProxy(String id, int state, int leader) {
    this.id = id;
    this.state = state;
    this.leader = leader;
  }

  public String getId() {
    return id;
  }

  public int getState() {
    return state;
  }

  public int getLeader() {
    return leader;
  }

  public void setParams(String id, int state, int leader) {
    this.id = id;
    this.state = state;
    this.leader = leader;
  }

}
