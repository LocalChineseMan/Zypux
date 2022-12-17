package org.tukaani.xz;

interface FilterEncoder extends FilterCoder {
  long getFilterID();
  
  byte[] getFilterProps();
  
  boolean supportsFlushing();
  
  FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache);
}
