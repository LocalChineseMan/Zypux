package org.apache.maven.shared.model.fileset.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.Mapper;
import org.apache.maven.shared.model.fileset.SetBase;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class FileSetXpp3Writer {
  private static final String NAMESPACE = null;
  
  private String fileComment = null;
  
  public void setFileComment(String fileComment) {
    this.fileComment = fileComment;
  }
  
  public void write(Writer writer, FileSet fileSet) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(writer);
    mXSerializer.startDocument(fileSet.getModelEncoding(), null);
    writeFileSet(fileSet, "fileSet", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  public void write(OutputStream stream, FileSet fileSet) throws IOException {
    MXSerializer mXSerializer = new MXSerializer();
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
    mXSerializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
    mXSerializer.setOutput(stream, fileSet.getModelEncoding());
    mXSerializer.startDocument(fileSet.getModelEncoding(), null);
    writeFileSet(fileSet, "fileSet", (XmlSerializer)mXSerializer);
    mXSerializer.endDocument();
  }
  
  private void writeFileSet(FileSet fileSet, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (fileSet.getDirectory() != null)
      serializer.startTag(NAMESPACE, "directory").text(fileSet.getDirectory()).endTag(NAMESPACE, "directory"); 
    if (fileSet.getLineEnding() != null)
      serializer.startTag(NAMESPACE, "lineEnding").text(fileSet.getLineEnding()).endTag(NAMESPACE, "lineEnding"); 
    if (fileSet.isFollowSymlinks())
      serializer.startTag(NAMESPACE, "followSymlinks").text(String.valueOf(fileSet.isFollowSymlinks())).endTag(NAMESPACE, "followSymlinks"); 
    if (fileSet.getOutputDirectory() != null)
      serializer.startTag(NAMESPACE, "outputDirectory").text(fileSet.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory"); 
    if (fileSet.isUseDefaultExcludes() != true)
      serializer.startTag(NAMESPACE, "useDefaultExcludes").text(String.valueOf(fileSet.isUseDefaultExcludes())).endTag(NAMESPACE, "useDefaultExcludes"); 
    if (fileSet.getIncludes() != null && fileSet.getIncludes().size() > 0) {
      serializer.startTag(NAMESPACE, "includes");
      for (Iterator<String> iter = fileSet.getIncludes().iterator(); iter.hasNext(); ) {
        String include = iter.next();
        serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
      } 
      serializer.endTag(NAMESPACE, "includes");
    } 
    if (fileSet.getExcludes() != null && fileSet.getExcludes().size() > 0) {
      serializer.startTag(NAMESPACE, "excludes");
      for (Iterator<String> iter = fileSet.getExcludes().iterator(); iter.hasNext(); ) {
        String exclude = iter.next();
        serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
      } 
      serializer.endTag(NAMESPACE, "excludes");
    } 
    if (fileSet.getFileMode() != null && !fileSet.getFileMode().equals("0644"))
      serializer.startTag(NAMESPACE, "fileMode").text(fileSet.getFileMode()).endTag(NAMESPACE, "fileMode"); 
    if (fileSet.getDirectoryMode() != null && !fileSet.getDirectoryMode().equals("0755"))
      serializer.startTag(NAMESPACE, "directoryMode").text(fileSet.getDirectoryMode()).endTag(NAMESPACE, "directoryMode"); 
    if (fileSet.getMapper() != null)
      writeMapper(fileSet.getMapper(), "mapper", serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeMapper(Mapper mapper, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (mapper.getType() != null && !mapper.getType().equals("identity"))
      serializer.startTag(NAMESPACE, "type").text(mapper.getType()).endTag(NAMESPACE, "type"); 
    if (mapper.getFrom() != null)
      serializer.startTag(NAMESPACE, "from").text(mapper.getFrom()).endTag(NAMESPACE, "from"); 
    if (mapper.getTo() != null)
      serializer.startTag(NAMESPACE, "to").text(mapper.getTo()).endTag(NAMESPACE, "to"); 
    if (mapper.getClassname() != null)
      serializer.startTag(NAMESPACE, "classname").text(mapper.getClassname()).endTag(NAMESPACE, "classname"); 
    serializer.endTag(NAMESPACE, tagName);
  }
  
  private void writeSetBase(SetBase setBase, String tagName, XmlSerializer serializer) throws IOException {
    serializer.startTag(NAMESPACE, tagName);
    if (setBase.isFollowSymlinks())
      serializer.startTag(NAMESPACE, "followSymlinks").text(String.valueOf(setBase.isFollowSymlinks())).endTag(NAMESPACE, "followSymlinks"); 
    if (setBase.getOutputDirectory() != null)
      serializer.startTag(NAMESPACE, "outputDirectory").text(setBase.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory"); 
    if (setBase.isUseDefaultExcludes() != true)
      serializer.startTag(NAMESPACE, "useDefaultExcludes").text(String.valueOf(setBase.isUseDefaultExcludes())).endTag(NAMESPACE, "useDefaultExcludes"); 
    if (setBase.getIncludes() != null && setBase.getIncludes().size() > 0) {
      serializer.startTag(NAMESPACE, "includes");
      for (Iterator<String> iter = setBase.getIncludes().iterator(); iter.hasNext(); ) {
        String include = iter.next();
        serializer.startTag(NAMESPACE, "include").text(include).endTag(NAMESPACE, "include");
      } 
      serializer.endTag(NAMESPACE, "includes");
    } 
    if (setBase.getExcludes() != null && setBase.getExcludes().size() > 0) {
      serializer.startTag(NAMESPACE, "excludes");
      for (Iterator<String> iter = setBase.getExcludes().iterator(); iter.hasNext(); ) {
        String exclude = iter.next();
        serializer.startTag(NAMESPACE, "exclude").text(exclude).endTag(NAMESPACE, "exclude");
      } 
      serializer.endTag(NAMESPACE, "excludes");
    } 
    if (setBase.getFileMode() != null && !setBase.getFileMode().equals("0644"))
      serializer.startTag(NAMESPACE, "fileMode").text(setBase.getFileMode()).endTag(NAMESPACE, "fileMode"); 
    if (setBase.getDirectoryMode() != null && !setBase.getDirectoryMode().equals("0755"))
      serializer.startTag(NAMESPACE, "directoryMode").text(setBase.getDirectoryMode()).endTag(NAMESPACE, "directoryMode"); 
    if (setBase.getMapper() != null)
      writeMapper(setBase.getMapper(), "mapper", serializer); 
    serializer.endTag(NAMESPACE, tagName);
  }
}
