package org.apache.maven.archiver;

import java.util.LinkedHashMap;
import java.util.Map;

public class ManifestSection {
  private String name = null;
  
  private final Map<String, String> manifestEntries = new LinkedHashMap<>();
  
  public void addManifestEntry(String key, String value) {
    this.manifestEntries.put(key, value);
  }
  
  public Map<String, String> getManifestEntries() {
    return this.manifestEntries;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void addManifestEntries(Map<String, String> map) {
    this.manifestEntries.putAll(map);
  }
  
  public boolean isManifestEntriesEmpty() {
    return this.manifestEntries.isEmpty();
  }
}
