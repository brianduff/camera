package foscam;

import java.nio.ByteBuffer;

abstract class AbstractResponse {
  protected abstract void parsePayload(ByteBuffer buffer);
  
  protected final String readString(ByteBuffer buffer, int size) {
    byte[] data = new byte[size];
    buffer.get(data);
    int end = data.length;
    for (int i = 0; i < data.length; i++) {
      if (data[i] == 0) {
        end = i;
      }
    }
    return new String(data, 0, end);
  }
  
  
  protected final void skip(ByteBuffer buffer, int size) {
    buffer.position(buffer.position() + size);
  }
}
