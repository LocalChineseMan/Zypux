package org.codehaus.plexus.components.io.functions;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public interface InputStreamTransformer {
  @Nonnull
  InputStream transform(@Nonnull PlexusIoResource paramPlexusIoResource, @Nonnull InputStream paramInputStream) throws IOException;
}
