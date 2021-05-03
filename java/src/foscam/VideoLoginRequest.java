package foscam;

public class VideoLoginRequest extends AbstractRequest {
  
  private int connectionId;

  protected VideoLoginRequest() {
    super(RequestCode.VIDEO_LOGIN);
  }
  
  public VideoLoginRequest setConnectionId(int connectionId) {
    this.connectionId = connectionId;
    return this;
  }

  @Override
  protected void buildRequest() {
    putInt(connectionId);
  }
}
