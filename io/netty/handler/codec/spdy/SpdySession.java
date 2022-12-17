package io.netty.handler.codec.spdy;

import io.netty.util.internal.PlatformDependent;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession {
  private final AtomicInteger activeLocalStreams = new AtomicInteger();
  
  private final AtomicInteger activeRemoteStreams = new AtomicInteger();
  
  private final Map<Integer, StreamState> activeStreams = PlatformDependent.newConcurrentHashMap();
  
  private final StreamComparator streamComparator = new StreamComparator(this);
  
  private final AtomicInteger sendWindowSize;
  
  private final AtomicInteger receiveWindowSize;
  
  SpdySession(int sendWindowSize, int receiveWindowSize) {
    this.sendWindowSize = new AtomicInteger(sendWindowSize);
    this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
  }
  
  int numActiveStreams(boolean remote) {
    if (remote)
      return this.activeRemoteStreams.get(); 
    return this.activeLocalStreams.get();
  }
  
  boolean noActiveStreams() {
    return this.activeStreams.isEmpty();
  }
  
  boolean isActiveStream(int streamId) {
    return this.activeStreams.containsKey(Integer.valueOf(streamId));
  }
  
  Map<Integer, StreamState> activeStreams() {
    Map<Integer, StreamState> streams = new TreeMap<Integer, StreamState>((Comparator<? super Integer>)this.streamComparator);
    streams.putAll(this.activeStreams);
    return streams;
  }
  
  void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
    if (!remoteSideClosed || !localSideClosed) {
      StreamState state = this.activeStreams.put(Integer.valueOf(streamId), new StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize));
      if (state == null)
        if (remote) {
          this.activeRemoteStreams.incrementAndGet();
        } else {
          this.activeLocalStreams.incrementAndGet();
        }  
    } 
  }
  
  private StreamState removeActiveStream(int streamId, boolean remote) {
    StreamState state = this.activeStreams.remove(Integer.valueOf(streamId));
    if (state != null)
      if (remote) {
        this.activeRemoteStreams.decrementAndGet();
      } else {
        this.activeLocalStreams.decrementAndGet();
      }  
    return state;
  }
  
  void removeStream(int streamId, Throwable cause, boolean remote) {
    StreamState state = removeActiveStream(streamId, remote);
    if (state != null)
      state.clearPendingWrites(cause); 
  }
  
  boolean isRemoteSideClosed(int streamId) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state == null || state.isRemoteSideClosed());
  }
  
  void closeRemoteSide(int streamId, boolean remote) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    if (state != null) {
      state.closeRemoteSide();
      if (state.isLocalSideClosed())
        removeActiveStream(streamId, remote); 
    } 
  }
  
  boolean isLocalSideClosed(int streamId) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state == null || state.isLocalSideClosed());
  }
  
  void closeLocalSide(int streamId, boolean remote) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    if (state != null) {
      state.closeLocalSide();
      if (state.isRemoteSideClosed())
        removeActiveStream(streamId, remote); 
    } 
  }
  
  boolean hasReceivedReply(int streamId) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null && state.hasReceivedReply());
  }
  
  void receivedReply(int streamId) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    if (state != null)
      state.receivedReply(); 
  }
  
  int getSendWindowSize(int streamId) {
    if (streamId == 0)
      return this.sendWindowSize.get(); 
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null) ? state.getSendWindowSize() : -1;
  }
  
  int updateSendWindowSize(int streamId, int deltaWindowSize) {
    if (streamId == 0)
      return this.sendWindowSize.addAndGet(deltaWindowSize); 
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null) ? state.updateSendWindowSize(deltaWindowSize) : -1;
  }
  
  int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
    if (streamId == 0)
      return this.receiveWindowSize.addAndGet(deltaWindowSize); 
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    if (state == null)
      return -1; 
    if (deltaWindowSize > 0)
      state.setReceiveWindowSizeLowerBound(0); 
    return state.updateReceiveWindowSize(deltaWindowSize);
  }
  
  int getReceiveWindowSizeLowerBound(int streamId) {
    if (streamId == 0)
      return 0; 
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null) ? state.getReceiveWindowSizeLowerBound() : 0;
  }
  
  void updateAllSendWindowSizes(int deltaWindowSize) {
    for (StreamState state : this.activeStreams.values())
      state.updateSendWindowSize(deltaWindowSize); 
  }
  
  void updateAllReceiveWindowSizes(int deltaWindowSize) {
    for (StreamState state : this.activeStreams.values()) {
      state.updateReceiveWindowSize(deltaWindowSize);
      if (deltaWindowSize < 0)
        state.setReceiveWindowSizeLowerBound(deltaWindowSize); 
    } 
  }
  
  boolean putPendingWrite(int streamId, PendingWrite pendingWrite) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null && state.putPendingWrite(pendingWrite));
  }
  
  PendingWrite getPendingWrite(int streamId) {
    if (streamId == 0) {
      for (Map.Entry<Integer, StreamState> e : activeStreams().entrySet()) {
        StreamState streamState = e.getValue();
        if (streamState.getSendWindowSize() > 0) {
          PendingWrite pendingWrite = streamState.getPendingWrite();
          if (pendingWrite != null)
            return pendingWrite; 
        } 
      } 
      return null;
    } 
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null) ? state.getPendingWrite() : null;
  }
  
  PendingWrite removePendingWrite(int streamId) {
    StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
    return (state != null) ? state.removePendingWrite() : null;
  }
  
  public static final class SpdySession {}
  
  private final class SpdySession {}
  
  private static final class SpdySession {}
}
