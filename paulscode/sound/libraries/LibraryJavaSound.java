package paulscode.sound.libraries;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;

public class LibraryJavaSound extends Library {
  private static final boolean GET = false;
  
  private static final boolean SET = true;
  
  private static final int XXX = 0;
  
  private final int maxClipSize = 1048576;
  
  private static Mixer myMixer = null;
  
  private static MixerRanking myMixerRanking = null;
  
  private static LibraryJavaSound instance = null;
  
  private static int minSampleRate = 4000;
  
  private static int maxSampleRate = 48000;
  
  private static int lineCount = 32;
  
  private static boolean useGainControl = true;
  
  private static boolean usePanControl = true;
  
  private static boolean useSampleRateControl = true;
  
  public LibraryJavaSound() throws SoundSystemException {
    instance = this;
  }
  
  public void init() throws SoundSystemException {
    MixerRanking mixerRanker = null;
    if (myMixer == null) {
      for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
        if (mixerInfo.getName().equals("Java Sound Audio Engine")) {
          mixerRanker = new MixerRanking();
          try {
            mixerRanker.rank(mixerInfo);
          } catch (Exception ljse) {
            break;
          } 
          if (mixerRanker.rank < 14)
            break; 
          myMixer = AudioSystem.getMixer(mixerInfo);
          mixerRanking(true, mixerRanker);
          break;
        } 
      } 
      if (myMixer == null) {
        MixerRanking bestRankedMixer = mixerRanker;
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
          mixerRanker = new MixerRanking();
          try {
            mixerRanker.rank(mixerInfo);
          } catch (Exception ljse) {}
          if (bestRankedMixer == null || mixerRanker.rank > bestRankedMixer.rank)
            bestRankedMixer = mixerRanker; 
        } 
        if (bestRankedMixer == null)
          throw new Exception("No useable mixers found!", new MixerRanking()); 
        try {
          myMixer = AudioSystem.getMixer(bestRankedMixer.mixerInfo);
          mixerRanking(true, bestRankedMixer);
        } catch (Exception e) {
          throw new Exception("No useable mixers available!", new MixerRanking());
        } 
      } 
    } 
    setMasterVolume(1.0F);
    message("JavaSound initialized.");
    super.init();
  }
  
  public static boolean libraryCompatible() {
    for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
      if (mixerInfo.getName().equals("Java Sound Audio Engine"))
        return true; 
    } 
    return false;
  }
  
  protected Channel createChannel(int type) {
    return new ChannelJavaSound(type, myMixer);
  }
  
  public void cleanup() {
    super.cleanup();
    instance = null;
    myMixer = null;
    myMixerRanking = null;
  }
  
  public boolean loadSound(FilenameURL filenameURL) {
    if (this.bufferMap == null) {
      this.bufferMap = new HashMap<Object, Object>();
      importantMessage("Buffer Map was null in method 'loadSound'");
    } 
    if (errorCheck((filenameURL == null), "Filename/URL not specified in method 'loadSound'"))
      return false; 
    if (this.bufferMap.get(filenameURL.getFilename()) != null)
      return true; 
    ICodec codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
    if (errorCheck((codec == null), "No codec found for file '" + filenameURL.getFilename() + "' in method 'loadSound'"))
      return false; 
    URL url = filenameURL.getURL();
    if (errorCheck((url == null), "Unable to open file '" + filenameURL.getFilename() + "' in method 'loadSound'"))
      return false; 
    codec.initialize(url);
    SoundBuffer buffer = codec.readAll();
    codec.cleanup();
    codec = null;
    if (buffer != null) {
      this.bufferMap.put(filenameURL.getFilename(), buffer);
    } else {
      errorMessage("Sound buffer null in method 'loadSound'");
    } 
    return true;
  }
  
  public boolean loadSound(SoundBuffer buffer, String identifier) {
    if (this.bufferMap == null) {
      this.bufferMap = new HashMap<Object, Object>();
      importantMessage("Buffer Map was null in method 'loadSound'");
    } 
    if (errorCheck((identifier == null), "Identifier not specified in method 'loadSound'"))
      return false; 
    if (this.bufferMap.get(identifier) != null)
      return true; 
    if (buffer != null) {
      this.bufferMap.put(identifier, buffer);
    } else {
      errorMessage("Sound buffer null in method 'loadSound'");
    } 
    return true;
  }
  
  public void setMasterVolume(float value) {
    super.setMasterVolume(value);
    Set<String> keys = this.sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = (Source)this.sourceMap.get(sourcename);
      if (source != null)
        source.positionChanged(); 
    } 
  }
  
  public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll) {
    SoundBuffer buffer = null;
    if (!toStream) {
      buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
      if (buffer == null)
        if (!loadSound(filenameURL)) {
          errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
          return;
        }  
      buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
      if (buffer == null) {
        errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
        return;
      } 
    } 
    if (!toStream && buffer != null)
      buffer.trimData(1048576); 
    this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false));
  }
  
  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
    this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
  }
  
  public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
    SoundBuffer buffer = null;
    if (!toStream) {
      buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
      if (buffer == null)
        if (!loadSound(filenameURL)) {
          errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
          return;
        }  
      buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
      if (buffer == null) {
        errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
        return;
      } 
    } 
    if (!toStream && buffer != null)
      buffer.trimData(1048576); 
    this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, temporary));
  }
  
  public void copySources(HashMap<String, Source> srcMap) {
    if (srcMap == null)
      return; 
    Set<String> keys = srcMap.keySet();
    Iterator<String> iter = keys.iterator();
    if (this.bufferMap == null) {
      this.bufferMap = new HashMap<Object, Object>();
      importantMessage("Buffer Map was null in method 'copySources'");
    } 
    this.sourceMap.clear();
    while (iter.hasNext()) {
      String sourcename = iter.next();
      Source source = srcMap.get(sourcename);
      if (source != null) {
        SoundBuffer buffer = null;
        if (!source.toStream) {
          loadSound(source.filenameURL);
          buffer = (SoundBuffer)this.bufferMap.get(source.filenameURL.getFilename());
        } 
        if (!source.toStream && buffer != null)
          buffer.trimData(1048576); 
        if (source.toStream || buffer != null)
          this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, source, buffer)); 
      } 
    } 
  }
  
  public void setListenerVelocity(float x, float y, float z) {
    super.setListenerVelocity(x, y, z);
    listenerMoved();
  }
  
  public void dopplerChanged() {
    super.dopplerChanged();
    listenerMoved();
  }
  
  public static Mixer getMixer() {
    return mixer(false, (Mixer)null);
  }
  
  public static void setMixer(Mixer m) throws SoundSystemException {
    mixer(true, m);
    SoundSystemException e = SoundSystem.getLastException();
    SoundSystem.setException(null);
    if (e != null)
      throw e; 
  }
  
  private static synchronized Mixer mixer(boolean action, Mixer m) {
    if (action == true) {
      if (m == null)
        return myMixer; 
      MixerRanking mixerRanker = new MixerRanking();
      try {
        mixerRanker.rank(m.getMixerInfo());
      } catch (Exception ljse) {
        SoundSystemConfig.getLogger().printStackTrace((Exception)ljse, 1);
        SoundSystem.setException(ljse);
      } 
      myMixer = m;
      mixerRanking(true, mixerRanker);
      if (instance != null) {
        ListIterator<Channel> itr = instance.normalChannels.listIterator();
        SoundSystem.setException(null);
        while (itr.hasNext()) {
          ChannelJavaSound c = (ChannelJavaSound)itr.next();
          c.newMixer(m);
        } 
        itr = instance.streamingChannels.listIterator();
        while (itr.hasNext()) {
          ChannelJavaSound c = (ChannelJavaSound)itr.next();
          c.newMixer(m);
        } 
      } 
    } 
    return myMixer;
  }
  
  public static MixerRanking getMixerRanking() {
    return mixerRanking(false, (MixerRanking)null);
  }
  
  private static synchronized MixerRanking mixerRanking(boolean action, MixerRanking value) {
    if (action == true)
      myMixerRanking = value; 
    return myMixerRanking;
  }
  
  public static void setMinSampleRate(int value) {
    minSampleRate(true, value);
  }
  
  private static synchronized int minSampleRate(boolean action, int value) {
    if (action == true)
      minSampleRate = value; 
    return minSampleRate;
  }
  
  public static void setMaxSampleRate(int value) {
    maxSampleRate(true, value);
  }
  
  private static synchronized int maxSampleRate(boolean action, int value) {
    if (action == true)
      maxSampleRate = value; 
    return maxSampleRate;
  }
  
  public static void setLineCount(int value) {
    lineCount(true, value);
  }
  
  private static synchronized int lineCount(boolean action, int value) {
    if (action == true)
      lineCount = value; 
    return lineCount;
  }
  
  public static void useGainControl(boolean value) {
    useGainControl(true, value);
  }
  
  private static synchronized boolean useGainControl(boolean action, boolean value) {
    if (action == true)
      useGainControl = value; 
    return useGainControl;
  }
  
  public static void usePanControl(boolean value) {
    usePanControl(true, value);
  }
  
  private static synchronized boolean usePanControl(boolean action, boolean value) {
    if (action == true)
      usePanControl = value; 
    return usePanControl;
  }
  
  public static void useSampleRateControl(boolean value) {
    useSampleRateControl(true, value);
  }
  
  private static synchronized boolean useSampleRateControl(boolean action, boolean value) {
    if (action == true)
      useSampleRateControl = value; 
    return useSampleRateControl;
  }
  
  public static String getTitle() {
    return "Java Sound";
  }
  
  public static String getDescription() {
    return "The Java Sound API.  For more information, see http://java.sun.com/products/java-media/sound/";
  }
  
  public String getClassName() {
    return "LibraryJavaSound";
  }
  
  public static class Exception extends SoundSystemException {
    public static final int MIXER_PROBLEM = 101;
    
    public static LibraryJavaSound.MixerRanking mixerRanking = null;
    
    public Exception(String message) {
      super(message);
    }
    
    public Exception(String message, int type) {
      super(message, type);
    }
    
    public Exception(String message, LibraryJavaSound.MixerRanking rank) {
      super(message, 101);
      mixerRanking = rank;
    }
  }
  
  public static class LibraryJavaSound {}
}
