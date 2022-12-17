package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public interface ZipExtraField {
  ZipShort getHeaderId();
  
  ZipShort getLocalFileDataLength();
  
  ZipShort getCentralDirectoryLength();
  
  byte[] getLocalFileDataData();
  
  byte[] getCentralDirectoryData();
  
  void parseFromLocalFileData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ZipException;
  
  void parseFromCentralDirectoryData(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ZipException;
}
