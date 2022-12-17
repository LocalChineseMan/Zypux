package org.tukaani.xz;

interface FilterCoder {
  boolean changesSize();
  
  boolean nonLastOK();
  
  boolean lastOK();
}
