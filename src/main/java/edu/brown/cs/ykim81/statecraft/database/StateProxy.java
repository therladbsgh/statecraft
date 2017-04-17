package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/17/2017.
 */
public class StateProxy {

  private int id;
  private String name;
  private double money;
  private int tax;

  public StateProxy(int id, String name, double money, int tax) {
    this.id = id;
    this.name = name;
    this.money = money;
    this.tax = tax;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getMoney() {
    return money;
  }

  public int getTax() {
    return tax;
  }

  public void setParams(int id, String name, double money, int tax) {
    this.id = id;
    this.name = name;
    this.money = money;
    this.tax = tax;
  }

}
