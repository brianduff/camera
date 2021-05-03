package foscam;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class OtherDevicesParamsNotify extends AbstractResponse {
  private OtherDeviceParams[] params;

  public List<OtherDeviceParams> getOtherDeviceParams() {
    return Arrays.asList(params);
  }

  @Override
  protected void parsePayload(ByteBuffer buffer) {
    System.out.println("Parsing other devices payload");
    params = new OtherDeviceParams[9];
    
    for (int i = 0; i < params.length; i++) {
      params[i] = new OtherDeviceParams();
      params[i].msid = readString(buffer, 13);
      params[i].alias = readString(buffer, 21);
      params[i].host = readString(buffer, 65);
      params[i].port = buffer.getShort();
      params[i].user = readString(buffer, 13);
      params[i].password = readString(buffer, 13);
      params[i].mode = buffer.get();
    }
  }
  
  class OtherDeviceParams {
    String msid;
    String alias;
    String host;
    short port;
    String user;
    String password;
    byte mode;
    
    public String toString() {
      return "msid=" + msid 
          + ", alias=" + alias
          + ", host=" + host
          + ", port=" + port
          + ", user=" + user
          + ", password=" + password
          + ", mode=" + mode;
    }
  }
}
