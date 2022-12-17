package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class XmlUtil {
  public static final int DEFAULT_INDENTATION_SIZE = 2;
  
  public static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
  
  public static boolean isXml(File f) {
    if (f == null)
      throw new IllegalArgumentException("f could not be null."); 
    if (!f.isFile())
      throw new IllegalArgumentException("The file '" + f.getAbsolutePath() + "' is not a file."); 
    try {
      Reader reader = ReaderFactory.newXmlReader(f);
      try {
        MXParser mXParser = new MXParser();
        mXParser.setInput(reader);
        mXParser.nextToken();
        boolean bool = true;
        if (reader != null)
          reader.close(); 
        return bool;
      } catch (Throwable throwable) {
        if (reader != null)
          try {
            reader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (Exception e) {
      return false;
    } 
  }
  
  public static void prettyFormat(Reader reader, Writer writer) throws IOException {
    prettyFormat(reader, writer, 2, DEFAULT_LINE_SEPARATOR);
  }
  
  public static void prettyFormat(Reader reader, Writer writer, int indentSize, String lineSeparator) throws IOException {
    if (reader == null)
      throw new IllegalArgumentException("The reader is null"); 
    if (writer == null)
      throw new IllegalArgumentException("The writer is null"); 
    if (indentSize < 0)
      indentSize = 0; 
    PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
    xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
    xmlWriter.setLineSeparator(lineSeparator);
    MXParser mXParser = new MXParser();
    try {
      mXParser.setInput(reader);
      prettyFormatInternal((XmlPullParser)mXParser, xmlWriter);
    } catch (XmlPullParserException e) {
      throw new IOException("Unable to parse the XML: " + e.getMessage());
    } 
  }
  
  public static void prettyFormat(InputStream is, OutputStream os) throws IOException {
    prettyFormat(is, os, 2, DEFAULT_LINE_SEPARATOR);
  }
  
  public static void prettyFormat(InputStream is, OutputStream os, int indentSize, String lineSeparator) throws IOException {
    if (is == null)
      throw new IllegalArgumentException("The is is null"); 
    if (os == null)
      throw new IllegalArgumentException("The os is null"); 
    if (indentSize < 0)
      indentSize = 0; 
    try {
      Reader reader = ReaderFactory.newXmlReader(is);
      try {
        Writer writer = new OutputStreamWriter(os);
        try {
          PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
          xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
          xmlWriter.setLineSeparator(lineSeparator);
          MXParser mXParser = new MXParser();
          mXParser.setInput(reader);
          prettyFormatInternal((XmlPullParser)mXParser, xmlWriter);
          writer.close();
        } catch (Throwable throwable) {
          try {
            writer.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        if (reader != null)
          reader.close(); 
      } catch (Throwable throwable) {
        if (reader != null)
          try {
            reader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (XmlPullParserException e) {
      throw new IOException("Unable to parse the XML: " + e.getMessage());
    } 
  }
  
  private static void prettyFormatInternal(XmlPullParser parser, PrettyPrintXMLWriter writer) throws XmlPullParserException, IOException {
    boolean hasTag = false;
    boolean hasComment = false;
    int eventType = parser.getEventType();
    while (eventType != 1) {
      if (eventType == 2) {
        hasTag = true;
        if (hasComment) {
          writer.writeText(writer.getLineIndenter());
          hasComment = false;
        } 
        writer.startElement(parser.getName());
        for (int i = 0; i < parser.getAttributeCount(); i++) {
          String key = parser.getAttributeName(i);
          String value = parser.getAttributeValue(i);
          writer.addAttribute(key, value);
        } 
      } else if (eventType == 4) {
        String text = parser.getText();
        if (!text.trim().equals("")) {
          text = StringUtils.removeDuplicateWhitespace(text);
          writer.writeText(text);
        } 
      } else if (eventType == 3) {
        hasTag = false;
        writer.endElement();
      } else if (eventType == 9) {
        hasComment = true;
        if (!hasTag) {
          writer.writeMarkup(writer.getLineSeparator());
          for (int i = 0; i < writer.getDepth(); i++)
            writer.writeMarkup(writer.getLineIndenter()); 
        } 
        writer.writeMarkup("<!--" + parser.getText().trim() + " -->");
        if (!hasTag) {
          writer.writeMarkup(writer.getLineSeparator());
          for (int i = 0; i < writer.getDepth() - 1; i++)
            writer.writeMarkup(writer.getLineIndenter()); 
        } 
      } else if (eventType == 10) {
        writer.writeMarkup("<!DOCTYPE" + parser.getText() + ">");
        writer.endOfLine();
      } else if (eventType == 8) {
        writer.writeMarkup("<?" + parser.getText() + "?>");
        writer.endOfLine();
      } else if (eventType == 5) {
        writer.writeMarkup("<![CDATA[" + parser.getText() + "]]>");
      } else if (eventType == 6) {
        writer.writeMarkup("&" + parser.getName() + ";");
      } 
      eventType = parser.nextToken();
    } 
  }
}
