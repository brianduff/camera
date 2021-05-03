package foscam;

import java.nio.ByteBuffer;

public class VerifyResponse extends AbstractResponse {
  private short result = -1;
  
  @Override
  protected void parsePayload(ByteBuffer buffer) {
    result = buffer.getShort();
  }
  
  public VerifyResult getResult() {
    return VerifyResult.valueOf(result);
  }

  public enum VerifyResult {
    SUCCESS,
    INCORRECT_USER,
    INCORRECT_PASSWORD,
    UNKNOWN;
    
    private static VerifyResult valueOf(short result) {
      switch (result) {
        case 0:
          return SUCCESS;
        case 1:
          return INCORRECT_USER;
        case 5:
          return INCORRECT_PASSWORD;
      }
      return UNKNOWN;
    }
  }
}
