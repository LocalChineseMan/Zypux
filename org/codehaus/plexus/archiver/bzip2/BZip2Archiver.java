package org.codehaus.plexus.archiver.bzip2;

import java.io.IOException;
import javax.inject.Named;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;

@Named("bzip2")
public class BZip2Archiver extends AbstractArchiver {
  private final BZip2Compressor compressor = new BZip2Compressor();
  
  public void execute() throws ArchiverException, IOException {
    if (!checkForced())
      return; 
    ResourceIterator iter = getResources();
    if (!iter.hasNext())
      throw new EmptyArchiveException("archive cannot be empty"); 
    ArchiveEntry entry = iter.next();
    if (iter.hasNext())
      throw new ArchiverException("There is more than one file in input."); 
    this.compressor.setSource(entry.getResource());
    this.compressor.setDestFile(getDestFile());
    this.compressor.compress();
  }
  
  public boolean isSupportingForced() {
    return true;
  }
  
  protected void close() {
    this.compressor.close();
  }
  
  protected String getArchiveType() {
    return "bzip2";
  }
}
