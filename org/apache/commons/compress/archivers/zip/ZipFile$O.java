package org.apache.commons.compress.archivers.zip;

final class OffsetEntry {
  private OffsetEntry() {}
  
  private long headerOffset = -1L;
  
  private long dataOffset = -1L;
}
