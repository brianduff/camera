package foscam;

import java.nio.ByteBuffer;

class EmptyResponse extends AbstractResponse {
  @Override
  protected void parsePayload(ByteBuffer buffer) {
  }
}
