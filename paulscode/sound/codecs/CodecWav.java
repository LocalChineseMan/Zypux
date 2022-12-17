package paulscode.sound.codecs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

public class CodecWav implements ICodec {
  private static final boolean GET = false;
  
  private static final boolean SET = true;
  
  private static final boolean XXX = false;
  
  private boolean endOfStream = false;
  
  private boolean initialized = false;
  
  private AudioInputStream myAudioInputStream = null;
  
  private SoundSystemLogger logger;
  
  public void reverseByteOrder(boolean b) {}
  
  public CodecWav() {
    this.logger = SoundSystemConfig.getLogger();
  }
  
  public boolean initialize(URL url) {
    initialized(true, false);
    cleanup();
    if (url == null) {
      errorMessage("url null in method 'initialize'");
      cleanup();
      return false;
    } 
    try {
      this.myAudioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
    } catch (UnsupportedAudioFileException uafe) {
      errorMessage("Unsupported audio format in method 'initialize'");
      printStackTrace(uafe);
      return false;
    } catch (IOException ioe) {
      errorMessage("Error setting up audio input stream in method 'initialize'");
      printStackTrace(ioe);
      return false;
    } 
    endOfStream(true, false);
    initialized(true, true);
    return true;
  }
  
  public boolean initialized() {
    return initialized(false, false);
  }
  
  public SoundBuffer read() {
    if (this.myAudioInputStream == null)
      return null; 
    AudioFormat audioFormat = this.myAudioInputStream.getFormat();
    if (audioFormat == null) {
      errorMessage("Audio Format null in method 'read'");
      return null;
    } 
    int bytesRead = 0, cnt = 0;
    byte[] streamBuffer = new byte[SoundSystemConfig.getStreamingBufferSize()];
    try {
      while (!endOfStream(false, false) && bytesRead < streamBuffer.length) {
        if ((cnt = this.myAudioInputStream.read(streamBuffer, bytesRead, streamBuffer.length - bytesRead)) <= 0) {
          endOfStream(true, true);
          break;
        } 
        bytesRead += cnt;
      } 
    } catch (IOException ioe) {
      endOfStream(true, true);
      return null;
    } 
    if (bytesRead <= 0)
      return null; 
    if (bytesRead < streamBuffer.length)
      streamBuffer = trimArray(streamBuffer, bytesRead); 
    byte[] data = convertAudioBytes(streamBuffer, (audioFormat.getSampleSizeInBits() == 16));
    SoundBuffer buffer = new SoundBuffer(data, audioFormat);
    return buffer;
  }
  
  public SoundBuffer readAll() {
    if (this.myAudioInputStream == null) {
      errorMessage("Audio input stream null in method 'readAll'");
      return null;
    } 
    AudioFormat myAudioFormat = this.myAudioInputStream.getFormat();
    if (myAudioFormat == null) {
      errorMessage("Audio Format null in method 'readAll'");
      return null;
    } 
    byte[] fullBuffer = null;
    int fileSize = myAudioFormat.getChannels() * (int)this.myAudioInputStream.getFrameLength() * myAudioFormat.getSampleSizeInBits() / 8;
    if (fileSize > 0) {
      fullBuffer = new byte[myAudioFormat.getChannels() * (int)this.myAudioInputStream.getFrameLength() * myAudioFormat.getSampleSizeInBits() / 8];
      int read = 0, total = 0;
      try {
        while ((read = this.myAudioInputStream.read(fullBuffer, total, fullBuffer.length - total)) != -1 && total < fullBuffer.length)
          total += read; 
      } catch (IOException e) {
        errorMessage("Exception thrown while reading from the AudioInputStream (location #1).");
        printStackTrace(e);
        return null;
      } 
    } else {
      int totalBytes = 0, bytesRead = 0, cnt = 0;
      byte[] smallBuffer = null;
      smallBuffer = new byte[SoundSystemConfig.getFileChunkSize()];
      while (!endOfStream(false, false) && totalBytes < SoundSystemConfig.getMaxFileSize()) {
        bytesRead = 0;
        cnt = 0;
        try {
          while (bytesRead < smallBuffer.length) {
            if ((cnt = this.myAudioInputStream.read(smallBuffer, bytesRead, smallBuffer.length - bytesRead)) <= 0) {
              endOfStream(true, true);
              break;
            } 
            bytesRead += cnt;
          } 
        } catch (IOException e) {
          errorMessage("Exception thrown while reading from the AudioInputStream (location #2).");
          printStackTrace(e);
          return null;
        } 
        totalBytes += bytesRead;
        fullBuffer = appendByteArrays(fullBuffer, smallBuffer, bytesRead);
      } 
    } 
    byte[] data = convertAudioBytes(fullBuffer, (myAudioFormat.getSampleSizeInBits() == 16));
    SoundBuffer soundBuffer = new SoundBuffer(data, myAudioFormat);
    try {
      this.myAudioInputStream.close();
    } catch (IOException e) {}
    return soundBuffer;
  }
  
  public boolean endOfStream() {
    return endOfStream(false, false);
  }
  
  public void cleanup() {
    if (this.myAudioInputStream != null)
      try {
        this.myAudioInputStream.close();
      } catch (Exception e) {} 
    this.myAudioInputStream = null;
  }
  
  public AudioFormat getAudioFormat() {
    if (this.myAudioInputStream == null)
      return null; 
    return this.myAudioInputStream.getFormat();
  }
  
  private synchronized boolean initialized(boolean action, boolean value) {
    if (action == true)
      this.initialized = value; 
    return this.initialized;
  }
  
  private synchronized boolean endOfStream(boolean action, boolean value) {
    if (action == true)
      this.endOfStream = value; 
    return this.endOfStream;
  }
  
  private static byte[] trimArray(byte[] array, int maxLength) {
    byte[] trimmedArray = null;
    if (array != null && array.length > maxLength) {
      trimmedArray = new byte[maxLength];
      System.arraycopy(array, 0, trimmedArray, 0, maxLength);
    } 
    return trimmedArray;
  }
  
  private static byte[] convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data) {
    ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
    dest.order(ByteOrder.nativeOrder());
    ByteBuffer src = ByteBuffer.wrap(audio_bytes);
    src.order(ByteOrder.LITTLE_ENDIAN);
    if (two_bytes_data) {
      ShortBuffer dest_short = dest.asShortBuffer();
      ShortBuffer src_short = src.asShortBuffer();
      while (src_short.hasRemaining())
        dest_short.put(src_short.get()); 
    } else {
      while (src.hasRemaining())
        dest.put(src.get()); 
    } 
    dest.rewind();
    if (!dest.hasArray()) {
      byte[] arrayBackedBuffer = new byte[dest.capacity()];
      dest.get(arrayBackedBuffer);
      dest.clear();
      return arrayBackedBuffer;
    } 
    return dest.array();
  }
  
  private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int length) {
    byte[] newArray;
    if (arrayOne == null && arrayTwo == null)
      return null; 
    if (arrayOne == null) {
      newArray = new byte[length];
      System.arraycopy(arrayTwo, 0, newArray, 0, length);
      arrayTwo = null;
    } else if (arrayTwo == null) {
      newArray = new byte[arrayOne.length];
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      arrayOne = null;
    } else {
      newArray = new byte[arrayOne.length + length];
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, length);
      arrayOne = null;
      arrayTwo = null;
    } 
    return newArray;
  }
  
  private void errorMessage(String message) {
    this.logger.errorMessage("CodecWav", message, 0);
  }
  
  private void printStackTrace(Exception e) {
    this.logger.printStackTrace(e, 1);
  }
}
