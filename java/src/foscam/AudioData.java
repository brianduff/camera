package foscam;

import java.nio.ByteBuffer;

public class AudioData extends AbstractResponse {
  private int timestamp;
  private int serialNumber;
  private int gatherTime;
  private byte audioFormat;
  private byte[] data;

  public int getTimestamp() {
    return timestamp;
  }

  public int getSerialNumber() {
    return serialNumber;
  }

  public int getGatherTime() {
    return gatherTime;
  }

  public byte getAudioFormat() {
    return audioFormat;
  }

  public byte[] getData() {
    return data;
  }
  
  @Override
  protected void parsePayload(ByteBuffer buffer) {
    timestamp = buffer.getInt();
    serialNumber = buffer.getInt();
    gatherTime = buffer.getInt();
    audioFormat = buffer.get();
    int dataLength = buffer.getInt();
    data = new byte[dataLength];
    buffer.get(data);
  }
}
