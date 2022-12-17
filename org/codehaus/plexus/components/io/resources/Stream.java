package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import org.codehaus.plexus.components.io.functions.PlexusIoResourceConsumer;

public interface Stream {
  void forEach(PlexusIoResourceConsumer paramPlexusIoResourceConsumer) throws IOException;
}
