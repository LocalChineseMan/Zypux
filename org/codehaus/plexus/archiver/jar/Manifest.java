package org.codehaus.plexus.archiver.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.codehaus.plexus.archiver.ArchiverException;

public class Manifest extends Manifest implements Iterable<String> {
  private static final String ATTRIBUTE_NAME = "Name";
  
  private static final String ATTRIBUTE_FROM = "From";
  
  private static final String DEFAULT_MANIFEST_VERSION = "1.0";
  
  private static final int MAX_LINE_LENGTH = 72;
  
  private static final int MAX_SECTION_LENGTH = 70;
  
  static final String EOL = "\r\n";
  
  public static class BaseAttribute {
    protected String name = null;
    
    public String getName() {
      return this.name;
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (!(o instanceof BaseAttribute))
        return false; 
      BaseAttribute that = (BaseAttribute)o;
      if ((this.name != null) ? !this.name.equals(that.name) : (that.name != null))
        return false; 
    }
    
    public int hashCode() {
      return (this.name != null) ? this.name.hashCode() : 0;
    }
  }
  
  public static class Attribute extends BaseAttribute implements Iterable<String> {
    private Vector<String> values = new Vector<>();
    
    private int currentIndex = 0;
    
    public Attribute(String name, String value) {
      this.name = name;
      setValue(value);
    }
    
    public Iterator<String> iterator() {
      return this.values.iterator();
    }
    
    public int hashCode() {
      int hashCode = super.hashCode();
      hashCode += this.values.hashCode();
      return hashCode;
    }
    
    public boolean equals(Object rhs) {
      if (super.equals(rhs))
        return false; 
      if (rhs == null || rhs.getClass() != getClass())
        return false; 
      if (rhs == this)
        return true; 
      Attribute rhsAttribute = (Attribute)rhs;
      String lhsKey = getKey();
      String rhsKey = rhsAttribute.getKey();
      if ((lhsKey == null && rhsKey != null) || (lhsKey != null && rhsKey == null) || !lhsKey.equals(rhsKey))
        return false; 
      return (rhsAttribute.values != null && this.values.equals(rhsAttribute.values));
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public String getKey() {
      return getKey(this.name);
    }
    
    private static String getKey(String name) {
      if (name == null)
        return null; 
      return name.toLowerCase(Locale.ENGLISH);
    }
    
    public void setValue(String value) {
      if (this.currentIndex >= this.values.size()) {
        this.values.addElement(value);
        this.currentIndex = this.values.size() - 1;
      } else {
        this.values.setElementAt(value, this.currentIndex);
      } 
    }
    
    public String getValue() {
      if (this.values.size() == 0)
        return null; 
      String fullValue = "";
      for (String value : this.values)
        fullValue = fullValue + value + " "; 
      return fullValue.trim();
    }
    
    public void addValue(String value) {
      this.currentIndex++;
      setValue(value);
    }
    
    void write(Writer writer) throws IOException {
      for (String value : this.values)
        writeValue(writer, value); 
    }
    
    private void writeValue(Writer writer, String value) throws IOException {
      String nameValue = this.name + ": " + value;
      StringTokenizer tokenizer = new StringTokenizer(nameValue, "\n\r");
      String prefix = "";
      while (tokenizer.hasMoreTokens()) {
        writeLine(writer, prefix + tokenizer.nextToken());
        prefix = " ";
      } 
    }
    
    private void writeLine(Writer writer, String line) throws IOException {
      while ((line.getBytes("UTF-8")).length > 70) {
        int breakIndex = Math.min(line.length(), 70);
        String section = line.substring(0, breakIndex);
        while ((section.getBytes("UTF-8")).length > 70 && breakIndex > 0) {
          breakIndex--;
          section = line.substring(0, breakIndex);
        } 
        if (breakIndex == 0)
          throw new IOException("Unable to write manifest line " + line); 
        writer.write(section + "\r\n");
        line = " " + line.substring(breakIndex);
      } 
      writer.write(line + "\r\n");
    }
    
    public Attribute() {}
  }
  
  public class ExistingAttribute extends Attribute implements Iterable<String> {
    private final Attributes attributes;
    
    public ExistingAttribute(Attributes attributes, String name) {
      this.attributes = attributes;
      this.name = name;
    }
    
    public Iterator<String> iterator() {
      return Manifest.getKeys(this.attributes).iterator();
    }
    
    public void setName(String name) {
      throw new UnsupportedOperationException("Cant do this");
    }
    
    public String getKey() {
      return this.name;
    }
    
    public void setValue(String value) {
      this.attributes.putValue(this.name, value);
    }
    
    public String getValue() {
      return this.attributes.getValue(this.name);
    }
    
    public void addValue(String value) {
      String value1 = getValue();
      value1 = (value1 != null) ? (" " + value) : value;
      setValue(value1);
    }
    
    void write(Writer writer) throws IOException {
      throw new UnsupportedOperationException("Cant do this");
    }
  }
  
  private static Collection<String> getKeys(Attributes attributes) {
    Collection<String> result = new ArrayList<>();
    for (Object objectObjectEntry : attributes.keySet())
      result.add(objectObjectEntry.toString()); 
    return result;
  }
  
  public static class Section implements Iterable<String> {
    private Vector<String> warnings = new Vector<>();
    
    private String name = null;
    
    private Hashtable<String, Manifest.Attribute> attributes = new Hashtable<>();
    
    private Vector<String> attributeIndex = new Vector<>();
    
    public void setName(String name) {
      this.name = name;
    }
    
    public String getName() {
      return this.name;
    }
    
    public Iterator<String> iterator() {
      return this.attributes.keySet().iterator();
    }
    
    public Manifest.Attribute getAttribute(String attributeName) {
      return this.attributes.get(attributeName.toLowerCase(Locale.ENGLISH));
    }
    
    public void addConfiguredAttribute(Manifest.Attribute attribute) throws ManifestException {
      String check = addAttributeAndCheck(attribute);
      if (check != null)
        throw new ManifestException("Specify the section name using the \"name\" attribute of the <section> element rather than using a \"Name\" manifest attribute"); 
    }
    
    public String addAttributeAndCheck(Manifest.Attribute attribute) throws ManifestException {
      if (attribute.getName() == null || attribute.getValue() == null)
        throw new ManifestException("Attributes must have name and value"); 
      if (attribute.getKey().equalsIgnoreCase("Name")) {
        this.warnings.addElement("\"Name\" attributes should not occur in the main section and must be the first element in all other sections: \"" + attribute
            
            .getName() + ": " + attribute.getValue() + "\"");
        return attribute.getValue();
      } 
      if (attribute.getKey().startsWith(Manifest.Attribute.getKey("From"))) {
        this.warnings.addElement("Manifest attributes should not start with \"From\" in \"" + attribute
            .getName() + ": " + attribute.getValue() + "\"");
      } else {
        String attributeKey = attribute.getKey();
        if (attributeKey.equalsIgnoreCase("Class-Path")) {
          Manifest.Attribute classpathAttribute = this.attributes.get(attributeKey);
          if (classpathAttribute == null) {
            storeAttribute(attribute);
          } else {
            this.warnings.addElement("Multiple Class-Path attributes are supported but violate the Jar specification and may not be correctly processed in all environments");
            for (String value : attribute)
              classpathAttribute.addValue(value); 
          } 
        } else {
          if (this.attributes.containsKey(attributeKey))
            throw new ManifestException("The attribute \"" + attribute.getName() + "\" may not occur more than once in the same section"); 
          storeAttribute(attribute);
        } 
      } 
      return null;
    }
    
    protected void storeAttribute(Manifest.Attribute attribute) {
      if (attribute == null)
        return; 
      String attributeKey = attribute.getKey();
      this.attributes.put(attributeKey, attribute);
      if (!this.attributeIndex.contains(attributeKey))
        this.attributeIndex.addElement(attributeKey); 
    }
    
    public Enumeration<String> getWarnings() {
      return this.warnings.elements();
    }
    
    public int hashCode() {
      int hashCode = 0;
      if (this.name != null)
        hashCode += this.name.hashCode(); 
      hashCode += this.attributes.hashCode();
      return hashCode;
    }
    
    public boolean equals(Object rhs) {
      if (rhs == null || rhs.getClass() != getClass())
        return false; 
      if (rhs == this)
        return true; 
      Section rhsSection = (Section)rhs;
      return (rhsSection.attributes != null && this.attributes.equals(rhsSection.attributes));
    }
  }
  
  public class ExistingSection implements Iterable<String> {
    private final Attributes backingAttributes;
    
    private final String sectionName;
    
    public ExistingSection(Attributes backingAttributes, String sectionName) {
      this.backingAttributes = backingAttributes;
      this.sectionName = sectionName;
    }
    
    public Iterator<String> iterator() {
      return Manifest.getKeys(this.backingAttributes).iterator();
    }
    
    public Manifest.ExistingAttribute getAttribute(String attributeName) {
      Attributes.Name name = new Attributes.Name(attributeName);
      return this.backingAttributes.containsKey(name) ? 
        new Manifest.ExistingAttribute(this.backingAttributes, attributeName) : 
        null;
    }
    
    public String getName() {
      return this.sectionName;
    }
    
    public String getAttributeValue(String attributeName) {
      return this.backingAttributes.getValue(attributeName);
    }
    
    public void removeAttribute(String attributeName) {
      this.backingAttributes.remove(new Attributes.Name(attributeName));
    }
    
    public void addConfiguredAttribute(Manifest.Attribute attribute) throws ManifestException {
      this.backingAttributes.putValue(attribute.getName(), attribute.getValue());
    }
    
    public String addAttributeAndCheck(Manifest.Attribute attribute) throws ManifestException {
      return Manifest.remap(this.backingAttributes, attribute);
    }
    
    public int hashCode() {
      return this.backingAttributes.hashCode();
    }
    
    public boolean equals(Object rhs) {
      return (rhs instanceof ExistingSection && this.backingAttributes.equals(((ExistingSection)rhs).backingAttributes));
    }
  }
  
  public Iterator<String> iterator() {
    return getEntries().keySet().iterator();
  }
  
  private Section mainSection = new Section();
  
  public static Manifest getDefaultManifest(boolean minimalDefaultManifest) throws ArchiverException {
    Manifest defaultManifest = new Manifest();
    defaultManifest.getMainAttributes().putValue("Manifest-Version", "1.0");
    if (!minimalDefaultManifest) {
      String createdBy = "Plexus Archiver";
      String plexusArchiverVersion = JdkManifestFactory.getArchiverVersion();
      if (plexusArchiverVersion != null)
        createdBy = createdBy + " " + plexusArchiverVersion; 
      defaultManifest.getMainAttributes().putValue("Created-By", createdBy);
    } 
    return defaultManifest;
  }
  
  public static Manifest getDefaultManifest() throws ArchiverException {
    return getDefaultManifest(false);
  }
  
  public Manifest() {
    setManifestVersion();
  }
  
  private void setManifestVersion() {
    getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
  }
  
  @Deprecated
  public Manifest(Reader r) throws ManifestException, IOException {
    super(getInputStream(r));
    setManifestVersion();
  }
  
  public Manifest(InputStream is) throws IOException {
    super(is);
    setManifestVersion();
  }
  
  public void addConfiguredSection(Section section) throws ManifestException {
    String sectionName = section.getName();
    if (sectionName == null)
      throw new ManifestException("Sections must have a name"); 
    Attributes attributes = getOrCreateAttributes(sectionName);
    for (String s : section.attributes.keySet()) {
      Attribute attribute = section.getAttribute(s);
      attributes.putValue(attribute.getName(), attribute.getValue());
    } 
  }
  
  private Attributes getOrCreateAttributes(String name) {
    Attributes attributes = getAttributes(name);
    if (attributes == null) {
      attributes = new Attributes();
      getEntries().put(name, attributes);
    } 
    return attributes;
  }
  
  public void addConfiguredAttribute(Attribute attribute) throws ManifestException {
    remap(getMainAttributes(), attribute);
  }
  
  public void write(Writer writer) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    write(byteArrayOutputStream);
    writer.write(byteArrayOutputStream.toString("UTF-8"));
  }
  
  public String toString() {
    StringWriter sw = new StringWriter();
    try {
      write(sw);
    } catch (IOException e) {
      return null;
    } 
    return sw.toString();
  }
  
  Enumeration<String> getWarnings() {
    Vector<String> warnings = new Vector<>();
    Enumeration<String> warnEnum = this.mainSection.getWarnings();
    while (warnEnum.hasMoreElements())
      warnings.addElement(warnEnum.nextElement()); 
    return warnings.elements();
  }
  
  public String getManifestVersion() {
    return "1.0";
  }
  
  public ExistingSection getMainSection() {
    return new ExistingSection(getMainAttributes(), null);
  }
  
  public ExistingSection getSection(String name) {
    Attributes attributes = getAttributes(name);
    if (attributes != null)
      return new ExistingSection(attributes, name); 
    return null;
  }
  
  @Deprecated
  private static InputStream getInputStream(Reader r) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int read;
    while ((read = r.read()) != -1)
      byteArrayOutputStream.write(read); 
    return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
  }
  
  public static String remap(Attributes backingAttributes, Attribute attribute) throws ManifestException {
    if (attribute.getKey() == null || attribute.getValue() == null)
      throw new ManifestException("Attributes must have name and value"); 
    String attributeKey = attribute.getKey();
    if (attributeKey.equalsIgnoreCase("Class-Path")) {
      String classpathAttribute = backingAttributes.getValue(attributeKey);
      if (classpathAttribute == null) {
        classpathAttribute = attribute.getValue();
      } else {
        classpathAttribute = classpathAttribute + " " + attribute.getValue();
      } 
      backingAttributes.putValue("Class-Path", classpathAttribute);
    } else {
      backingAttributes.putValue(attribute.getName(), attribute.getValue());
      if (attribute.getKey().equalsIgnoreCase("Name"))
        return attribute.getValue(); 
    } 
    return null;
  }
}
