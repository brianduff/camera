package foscam;

public class KeepAliveRequest extends AbstractRequest {
  public KeepAliveRequest() {
    super(RequestCode.KEEP_ALIVE);
  }

  @Override
  protected void buildRequest() {
  }
}
