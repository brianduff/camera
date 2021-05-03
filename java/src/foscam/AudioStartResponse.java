package foscam;

import java.nio.ByteBuffer;

public class AudioStartResponse extends AbstractResponse {
  private short result;
  private int connectionId;

  @Override
  protected void parsePayload(ByteBuffer buffer) {
    result = buffer.getShort();
    if (getResult() == AudioStartResult.SUCCESS) {
      connectionId = buffer.getInt();
    }
  }
  
  public AudioStartResult getResult() {
    return AudioStartResult.valueOf(result);
  }
  
  public int getConnectionId() {
    return connectionId;
  }
  
  public enum AudioStartResult {
    SUCCESS,
    TOO_MANY_CONNECTIONS,
    NOT_SUPPORTED,
    UNKNOWN;
    
    private static AudioStartResult valueOf(short result) {
      switch (result) {
        case 0: return SUCCESS;
        case 2: return TOO_MANY_CONNECTIONS;
        case 7: return NOT_SUPPORTED;
      }
      return UNKNOWN;
    }
  }
}
