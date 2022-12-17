package org.apache.maven.plugins.jar;

import java.io.File;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class JarMojo extends AbstractJarMojo {
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File classesDirectory;
  
  @Parameter
  private String classifier;
  
  protected String getClassifier() {
    return this.classifier;
  }
  
  protected String getType() {
    return "jar";
  }
  
  protected File getClassesDirectory() {
    return this.classesDirectory;
  }
}
