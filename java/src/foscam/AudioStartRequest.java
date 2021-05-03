package foscam;

public class AudioStartRequest extends AbstractRequest {
  public AudioStartRequest() {
    super(RequestCode.AUDIO_START);
  }
  
  @Override
  protected void buildRequest() {
  }
}
