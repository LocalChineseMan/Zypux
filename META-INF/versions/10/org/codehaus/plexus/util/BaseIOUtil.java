package META-INF.versions.10.org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

abstract class BaseIOUtil {
  static void copy(InputStream input, OutputStream output) throws IOException {
    input.transferTo(output);
  }
  
  static void copy(Reader input, Writer output) throws IOException {
    input.transferTo(output);
  }
}
