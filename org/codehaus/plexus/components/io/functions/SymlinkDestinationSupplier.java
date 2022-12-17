package org.codehaus.plexus.components.io.functions;

import java.io.IOException;

public interface SymlinkDestinationSupplier {
  String getSymlinkDestination() throws IOException;
}
