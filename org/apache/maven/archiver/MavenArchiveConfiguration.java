package org.apache.maven.archiver;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MavenArchiveConfiguration {
  private boolean compress = true;
  
  private boolean recompressAddedZips = true;
  
  private boolean index;
  
  private boolean addMavenDescriptor = true;
  
  private File manifestFile;
  
  private ManifestConfiguration manifest;
  
  private Map<String, String> manifestEntries = new LinkedHashMap<>();
  
  private List<ManifestSection> manifestSections = new LinkedList<>();
  
  private boolean forced = true;
  
  private File pomPropertiesFile;
  
  public boolean isCompress() {
    return this.compress;
  }
  
  public boolean isRecompressAddedZips() {
    return this.recompressAddedZips;
  }
  
  public void setRecompressAddedZips(boolean recompressAddedZips) {
    this.recompressAddedZips = recompressAddedZips;
  }
  
  public boolean isIndex() {
    return this.index;
  }
  
  public boolean isAddMavenDescriptor() {
    return this.addMavenDescriptor;
  }
  
  public File getManifestFile() {
    return this.manifestFile;
  }
  
  public ManifestConfiguration getManifest() {
    if (this.manifest == null)
      this.manifest = new ManifestConfiguration(); 
    return this.manifest;
  }
  
  public void setCompress(boolean compress) {
    this.compress = compress;
  }
  
  public void setIndex(boolean index) {
    this.index = index;
  }
  
  public void setAddMavenDescriptor(boolean addMavenDescriptor) {
    this.addMavenDescriptor = addMavenDescriptor;
  }
  
  public void setManifestFile(File manifestFile) {
    this.manifestFile = manifestFile;
  }
  
  public void setManifest(ManifestConfiguration manifest) {
    this.manifest = manifest;
  }
  
  public void addManifestEntry(String key, String value) {
    this.manifestEntries.put(key, value);
  }
  
  public void addManifestEntries(Map<String, String> map) {
    this.manifestEntries.putAll(map);
  }
  
  public boolean isManifestEntriesEmpty() {
    return this.manifestEntries.isEmpty();
  }
  
  public Map<String, String> getManifestEntries() {
    return this.manifestEntries;
  }
  
  public void setManifestEntries(Map<String, String> manifestEntries) {
    this.manifestEntries = manifestEntries;
  }
  
  public void addManifestSection(ManifestSection section) {
    this.manifestSections.add(section);
  }
  
  public void addManifestSections(List<ManifestSection> list) {
    this.manifestSections.addAll(list);
  }
  
  public boolean isManifestSectionsEmpty() {
    return this.manifestSections.isEmpty();
  }
  
  public List<ManifestSection> getManifestSections() {
    return this.manifestSections;
  }
  
  public void setManifestSections(List<ManifestSection> manifestSections) {
    this.manifestSections = manifestSections;
  }
  
  public boolean isForced() {
    return this.forced;
  }
  
  public void setForced(boolean forced) {
    this.forced = forced;
  }
  
  public File getPomPropertiesFile() {
    return this.pomPropertiesFile;
  }
  
  public void setPomPropertiesFile(File pomPropertiesFile) {
    this.pomPropertiesFile = pomPropertiesFile;
  }
}
