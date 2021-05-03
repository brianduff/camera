package foscam;

import java.util.logging.Logger;

import foscam.VerifyResponse.VerifyResult;

public class Test {
  private static final Logger log = Logger.getLogger(Test.class.getName());
  private static CameraConnection conn;
  
  public static void main(String[] args) throws Exception {    
    conn = new CameraConnection("10.0.1.201", 80);
    conn.setAudioDataCallback(Test::audioDataReceived);
    conn.setOtherDevicesParamsCallback(Test::otherDevicesNotify);
    conn.logIn(Test::loggedIn);
  }
  
  private static void fail(String message) {
    try {
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException(message);
  }
  
  public static void loggedIn(LoginResponse response) {
    if (response.isOk()) {
      log.info("Got successful login response");
      conn.verify("admin", "gilopa56", Test::verified);
    } else {
      fail("Failed to login");
    }
  }
  
  public static void verified(VerifyResponse response) {
    if (response.getResult() == VerifyResult.SUCCESS) {
      log.info("Got successful verify");
      conn.startAudio(Test::audioStarted);
    } else {
      fail("Failed to verify " + response.getResult());
    }
  }
  
  public static void audioStarted(AudioStartResponse response) {
    System.out.println("Audio started with " + response.getResult() + ": " + response.getConnectionId());
    conn.videoLogin(response.getConnectionId());
  }
  
  public static void audioDataReceived(AudioData response) {
    log.info("Received " + response.getData().length + " bytes of audio data.");
  }
  
  public static void otherDevicesNotify(OtherDevicesParamsNotify response) {
    log.info("Other devices: " + response.getOtherDeviceParams());
  }
}
