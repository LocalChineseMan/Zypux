package io.netty.util.concurrent;

final class CauseHolder {
  final Throwable cause;
  
  CauseHolder(Throwable cause) {
    this.cause = cause;
  }
  
  private static final class DefaultPromise {}
}
