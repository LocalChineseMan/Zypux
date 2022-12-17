package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.codehaus.plexus.archiver.ArchiveFile;
import org.codehaus.plexus.archiver.util.Streams;

public class TarFile implements ArchiveFile {
  private final File file;
  
  private TarArchiveInputStream inputStream;
  
  private TarArchiveEntry currentEntry;
  
  public TarFile(File file) {
    this.file = file;
  }
  
  public Enumeration<ArchiveEntry> getEntries() throws IOException {
    if (this.inputStream != null)
      close(); 
    open();
    return new Enumeration<ArchiveEntry>() {
        boolean currentEntryValid;
        
        public boolean hasMoreElements() {
          if (!this.currentEntryValid)
            try {
              TarFile.this.currentEntry = TarFile.this.inputStream.getNextTarEntry();
            } catch (IOException e) {
              throw new UndeclaredThrowableException(e);
            }  
          return (TarFile.this.currentEntry != null);
        }
        
        public ArchiveEntry nextElement() {
          if (TarFile.this.currentEntry == null)
            throw new NoSuchElementException(); 
          this.currentEntryValid = false;
          return (ArchiveEntry)TarFile.this.currentEntry;
        }
      };
  }
  
  public void close() throws IOException {
    if (this.inputStream != null) {
      this.inputStream.close();
      this.inputStream = null;
    } 
  }
  
  public InputStream getInputStream(ArchiveEntry entry) throws IOException {
    return getInputStream(new TarArchiveEntry(entry.getName()));
  }
  
  public InputStream getInputStream(TarArchiveEntry entry) throws IOException {
    if (entry.equals(this.currentEntry) && this.inputStream != null)
      return new FilterInputStream((InputStream)this.inputStream) {
          public void close() throws IOException {}
        }; 
    return getInputStream(entry, this.currentEntry);
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return Streams.fileInputStream(file);
  }
  
  private InputStream getInputStream(TarArchiveEntry entry, TarArchiveEntry currentEntry) throws IOException {
    if (currentEntry == null || this.inputStream == null) {
      if (this.inputStream != null)
        close(); 
      open();
      if (!findEntry(entry, null))
        throw new IOException("Unknown entry: " + entry.getName()); 
    } else {
      if (findEntry(entry, null))
        return getInputStream(entry); 
      close();
      open();
      if (!findEntry(entry, currentEntry))
        throw new IOException("No such entry: " + entry.getName()); 
    } 
    return getInputStream(entry);
  }
  
  private void open() throws IOException {
    this.inputStream = new TarArchiveInputStream(Streams.bufferedInputStream(getInputStream(this.file)), "UTF8");
  }
  
  private boolean findEntry(TarArchiveEntry entry, TarArchiveEntry currentEntry) throws IOException {
    do {
      this.currentEntry = this.inputStream.getNextTarEntry();
      if (this.currentEntry == null || (currentEntry != null && this.currentEntry
        .equals(currentEntry)))
        return false; 
    } while (!this.currentEntry.equals(entry));
    return true;
  }
}
