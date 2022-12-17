package org.apache.maven.shared.model.fileset.io.xpp3;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.Mapper;
import org.apache.maven.shared.model.fileset.SetBase;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class FileSetXpp3Reader {
  private boolean addDefaultEntities = true;
  
  public final ContentTransformer contentTransformer;
  
  public FileSetXpp3Reader() {
    this(new ContentTransformer() {
          public String transform(String source, String fieldName) {
            return source;
          }
        });
  }
  
  public FileSetXpp3Reader(ContentTransformer contentTransformer) {
    this.contentTransformer = contentTransformer;
  }
  
  private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set<String> parsed) throws XmlPullParserException {
    if (!parser.getName().equals(tagName) && !parser.getName().equals(alias))
      return false; 
    if (!parsed.add(tagName))
      throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null); 
    return true;
  }
  
  private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict) throws XmlPullParserException, IOException {
    if (strict)
      throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null); 
  }
  
  private void checkUnknownElement(XmlPullParser parser, boolean strict) throws XmlPullParserException, IOException {
    if (strict)
      throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null); 
    for (int unrecognizedTagCount = 1; unrecognizedTagCount > 0; ) {
      int eventType = parser.next();
      if (eventType == 2) {
        unrecognizedTagCount++;
        continue;
      } 
      if (eventType == 3)
        unrecognizedTagCount--; 
    } 
  }
  
  public boolean getAddDefaultEntities() {
    return this.addDefaultEntities;
  }
  
  private boolean getBooleanValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    return getBooleanValue(s, attribute, parser, null);
  }
  
  private boolean getBooleanValue(String s, String attribute, XmlPullParser parser, String defaultValue) throws XmlPullParserException {
    if (s != null && s.length() != 0)
      return Boolean.valueOf(s).booleanValue(); 
    if (defaultValue != null)
      return Boolean.valueOf(defaultValue).booleanValue(); 
    return false;
  }
  
  private byte getByteValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Byte.valueOf(s).byteValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, nfe); 
      }  
    return 0;
  }
  
  private char getCharacterValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    if (s != null)
      return s.charAt(0); 
    return Character.MIN_VALUE;
  }
  
  private Date getDateValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    return getDateValue(s, attribute, null, parser);
  }
  
  private Date getDateValue(String s, String attribute, String dateFormat, XmlPullParser parser) throws XmlPullParserException {
    if (s != null) {
      String effectiveDateFormat = dateFormat;
      if (dateFormat == null)
        effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"; 
      if ("long".equals(effectiveDateFormat))
        try {
          return new Date(Long.parseLong(s));
        } catch (NumberFormatException e) {
          throw new XmlPullParserException(e.getMessage(), parser, e);
        }  
      try {
        DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
        return dateParser.parse(s);
      } catch (ParseException e) {
        throw new XmlPullParserException(e.getMessage(), parser, e);
      } 
    } 
    return null;
  }
  
  private double getDoubleValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Double.valueOf(s).doubleValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe); 
      }  
    return 0.0D;
  }
  
  private float getFloatValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Float.valueOf(s).floatValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe); 
      }  
    return 0.0F;
  }
  
  private int getIntegerValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Integer.valueOf(s).intValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, nfe); 
      }  
    return 0;
  }
  
  private long getLongValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Long.valueOf(s).longValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, nfe); 
      }  
    return 0L;
  }
  
  private String getRequiredAttributeValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s == null)
      if (strict)
        throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, null);  
    return s;
  }
  
  private short getShortValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Short.valueOf(s).shortValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, nfe); 
      }  
    return 0;
  }
  
  private String getTrimmedValue(String s) {
    if (s != null)
      s = s.trim(); 
    return s;
  }
  
  private String interpolatedTrimmed(String value, String context) {
    return getTrimmedValue(this.contentTransformer.transform(value, context));
  }
  
  private int nextTag(XmlPullParser parser) throws IOException, XmlPullParserException {
    int eventType = parser.next();
    if (eventType == 4)
      eventType = parser.next(); 
    if (eventType != 2 && eventType != 3)
      throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null); 
    return eventType;
  }
  
  public FileSet read(Reader reader, boolean strict) throws IOException, XmlPullParserException {
    MXParser mXParser = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
    mXParser.setInput(reader);
    return read((XmlPullParser)mXParser, strict);
  }
  
  public FileSet read(Reader reader) throws IOException, XmlPullParserException {
    return read(reader, true);
  }
  
  public FileSet read(InputStream in, boolean strict) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in), strict);
  }
  
  public FileSet read(InputStream in) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in));
  }
  
  private FileSet parseFileSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    FileSet fileSet = new FileSet();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        if (!"xmlns".equals(name))
          checkUnknownAttribute(parser, name, tagName, strict);  
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
        fileSet.setDirectory(interpolatedTrimmed(parser.nextText(), "directory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "lineEnding", null, parsed)) {
        fileSet.setLineEnding(interpolatedTrimmed(parser.nextText(), "lineEnding"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "followSymlinks", null, parsed)) {
        fileSet.setFollowSymlinks(getBooleanValue(interpolatedTrimmed(parser.nextText(), "followSymlinks"), "followSymlinks", parser, "false"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
        fileSet.setOutputDirectory(interpolatedTrimmed(parser.nextText(), "outputDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "useDefaultExcludes", null, parsed)) {
        fileSet.setUseDefaultExcludes(getBooleanValue(interpolatedTrimmed(parser.nextText(), "useDefaultExcludes"), "useDefaultExcludes", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
        List<String> includes = new ArrayList<>();
        fileSet.setIncludes(includes);
        while (parser.nextTag() == 2) {
          if ("include".equals(parser.getName())) {
            includes.add(interpolatedTrimmed(parser.nextText(), "includes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
        List<String> excludes = new ArrayList<>();
        fileSet.setExcludes(excludes);
        while (parser.nextTag() == 2) {
          if ("exclude".equals(parser.getName())) {
            excludes.add(interpolatedTrimmed(parser.nextText(), "excludes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "fileMode", null, parsed)) {
        fileSet.setFileMode(interpolatedTrimmed(parser.nextText(), "fileMode"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "directoryMode", null, parsed)) {
        fileSet.setDirectoryMode(interpolatedTrimmed(parser.nextText(), "directoryMode"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "mapper", null, parsed)) {
        fileSet.setMapper(parseMapper(parser, strict));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return fileSet;
  }
  
  private Mapper parseMapper(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Mapper mapper = new Mapper();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
        mapper.setType(interpolatedTrimmed(parser.nextText(), "type"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "from", null, parsed)) {
        mapper.setFrom(interpolatedTrimmed(parser.nextText(), "from"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "to", null, parsed)) {
        mapper.setTo(interpolatedTrimmed(parser.nextText(), "to"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "classname", null, parsed)) {
        mapper.setClassname(interpolatedTrimmed(parser.nextText(), "classname"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return mapper;
  }
  
  private SetBase parseSetBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    SetBase setBase = new SetBase();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "followSymlinks", null, parsed)) {
        setBase.setFollowSymlinks(getBooleanValue(interpolatedTrimmed(parser.nextText(), "followSymlinks"), "followSymlinks", parser, "false"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
        setBase.setOutputDirectory(interpolatedTrimmed(parser.nextText(), "outputDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "useDefaultExcludes", null, parsed)) {
        setBase.setUseDefaultExcludes(getBooleanValue(interpolatedTrimmed(parser.nextText(), "useDefaultExcludes"), "useDefaultExcludes", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
        List<String> includes = new ArrayList<>();
        setBase.setIncludes(includes);
        while (parser.nextTag() == 2) {
          if ("include".equals(parser.getName())) {
            includes.add(interpolatedTrimmed(parser.nextText(), "includes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
        List<String> excludes = new ArrayList<>();
        setBase.setExcludes(excludes);
        while (parser.nextTag() == 2) {
          if ("exclude".equals(parser.getName())) {
            excludes.add(interpolatedTrimmed(parser.nextText(), "excludes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "fileMode", null, parsed)) {
        setBase.setFileMode(interpolatedTrimmed(parser.nextText(), "fileMode"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "directoryMode", null, parsed)) {
        setBase.setDirectoryMode(interpolatedTrimmed(parser.nextText(), "directoryMode"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "mapper", null, parsed)) {
        setBase.setMapper(parseMapper(parser, strict));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return setBase;
  }
  
  private FileSet read(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    FileSet fileSet = null;
    int eventType = parser.getEventType();
    boolean parsed = false;
    while (eventType != 1) {
      if (eventType == 2) {
        if (strict && !"fileSet".equals(parser.getName()))
          throw new XmlPullParserException("Expected root element 'fileSet' but found '" + parser.getName() + "'", parser, null); 
        if (parsed)
          throw new XmlPullParserException("Duplicated tag: 'fileSet'", parser, null); 
        fileSet = parseFileSet(parser, strict);
        fileSet.setModelEncoding(parser.getInputEncoding());
        parsed = true;
      } 
      eventType = parser.next();
    } 
    if (parsed)
      return fileSet; 
    throw new XmlPullParserException("Expected root element 'fileSet' but found no element at all: invalid XML document", parser, null);
  }
  
  public void setAddDefaultEntities(boolean addDefaultEntities) {
    this.addDefaultEntities = addDefaultEntities;
  }
  
  public static interface ContentTransformer {
    String transform(String param1String1, String param1String2);
  }
}
