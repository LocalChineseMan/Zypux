package org.apache.maven.shared.model.fileset.mappers;

import java.io.File;

public class PackageNameMapper extends GlobPatternMapper {
  protected String extractVariablePart(String name) {
    String var = name.substring(this.prefixLength, name.length() - this.postfixLength);
    return var.replace(File.separatorChar, '.');
  }
}
