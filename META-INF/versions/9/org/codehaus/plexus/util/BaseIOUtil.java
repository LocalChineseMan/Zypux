package META-INF.versions.9.org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;

abstract class BaseIOUtil {
  private static final int DEFAULT_BUFFER_SIZE = 16384;
  
  static void copy(InputStream input, OutputStream output) throws IOException {
    input.transferTo(output);
  }
  
  static void copy(Reader input, Writer output) throws IOException {
    IOUtil.copy(input, output, 16384);
  }
}
