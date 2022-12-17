package org.codehaus.plexus.components.io.functions;

import java.io.IOException;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public interface PlexusIoResourceConsumer {
  void accept(PlexusIoResource paramPlexusIoResource) throws IOException;
}
