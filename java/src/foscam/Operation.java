package foscam;

public enum Operation {
  LOGIN_RESP("MO_O", 1, LoginResponse.class),
  VERIFY_RESP("MO_O", 3, VerifyResponse.class),
  AUDIO_START_RESP("MO_O", 9, AudioStartResponse.class),
  OTHER_DEVICES_PARAMS_NOTIFY("MO_O", 28, OtherDevicesParamsNotify.class),
  KEEP_ALIVE_RESPONSE("MO_O", 255, EmptyResponse.class),
  
  
  AUDIO_DATA("MO_V", 2, AudioData.class);

  static final String OPERATION_PROTOCOL = "MO_O";
  static final String VIDEO_PROTOCOL = "MO_V";
  
  final String protocol;
  final short operationCode;
  final Class<? extends AbstractCommand> commandClass;
  
  Operation(String protocol, int code,
      Class<? extends AbstractCommand> commandClass) {
    this.protocol = protocol;
    this.operationCode = (short) code;
    this.commandClass = commandClass;
  }
  
  public static Operation valueOf(String protocol, int opcode) {
    for (Operation c : values()) {
      if (c.operationCode == opcode && c.protocol.equals(protocol)) {
        return c;
      }
    }
    return null;
  }
}
