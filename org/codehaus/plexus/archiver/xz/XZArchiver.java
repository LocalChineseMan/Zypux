package org.codehaus.plexus.archiver.xz;

import java.io.IOException;
import javax.inject.Named;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;

@Named("xz")
public class XZArchiver extends AbstractArchiver {
  private final XZCompressor compressor = new XZCompressor();
  
  protected void execute() throws ArchiverException, IOException {
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
  
  protected void close() throws IOException {
    this.compressor.close();
  }
  
  protected String getArchiveType() {
    return "xz";
  }
}
