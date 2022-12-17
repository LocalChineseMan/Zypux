package org.apache.maven.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import javax.lang.model.SourceVersion;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PrefixedPropertiesValueSource;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.StringUtils;

public class MavenArchiver {
  private static final String CREATED_BY = "Maven Archiver";
  
  public static final String SIMPLE_LAYOUT = "${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}";
  
  public static final String REPOSITORY_LAYOUT = "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}";
  
  public static final String SIMPLE_LAYOUT_NONUNIQUE = "${artifact.artifactId}-${artifact.baseVersion}${dashClassifier?}.${artifact.extension}";
  
  public static final String REPOSITORY_LAYOUT_NONUNIQUE = "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/${artifact.artifactId}-${artifact.baseVersion}${dashClassifier?}.${artifact.extension}";
  
  private static final Instant DATE_MIN = Instant.parse("1980-01-01T00:00:02Z");
  
  private static final Instant DATE_MAX = Instant.parse("2099-12-31T23:59:59Z");
  
  private static final List<String> ARTIFACT_EXPRESSION_PREFIXES;
  
  private JarArchiver archiver;
  
  private File archiveFile;
  
  private String createdBy;
  
  static {
    List<String> artifactExpressionPrefixes = new ArrayList<>();
    artifactExpressionPrefixes.add("artifact.");
    ARTIFACT_EXPRESSION_PREFIXES = artifactExpressionPrefixes;
  }
  
  static boolean isValidModuleName(String name) {
    return SourceVersion.isName(name);
  }
  
  private boolean buildJdkSpecDefaultEntry = true;
  
  public Manifest getManifest(MavenSession session, MavenProject project, MavenArchiveConfiguration config) throws ManifestException, DependencyResolutionRequiredException {
    boolean hasManifestEntries = !config.isManifestEntriesEmpty();
    Map<String, String> entries = hasManifestEntries ? config.getManifestEntries() : Collections.<String, String>emptyMap();
    Manifest manifest = getManifest(session, project, config.getManifest(), entries);
    if (hasManifestEntries)
      for (Map.Entry<String, String> entry : entries.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        Manifest.ExistingAttribute existingAttribute = manifest.getMainSection().getAttribute(key);
        if (key.equals(Attributes.Name.CLASS_PATH.toString()) && existingAttribute != null) {
          existingAttribute.setValue(value + " " + existingAttribute.getValue());
          continue;
        } 
        addManifestAttribute(manifest, key, value);
      }  
    if (!config.isManifestSectionsEmpty())
      for (ManifestSection section : config.getManifestSections()) {
        Manifest.Section theSection = new Manifest.Section();
        theSection.setName(section.getName());
        if (!section.isManifestEntriesEmpty()) {
          Map<String, String> sectionEntries = section.getManifestEntries();
          for (Map.Entry<String, String> entry : sectionEntries.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Manifest.Attribute attr = new Manifest.Attribute(key, value);
            theSection.addConfiguredAttribute(attr);
          } 
        } 
        manifest.addConfiguredSection(theSection);
      }  
    return manifest;
  }
  
  public Manifest getManifest(MavenProject project, ManifestConfiguration config) throws ManifestException, DependencyResolutionRequiredException {
    return getManifest(null, project, config, Collections.emptyMap());
  }
  
  public Manifest getManifest(MavenSession mavenSession, MavenProject project, ManifestConfiguration config) throws ManifestException, DependencyResolutionRequiredException {
    return getManifest(mavenSession, project, config, Collections.emptyMap());
  }
  
  private void addManifestAttribute(Manifest manifest, Map<String, String> map, String key, String value) throws ManifestException {
    if (map.containsKey(key))
      return; 
    addManifestAttribute(manifest, key, value);
  }
  
  private void addManifestAttribute(Manifest manifest, String key, String value) throws ManifestException {
    if (!StringUtils.isEmpty(value)) {
      Manifest.Attribute attr = new Manifest.Attribute(key, value);
      manifest.addConfiguredAttribute(attr);
    } else {
      Manifest.Attribute attr = new Manifest.Attribute(key, "");
      manifest.addConfiguredAttribute(attr);
    } 
  }
  
  protected Manifest getManifest(MavenSession session, MavenProject project, ManifestConfiguration config, Map<String, String> entries) throws ManifestException, DependencyResolutionRequiredException {
    Manifest m = new Manifest();
    if (config.isAddDefaultEntries())
      handleDefaultEntries(m, entries); 
    if (config.isAddBuildEnvironmentEntries())
      handleBuildEnvironmentEntries(session, m, entries); 
    if (config.isAddClasspath()) {
      StringBuilder classpath = new StringBuilder();
      List<String> artifacts = project.getRuntimeClasspathElements();
      String classpathPrefix = config.getClasspathPrefix();
      String layoutType = config.getClasspathLayoutType();
      String layout = config.getCustomClasspathLayout();
      StringSearchInterpolator stringSearchInterpolator = new StringSearchInterpolator();
      for (String artifactFile : artifacts) {
        File f = new File(artifactFile);
        if (f.getAbsoluteFile().isFile()) {
          Artifact artifact = findArtifactWithFile(project.getArtifacts(), f);
          if (classpath.length() > 0)
            classpath.append(" "); 
          classpath.append(classpathPrefix);
          if (artifact == null || layoutType == null) {
            classpath.append(f.getName());
            continue;
          } 
          List<ValueSource> valueSources = new ArrayList<>();
          handleExtraExpression(artifact, valueSources);
          for (ValueSource vs : valueSources)
            stringSearchInterpolator.addValueSource(vs); 
          PrefixAwareRecursionInterceptor prefixAwareRecursionInterceptor = new PrefixAwareRecursionInterceptor(ARTIFACT_EXPRESSION_PREFIXES);
          try {
            switch (layoutType) {
              case "simple":
                if (config.isUseUniqueVersions()) {
                  classpath.append(stringSearchInterpolator.interpolate("${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}", (RecursionInterceptor)prefixAwareRecursionInterceptor));
                  break;
                } 
                classpath.append(stringSearchInterpolator.interpolate("${artifact.artifactId}-${artifact.baseVersion}${dashClassifier?}.${artifact.extension}", (RecursionInterceptor)prefixAwareRecursionInterceptor));
                break;
              case "repository":
                if (config.isUseUniqueVersions()) {
                  classpath.append(stringSearchInterpolator.interpolate("${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}", (RecursionInterceptor)prefixAwareRecursionInterceptor));
                  break;
                } 
                classpath.append(stringSearchInterpolator.interpolate("${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/${artifact.artifactId}-${artifact.baseVersion}${dashClassifier?}.${artifact.extension}", (RecursionInterceptor)prefixAwareRecursionInterceptor));
                break;
              case "custom":
                if (layout == null)
                  throw new ManifestException("custom layout type was declared, but custom layout expression was not specified. Check your <archive><manifest><customLayout/> element."); 
                classpath.append(stringSearchInterpolator.interpolate(layout, (RecursionInterceptor)prefixAwareRecursionInterceptor));
                break;
              default:
                throw new ManifestException("Unknown classpath layout type: '" + layoutType + "'. Check your <archive><manifest><layoutType/> element.");
            } 
          } catch (InterpolationException e) {
            ManifestException error = new ManifestException("Error interpolating artifact path for classpath entry: " + e.getMessage());
            error.initCause((Throwable)e);
            throw error;
          } finally {
            for (ValueSource vs : valueSources)
              stringSearchInterpolator.removeValuesSource(vs); 
          } 
        } 
      } 
      if (classpath.length() > 0)
        addManifestAttribute(m, "Class-Path", classpath.toString()); 
    } 
    if (config.isAddDefaultSpecificationEntries())
      handleSpecificationEntries(project, entries, m); 
    if (config.isAddDefaultImplementationEntries())
      handleImplementationEntries(project, entries, m); 
    String mainClass = config.getMainClass();
    if (mainClass != null && !"".equals(mainClass))
      addManifestAttribute(m, entries, "Main-Class", mainClass); 
    if (config.isAddExtensions())
      handleExtensions(project, entries, m); 
    addCustomEntries(m, entries, config);
    return m;
  }
  
  private void handleExtraExpression(Artifact artifact, List<ValueSource> valueSources) {
    valueSources.add(new PrefixedObjectValueSource(ARTIFACT_EXPRESSION_PREFIXES, artifact, true));
    valueSources.add(new PrefixedObjectValueSource(ARTIFACT_EXPRESSION_PREFIXES, artifact
          .getArtifactHandler(), true));
    Properties extraExpressions = new Properties();
    if (!artifact.isSnapshot())
      extraExpressions.setProperty("baseVersion", artifact.getVersion()); 
    extraExpressions.setProperty("groupIdPath", artifact.getGroupId().replace('.', '/'));
    if (StringUtils.isNotEmpty(artifact.getClassifier())) {
      extraExpressions.setProperty("dashClassifier", "-" + artifact.getClassifier());
      extraExpressions.setProperty("dashClassifier?", "-" + artifact.getClassifier());
    } else {
      extraExpressions.setProperty("dashClassifier", "");
      extraExpressions.setProperty("dashClassifier?", "");
    } 
    valueSources.add(new PrefixedPropertiesValueSource(ARTIFACT_EXPRESSION_PREFIXES, extraExpressions, true));
  }
  
  private void handleExtensions(MavenProject project, Map<String, String> entries, Manifest m) throws ManifestException {
    StringBuilder extensionsList = new StringBuilder();
    Set<Artifact> artifacts = project.getArtifacts();
    for (Artifact artifact : artifacts) {
      if (!"test".equals(artifact.getScope()))
        if ("jar".equals(artifact.getType())) {
          if (extensionsList.length() > 0)
            extensionsList.append(" "); 
          extensionsList.append(artifact.getArtifactId());
        }  
    } 
    if (extensionsList.length() > 0)
      addManifestAttribute(m, entries, "Extension-List", extensionsList.toString()); 
    for (Artifact artifact : artifacts) {
      if ("jar".equals(artifact.getType())) {
        String artifactId = artifact.getArtifactId().replace('.', '_');
        String ename = artifactId + "-Extension-Name";
        addManifestAttribute(m, entries, ename, artifact.getArtifactId());
        String iname = artifactId + "-Implementation-Version";
        addManifestAttribute(m, entries, iname, artifact.getVersion());
        if (artifact.getRepository() != null) {
          iname = artifactId + "-Implementation-URL";
          String url = artifact.getRepository().getUrl() + "/" + artifact;
          addManifestAttribute(m, entries, iname, url);
        } 
      } 
    } 
  }
  
  private void handleImplementationEntries(MavenProject project, Map<String, String> entries, Manifest m) throws ManifestException {
    addManifestAttribute(m, entries, "Implementation-Title", project.getName());
    addManifestAttribute(m, entries, "Implementation-Version", project.getVersion());
    if (project.getOrganization() != null)
      addManifestAttribute(m, entries, "Implementation-Vendor", project.getOrganization().getName()); 
  }
  
  private void handleSpecificationEntries(MavenProject project, Map<String, String> entries, Manifest m) throws ManifestException {
    addManifestAttribute(m, entries, "Specification-Title", project.getName());
    try {
      ArtifactVersion version = project.getArtifact().getSelectedVersion();
      String specVersion = String.format("%s.%s", new Object[] { Integer.valueOf(version.getMajorVersion()), Integer.valueOf(version.getMinorVersion()) });
      addManifestAttribute(m, entries, "Specification-Version", specVersion);
    } catch (OverConstrainedVersionException e) {
      throw new ManifestException("Failed to get selected artifact version to calculate the specification version: " + e
          .getMessage());
    } 
    if (project.getOrganization() != null)
      addManifestAttribute(m, entries, "Specification-Vendor", project.getOrganization().getName()); 
  }
  
  private void addCustomEntries(Manifest m, Map<String, String> entries, ManifestConfiguration config) throws ManifestException {
    if (config.getPackageName() != null)
      addManifestAttribute(m, entries, "Package", config.getPackageName()); 
  }
  
  public JarArchiver getArchiver() {
    return this.archiver;
  }
  
  public void setArchiver(JarArchiver archiver) {
    this.archiver = archiver;
  }
  
  public void setOutputFile(File outputFile) {
    this.archiveFile = outputFile;
  }
  
  public void createArchive(MavenSession session, MavenProject project, MavenArchiveConfiguration archiveConfiguration) throws ManifestException, IOException, DependencyResolutionRequiredException {
    MavenProject workingProject = project.clone();
    boolean forced = archiveConfiguration.isForced();
    if (archiveConfiguration.isAddMavenDescriptor()) {
      if (workingProject.getArtifact().isSnapshot())
        workingProject.setVersion(workingProject.getArtifact().getVersion()); 
      String groupId = workingProject.getGroupId();
      String artifactId = workingProject.getArtifactId();
      this.archiver.addFile(project.getFile(), "META-INF/maven/" + groupId + "/" + artifactId + "/pom.xml");
      File customPomPropertiesFile = archiveConfiguration.getPomPropertiesFile();
      File dir = new File(workingProject.getBuild().getDirectory(), "maven-archiver");
      File pomPropertiesFile = new File(dir, "pom.properties");
      (new PomPropertiesUtil()).createPomProperties(session, workingProject, (Archiver)this.archiver, customPomPropertiesFile, pomPropertiesFile, forced);
    } 
    this.archiver.setMinimalDefaultManifest(true);
    File manifestFile = archiveConfiguration.getManifestFile();
    if (manifestFile != null)
      this.archiver.setManifest(manifestFile); 
    Manifest manifest = getManifest(session, workingProject, archiveConfiguration);
    this.archiver.addConfiguredManifest(manifest);
    this.archiver.setCompress(archiveConfiguration.isCompress());
    this.archiver.setRecompressAddedZips(archiveConfiguration.isRecompressAddedZips());
    this.archiver.setIndex(archiveConfiguration.isIndex());
    this.archiver.setDestFile(this.archiveFile);
    if (archiveConfiguration.getManifest().isAddClasspath()) {
      List<String> artifacts = project.getRuntimeClasspathElements();
      for (String artifact : artifacts) {
        File f = new File(artifact);
        this.archiver.addConfiguredIndexJars(f);
      } 
    } 
    this.archiver.setForced(forced);
    if (archiveConfiguration.isForced() || this.archiver.isSupportingForced());
    String automaticModuleName = manifest.getMainSection().getAttributeValue("Automatic-Module-Name");
    if (automaticModuleName != null)
      if (!isValidModuleName(automaticModuleName))
        throw new ManifestException("Invalid automatic module name: '" + automaticModuleName + "'");  
    this.archiver.createArchive();
  }
  
  private void handleDefaultEntries(Manifest m, Map<String, String> entries) throws ManifestException {
    String createdBy = this.createdBy;
    if (createdBy == null)
      createdBy = createdBy("Maven Archiver", "org.apache.maven", "maven-archiver"); 
    addManifestAttribute(m, entries, "Created-By", createdBy);
    if (this.buildJdkSpecDefaultEntry)
      addManifestAttribute(m, entries, "Build-Jdk-Spec", System.getProperty("java.specification.version")); 
  }
  
  private void handleBuildEnvironmentEntries(MavenSession session, Manifest m, Map<String, String> entries) throws ManifestException {
    addManifestAttribute(m, entries, "Build-Tool", 
        (session != null) ? session.getSystemProperties().getProperty("maven.build.version") : "Apache Maven");
    addManifestAttribute(m, entries, "Build-Jdk", String.format("%s (%s)", new Object[] { System.getProperty("java.version"), 
            System.getProperty("java.vendor") }));
    addManifestAttribute(m, entries, "Build-Os", String.format("%s (%s; %s)", new Object[] { System.getProperty("os.name"), 
            System.getProperty("os.version"), System.getProperty("os.arch") }));
  }
  
  private Artifact findArtifactWithFile(Set<Artifact> artifacts, File file) {
    for (Artifact artifact : artifacts) {
      if (artifact.getFile() != null)
        if (artifact.getFile().equals(file))
          return artifact;  
    } 
    return null;
  }
  
  private static String getCreatedByVersion(String groupId, String artifactId) {
    Properties properties = loadOptionalProperties(MavenArchiver.class.getResourceAsStream("/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties"));
    return properties.getProperty("version");
  }
  
  private static Properties loadOptionalProperties(InputStream inputStream) {
    Properties properties = new Properties();
    if (inputStream != null)
      try {
        InputStream in = inputStream;
        try {
          properties.load(in);
          if (in != null)
            in.close(); 
        } catch (Throwable throwable) {
          if (in != null)
            try {
              in.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IllegalArgumentException|IOException illegalArgumentException) {} 
    return properties;
  }
  
  public void setCreatedBy(String description, String groupId, String artifactId) {
    this.createdBy = createdBy(description, groupId, artifactId);
  }
  
  private String createdBy(String description, String groupId, String artifactId) {
    String createdBy = description;
    String version = getCreatedByVersion(groupId, artifactId);
    if (version != null)
      createdBy = createdBy + " " + version; 
    return createdBy;
  }
  
  public void setBuildJdkSpecDefaultEntry(boolean buildJdkSpecDefaultEntry) {
    this.buildJdkSpecDefaultEntry = buildJdkSpecDefaultEntry;
  }
  
  @Deprecated
  public Date parseOutputTimestamp(String outputTimestamp) {
    return parseBuildOutputTimestamp(outputTimestamp).<Date>map(Date::from).orElse(null);
  }
  
  @Deprecated
  public Date configureReproducible(String outputTimestamp) {
    configureReproducibleBuild(outputTimestamp);
    return parseOutputTimestamp(outputTimestamp);
  }
  
  public static Optional<Instant> parseBuildOutputTimestamp(String outputTimestamp) {
    if (outputTimestamp == null)
      return Optional.empty(); 
    if (StringUtils.isNotEmpty(outputTimestamp) && StringUtils.isNumeric(outputTimestamp))
      return Optional.of(Instant.ofEpochSecond(Long.parseLong(outputTimestamp))); 
    if (outputTimestamp.length() < 2)
      return Optional.empty(); 
    try {
      Instant date = OffsetDateTime.parse(outputTimestamp).withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toInstant();
      if (date.isBefore(DATE_MIN) || date.isAfter(DATE_MAX))
        throw new IllegalArgumentException("'" + date + "' is not within the valid range " + DATE_MIN + " to " + DATE_MAX); 
      return Optional.of(date);
    } catch (DateTimeParseException pe) {
      throw new IllegalArgumentException("Invalid project.build.outputTimestamp value '" + outputTimestamp + "'", pe);
    } 
  }
  
  public void configureReproducibleBuild(String outputTimestamp) {
    parseBuildOutputTimestamp(outputTimestamp)
      .map(FileTime::from)
      .ifPresent(modifiedTime -> getArchiver().configureReproducibleBuild(modifiedTime));
  }
}
