package org.codehaus.plexus.components.io.fileselectors;

import java.io.IOException;
import javax.annotation.Nonnull;

public interface FileSelector {
  boolean isSelected(@Nonnull FileInfo paramFileInfo) throws IOException;
}
