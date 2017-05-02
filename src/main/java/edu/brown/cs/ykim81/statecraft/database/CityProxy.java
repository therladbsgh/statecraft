package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 5/1/2017.
 */
public class CityProxy {

  private int id;
  private String name;
  private int stateId;


  public CityProxy(int id, String name, int stateId) {
    this.id = id;
    this.name = name;
    this.stateId = stateId;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getStateId() {
    return stateId;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CityProxy) {
      CityProxy c = (CityProxy) o;
      return this.id == c.id;
    } else {
      return false;
    }
  }

}
