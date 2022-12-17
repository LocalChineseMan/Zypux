package org.codehaus.plexus.archiver.filters;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.util.SelectorUtils;

@Singleton
@Named("jar-security")
public class JarSecurityFileSelector implements FileSelector {
  public static final String[] SECURITY_FILE_PATTERNS = new String[] { "META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.SF", "META-INF/*.EC", "META-INF/*.rsa", "META-INF/*.dsa", "META-INF/*.sf", "META-INF/*.ec" };
  
  public boolean isSelected(@Nonnull FileInfo fileInfo) throws IOException {
    String name = fileInfo.getName();
    for (String pattern : SECURITY_FILE_PATTERNS) {
      if (SelectorUtils.match(pattern, name))
        return false; 
    } 
    return true;
  }
}
