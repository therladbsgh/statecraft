package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/24/2017.
 */
public class ChunkProxy {

  private double x;
  private double z;
  private int stateId;

  public ChunkProxy(double x, double z, int stateId) {
    this.x = x;
    this.z = z;
    this.stateId = stateId;
  }

  public double getX() {
    return x;
  }

  public double getZ() {
    return z;
  }

  public int getId() {
    return stateId;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ChunkProxy) {
      ChunkProxy c = (ChunkProxy) o;
      return Double.valueOf(x).equals(c.x) && Double.valueOf(z).equals(c.z);
    } else {
      return false;
    }
  }

}
