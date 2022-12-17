package org.apache.maven.plugins.jar;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "test-jar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true, requiresDependencyResolution = ResolutionScope.TEST)
public class TestJarMojo extends AbstractJarMojo {
  @Parameter(property = "maven.test.skip")
  private boolean skip;
  
  @Parameter(defaultValue = "${project.build.testOutputDirectory}", required = true)
  private File testClassesDirectory;
  
  @Parameter(defaultValue = "tests")
  private String classifier;
  
  protected String getClassifier() {
    return this.classifier;
  }
  
  protected String getType() {
    return "test-jar";
  }
  
  protected File getClassesDirectory() {
    return this.testClassesDirectory;
  }
  
  public void execute() throws MojoExecutionException {
    if (this.skip) {
      getLog().info("Skipping packaging of the test-jar");
    } else {
      super.execute();
    } 
  }
}
