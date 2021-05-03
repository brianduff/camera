package foscam;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CameraConnection implements AutoCloseable {
  private static final int KEEP_ALIVE_SECS = 20;
  private static Logger log = Logger.getLogger(CameraConnection.class.getName());
  private static final int ENVELOPE_SIZE = 23;
  private final Socket socket;
  
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  
  private final Map<Operation, CallbackWrapper<?>> callbacks = new HashMap<>();  
  private final BlockingQueue<AbstractRequest> pendingRequests = new LinkedBlockingQueue<>();
  
  public CameraConnection(String host, int port) throws IOException {
    socket = new Socket(host, port);
    new Thread(this::processRequests).start();
    new Thread(this::processResponses).start();
    
    scheduler.scheduleAtFixedRate(this::sendKeepAlive, KEEP_ALIVE_SECS, 45, TimeUnit.SECONDS);
    addCallback(Operation.KEEP_ALIVE_RESPONSE, EmptyResponse.class, this::receiveKeepAlive);
  }
  
  private void sendKeepAlive() {
    send(new KeepAliveRequest());
  }
  
  private void receiveKeepAlive(EmptyResponse response) {
    log.info("Received KeepAlive");
  }
  
  private void processRequests() {
    while (true) {
      try {
        AbstractRequest request = pendingRequests.take();
        try {
          log.info("Sending request: " + request);
          reallySend(request);
        } catch (IOException e) {
          // TODO(bduff): Exception handling
          e.printStackTrace();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }  
  }
  
  private void processResponses() {
    while (true) {
      // Read the envelope.
      try {
        byte[] envelope = new byte[ENVELOPE_SIZE];
        System.out.println("Blocking on envelope read...");
        socket.getInputStream().read(envelope);
        ByteBuffer buffer = ByteBuffer.wrap(envelope).order(ByteOrder.LITTLE_ENDIAN);
  
        byte[] protocolData = new byte[4];
        buffer.get(protocolData);
        short opcode = buffer.getShort();
        // Skip reserved fields.
        buffer.get();
        buffer.getLong();
        int length = buffer.getInt();
        
        String protocolString = new String(protocolData);

        Operation code = Operation.valueOf(protocolString, opcode);
        if (code == null) {
          log.warning("Ignored unknown opcode: " + protocolString + ":" + opcode);
          socket.getInputStream().skip(length);
          continue;
        }        
        
        CallbackWrapper<?> callback = callbacks.get(code);
        if (callback == null) {
          log.warning("Unhandled opcode: " + code);
          socket.getInputStream().skip(length);
          continue;
        }

        ByteBuffer payload = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        socket.getInputStream().read(payload.array());

        callback.invoke(payload);

      } catch (IOException e) {
        // TODO(bduff): Exception handling
        e.printStackTrace();
      }
      
    }
  }
  
  public static interface Callback<T extends AbstractResponse> {
    void handle(T response);
  }
  
  private static class CallbackWrapper<T extends AbstractResponse> {
    private final Class<T> responseClass;
    private final Callback<T> callback;
    
    CallbackWrapper(Class<T> responseClass, Callback<T> callback) {
      this.responseClass = responseClass;
      this.callback = callback;
    }
    
    void invoke(ByteBuffer payload) {
      try {
        T response = responseClass.newInstance();
        response.parsePayload(payload);
        log.info("Handling " + response);
        callback.handle(response);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }
  }
  
  private <T extends AbstractResponse> void addCallback(Operation operation, Class<T> responseClass, Callback<T> callback) {
    callbacks.put(operation, new CallbackWrapper<>(responseClass, callback));
  }
  
  public void logIn(Callback<LoginResponse> callback) {
    addCallback(Operation.LOGIN_RESP, LoginResponse.class, callback);
    send(new LoginRequest());
  }
  
  public void verify(String username, String password, Callback<VerifyResponse> callback) {
    addCallback(Operation.VERIFY_RESP, VerifyResponse.class, callback);
    send(new VerifyRequest().setUser(username).setPassword(password));
  }
  
  public void startAudio(Callback<AudioStartResponse> callback) {
    addCallback(Operation.AUDIO_START_RESP, AudioStartResponse.class, callback);
    send(new AudioStartRequest());
  }
  
  public void videoLogin(int connectionId) {
    send(new VideoLoginRequest().setConnectionId(connectionId));
  }
  
  public void setAudioDataCallback(Callback<AudioData> callback) {
    addCallback(Operation.AUDIO_DATA, AudioData.class, callback);
  }
  
  public void setOtherDevicesParamsCallback(Callback<OtherDevicesParamsNotify> callback) {
    addCallback(Operation.OTHER_DEVICES_PARAMS_NOTIFY, OtherDevicesParamsNotify.class, callback);
  }
  
  private void send(AbstractRequest request) {
    pendingRequests.offer(request);
  }
    
  private void reallySend(AbstractRequest request) throws IOException {
    byte[] requestBytes = request.toByteArray();
    ByteBuffer buffer = ByteBuffer.allocate(requestBytes.length + ENVELOPE_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(request.getRequestCode().protocol.getBytes());
    buffer.putShort(request.getRequestCode().operationCode);
    buffer.put((byte) 0); // reserved
    buffer.putLong(0L); // reserved
    buffer.putInt(requestBytes.length);
    buffer.putInt(0); // reserved
    buffer.put(requestBytes);
    
    byte[] bytes = new byte[buffer.position()];
    buffer.flip();
    buffer.get(bytes);
    socket.getOutputStream().write(bytes);
  }
  

  @Override
  public void close() throws Exception {
    this.socket.close();
  }
}
