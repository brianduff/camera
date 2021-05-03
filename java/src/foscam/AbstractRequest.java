package foscam;

abstract class AbstractRequest {
  private final RequestCode requestCode;
  private final 
  
  protected AbstractRequest(RequestCode requestCode) {
    this.requestCode = requestCode;
  }
  
  final RequestCode getRequestCode() {
    return requestCode;
  }
  
  protected void buildRequest() {
    
  }
  
  final byte[] toByteArray() {
    return null;
  }
  
  protected final void putString(String s, int length) {
    
  }
}
