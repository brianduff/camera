package foscam;

public class LoginRequest extends AbstractRequest {
  
  public LoginRequest() {
    super(RequestCode.LOGIN);
  }

  @Override
  protected void buildRequest() {
    // Nothing to write.
  }
}
