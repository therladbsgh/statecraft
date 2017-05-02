package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/24/2017.
 */
public class ChunkProxy {

  private int id;
  private double x;
  private double z;
  private int stateId;
  private int cityId;
  private District district;

  public ChunkProxy(int id, double x, double z, int stateId, int cityId, District district) {
    this.id = id;
    this.x = x;
    this.z = z;
    this.stateId = stateId;
    this.cityId = cityId;
    this.district = district;
  }

  public int getChunkId() {
    return id;
  }

  public double getX() {
    return x;
  }

  public double getZ() {
    return z;
  }

  public int getStateId() {
    return stateId;
  }

  public int getCityId() {
    return cityId;
  }

  public District getDistrict() {
    return district;
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
