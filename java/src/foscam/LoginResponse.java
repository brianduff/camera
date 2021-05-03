package foscam;

import java.nio.ByteBuffer;

public class LoginResponse extends AbstractResponse {
  private short result = -1;
  private String cameraId;
  private String firmwareVersion;

  @Override
  protected void parsePayload(ByteBuffer buffer) {
    result = buffer.getShort();
    if (isOk()) {
      cameraId = readString(buffer, 13);
      skip(buffer, 8); // reserved
      firmwareVersion = readString(buffer, 4);
    }
  }
  
  public boolean isOk() {
    return result == 0;
  }
  
  public String getCameraId() {
    return cameraId;
  }
  
  public String getFirmwareVersion() {
    return firmwareVersion;
  }
}
