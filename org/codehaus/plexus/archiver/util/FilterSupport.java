package org.codehaus.plexus.archiver.util;

import java.io.InputStream;
import java.util.List;
import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.slf4j.Logger;

@Deprecated
public class FilterSupport {
  private final List<ArchiveFileFilter> filters;
  
  private final Logger logger;
  
  public FilterSupport(List<ArchiveFileFilter> filters, Logger logger) {
    this.filters = filters;
    this.logger = logger;
  }
  
  public boolean include(InputStream dataStream, String entryName) {
    boolean included = true;
    if (this.filters != null && !this.filters.isEmpty())
      for (ArchiveFileFilter filter : this.filters) {
        included = filter.include(dataStream, entryName);
        if (!included) {
          if (this.logger.isDebugEnabled())
            this.logger.debug("Entry: '" + entryName + "' excluded by filter: " + filter
                .getClass().getName()); 
          break;
        } 
      }  
    return included;
  }
}
