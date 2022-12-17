package org.codehaus.plexus.components.io.functions;

import java.io.File;
import javax.annotation.Nonnull;

public interface FileSupplier {
  @Nonnull
  File getFile();
}
