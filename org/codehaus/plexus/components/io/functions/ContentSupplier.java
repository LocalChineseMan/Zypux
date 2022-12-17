package org.codehaus.plexus.components.io.functions;

import java.io.IOException;
import java.io.InputStream;

public interface ContentSupplier {
  InputStream getContents() throws IOException;
}
