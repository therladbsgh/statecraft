package edu.brown.cs.ykim81.statecraft.database;

/**
 * Created by therl on 4/28/2017.
 */
public class ChunkBuildProxy {

  private String userId;
  private int chunkId;

  public ChunkBuildProxy(String userId, int chunkId) {
    this.userId = userId;
    this.chunkId = chunkId;
  }

  public String getUserId() {
    return userId;
  }

  public int getChunkId() {
    return chunkId;
  }

}
