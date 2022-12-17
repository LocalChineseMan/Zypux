package org.apache.maven.plugins.jar;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

public abstract class AbstractJarMojo extends AbstractMojo {
  private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };
  
  private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
  
  private static final String MODULE_DESCRIPTOR_FILE_NAME = "module-info.class";
  
  @Parameter
  private String[] includes;
  
  @Parameter
  private String[] excludes;
  
  @Parameter(defaultValue = "${project.build.directory}", required = true)
  private File outputDirectory;
  
  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  private String finalName;
  
  @Component
  private Map<String, Archiver> archivers;
  
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;
  
  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;
  
  @Parameter
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
  
  @Parameter(property = "jar.useDefaultManifestFile", defaultValue = "false")
  private boolean useDefaultManifestFile;
  
  @Component
  private MavenProjectHelper projectHelper;
  
  @Parameter(property = "maven.jar.forceCreation", defaultValue = "false")
  private boolean forceCreation;
  
  @Parameter(defaultValue = "false")
  private boolean skipIfEmpty;
  
  @Parameter(defaultValue = "${project.build.outputTimestamp}")
  private String outputTimestamp;
  
  protected abstract File getClassesDirectory();
  
  protected final MavenProject getProject() {
    return this.project;
  }
  
  protected abstract String getClassifier();
  
  protected abstract String getType();
  
  protected File getJarFile(File basedir, String resultFinalName, String classifier) {
    String fileName;
    if (basedir == null)
      throw new IllegalArgumentException("basedir is not allowed to be null"); 
    if (resultFinalName == null)
      throw new IllegalArgumentException("finalName is not allowed to be null"); 
    if (hasClassifier()) {
      fileName = resultFinalName + "-" + classifier + ".jar";
    } else {
      fileName = resultFinalName + ".jar";
    } 
    return new File(basedir, fileName);
  }
  
  public File createArchive() throws MojoExecutionException {
    File jarFile = getJarFile(this.outputDirectory, this.finalName, getClassifier());
    FileSetManager fileSetManager = new FileSetManager();
    FileSet jarContentFileSet = new FileSet();
    jarContentFileSet.setDirectory(getClassesDirectory().getAbsolutePath());
    jarContentFileSet.setIncludes(Arrays.asList(getIncludes()));
    jarContentFileSet.setExcludes(Arrays.asList(getExcludes()));
    boolean containsModuleDescriptor = false;
    String[] includedFiles = fileSetManager.getIncludedFiles(jarContentFileSet);
    for (String includedFile : includedFiles) {
      if (includedFile.endsWith("module-info.class")) {
        containsModuleDescriptor = true;
        break;
      } 
    } 
    String archiverName = containsModuleDescriptor ? "mjar" : "jar";
    MavenArchiver archiver = new MavenArchiver();
    archiver.setCreatedBy("Maven JAR Plugin", "org.apache.maven.plugins", "maven-jar-plugin");
    archiver.setArchiver((JarArchiver)this.archivers.get(archiverName));
    archiver.setOutputFile(jarFile);
    archiver.configureReproducibleBuild(this.outputTimestamp);
    this.archive.setForced(this.forceCreation);
    try {
      File contentDirectory = getClassesDirectory();
      if (!contentDirectory.exists()) {
        if (!this.forceCreation)
          getLog().warn("JAR will be empty - no content was marked for inclusion!"); 
      } else {
        archiver.getArchiver().addDirectory(contentDirectory, getIncludes(), getExcludes());
      } 
      archiver.createArchive(this.session, this.project, this.archive);
      return jarFile;
    } catch (Exception e) {
      throw new MojoExecutionException("Error assembling JAR", e);
    } 
  }
  
  public void execute() throws MojoExecutionException {
    if (this.useDefaultManifestFile)
      throw new MojoExecutionException("You are using 'useDefaultManifestFile' which has been removed from the maven-jar-plugin. Please see the >>Major Version Upgrade to version 3.0.0<< on the plugin site."); 
    if (this.skipIfEmpty && (!getClassesDirectory().exists() || (getClassesDirectory().list()).length < 1)) {
      getLog().info("Skipping packaging of the " + getType());
    } else {
      File jarFile = createArchive();
      if (hasClassifier()) {
        this.projectHelper.attachArtifact(getProject(), getType(), getClassifier(), jarFile);
      } else {
        if (projectHasAlreadySetAnArtifact())
          throw new MojoExecutionException("You have to use a classifier to attach supplemental artifacts to the project instead of replacing them."); 
        getProject().getArtifact().setFile(jarFile);
      } 
    } 
  }
  
  private boolean projectHasAlreadySetAnArtifact() {
    if (getProject().getArtifact().getFile() == null)
      return false; 
    return getProject().getArtifact().getFile().isFile();
  }
  
  protected boolean hasClassifier() {
    return (getClassifier() != null && getClassifier().trim().length() > 0);
  }
  
  private String[] getIncludes() {
    if (this.includes != null && this.includes.length > 0)
      return this.includes; 
    return DEFAULT_INCLUDES;
  }
  
  private String[] getExcludes() {
    if (this.excludes != null && this.excludes.length > 0)
      return this.excludes; 
    return DEFAULT_EXCLUDES;
  }
}
