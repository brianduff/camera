package foscam;

public enum RequestCode {
  LOGIN("MO_O", 0),
  VERIFY("MO_O", 2),
  AUDIO_START("MO_O", 8),
  KEEP_ALIVE("MO_O", 255),
  
  VIDEO_LOGIN("MO_V", 0);
  
  final String protocol;
  final short operationCode;
  
  RequestCode(String protocol, int code) {
    this.protocol = protocol;
    this.operationCode = (short) code;
  }
  
  public static RequestCode valueOf(String protocol, int opcode) {
    for (RequestCode c : values()) {
      if (c.operationCode == opcode && c.protocol.equals(protocol)) {
        return c;
      }
    }
    return null;
  }
}
