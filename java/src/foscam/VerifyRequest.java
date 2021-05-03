package foscam;

public class VerifyRequest extends AbstractRequest {
  private static final int FIELD_LENGTH = 13;
  
  private String user;
  private String password;
  
  public VerifyRequest() {
    super(RequestCode.VERIFY);
  }
  
  public VerifyRequest setUser(String user) {
    this.user = user;
    return this;
  }
  
  public VerifyRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  protected void buildRequest() {
    putString(user, FIELD_LENGTH);
    putString(password, FIELD_LENGTH);
  }
}
