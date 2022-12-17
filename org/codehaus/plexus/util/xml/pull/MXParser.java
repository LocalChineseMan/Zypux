package org.codehaus.plexus.util.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlReader;
import org.codehaus.plexus.util.xml.XmlStreamReader;

public class MXParser implements XmlPullParser {
  private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  
  private static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
  
  private static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
  
  private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
  
  private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
  
  private static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
  
  private static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
  
  private boolean allStringsInterned;
  
  private static final boolean TRACE_SIZING = false;
  
  private boolean processNamespaces;
  
  private boolean roundtripSupported;
  
  private String location;
  
  private int lineNumber;
  
  private int columnNumber;
  
  private boolean seenRoot;
  
  private boolean reachedEnd;
  
  private int eventType;
  
  private boolean emptyElementTag;
  
  private int depth;
  
  private char[][] elRawName;
  
  private int[] elRawNameEnd;
  
  private int[] elRawNameLine;
  
  private String[] elName;
  
  private String[] elPrefix;
  
  private String[] elUri;
  
  private int[] elNamespaceCount;
  
  private void resetStringCache() {}
  
  private String newString(char[] cbuf, int off, int len) {
    return new String(cbuf, off, len);
  }
  
  private String newStringIntern(char[] cbuf, int off, int len) {
    return (new String(cbuf, off, len)).intern();
  }
  
  private String fileEncoding = null;
  
  private int attributeCount;
  
  private String[] attributeName;
  
  private int[] attributeNameHash;
  
  private String[] attributePrefix;
  
  private String[] attributeUri;
  
  private String[] attributeValue;
  
  private int namespaceEnd;
  
  private String[] namespacePrefix;
  
  private int[] namespacePrefixHash;
  
  private String[] namespaceUri;
  
  private int entityEnd;
  
  private String[] entityName;
  
  private char[][] entityNameBuf;
  
  private String[] entityReplacement;
  
  private char[][] entityReplacementBuf;
  
  private int[] entityNameHash;
  
  private final EntityReplacementMap replacementMapTemplate;
  
  private static final int READ_CHUNK_SIZE = 8192;
  
  private Reader reader;
  
  private String inputEncoding;
  
  private void ensureElementsCapacity() {
    int elStackSize = (this.elName != null) ? this.elName.length : 0;
    if (this.depth + 1 >= elStackSize) {
      int newSize = ((this.depth >= 7) ? (2 * this.depth) : 8) + 2;
      boolean needsCopying = (elStackSize > 0);
      String[] arr = null;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.elName, 0, arr, 0, elStackSize); 
      this.elName = arr;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.elPrefix, 0, arr, 0, elStackSize); 
      this.elPrefix = arr;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.elUri, 0, arr, 0, elStackSize); 
      this.elUri = arr;
      int[] iarr = new int[newSize];
      if (needsCopying) {
        System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
      } else {
        iarr[0] = 0;
      } 
      this.elNamespaceCount = iarr;
      iarr = new int[newSize];
      if (needsCopying)
        System.arraycopy(this.elRawNameEnd, 0, iarr, 0, elStackSize); 
      this.elRawNameEnd = iarr;
      iarr = new int[newSize];
      if (needsCopying)
        System.arraycopy(this.elRawNameLine, 0, iarr, 0, elStackSize); 
      this.elRawNameLine = iarr;
      char[][] carr = new char[newSize][];
      if (needsCopying)
        System.arraycopy(this.elRawName, 0, carr, 0, elStackSize); 
      this.elRawName = carr;
    } 
  }
  
  private void ensureAttributesCapacity(int size) {
    int attrPosSize = (this.attributeName != null) ? this.attributeName.length : 0;
    if (size >= attrPosSize) {
      int newSize = (size > 7) ? (2 * size) : 8;
      boolean needsCopying = (attrPosSize > 0);
      String[] arr = null;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.attributeName, 0, arr, 0, attrPosSize); 
      this.attributeName = arr;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.attributePrefix, 0, arr, 0, attrPosSize); 
      this.attributePrefix = arr;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.attributeUri, 0, arr, 0, attrPosSize); 
      this.attributeUri = arr;
      arr = new String[newSize];
      if (needsCopying)
        System.arraycopy(this.attributeValue, 0, arr, 0, attrPosSize); 
      this.attributeValue = arr;
      if (!this.allStringsInterned) {
        int[] iarr = new int[newSize];
        if (needsCopying)
          System.arraycopy(this.attributeNameHash, 0, iarr, 0, attrPosSize); 
        this.attributeNameHash = iarr;
      } 
      arr = null;
    } 
  }
  
  private void ensureNamespacesCapacity(int size) {
    int namespaceSize = (this.namespacePrefix != null) ? this.namespacePrefix.length : 0;
    if (size >= namespaceSize) {
      int newSize = (size > 7) ? (2 * size) : 8;
      String[] newNamespacePrefix = new String[newSize];
      String[] newNamespaceUri = new String[newSize];
      if (this.namespacePrefix != null) {
        System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
        System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
      } 
      this.namespacePrefix = newNamespacePrefix;
      this.namespaceUri = newNamespaceUri;
      if (!this.allStringsInterned) {
        int[] newNamespacePrefixHash = new int[newSize];
        if (this.namespacePrefixHash != null)
          System.arraycopy(this.namespacePrefixHash, 0, newNamespacePrefixHash, 0, this.namespaceEnd); 
        this.namespacePrefixHash = newNamespacePrefixHash;
      } 
    } 
  }
  
  private static final int fastHash(char[] ch, int off, int len) {
    if (len == 0)
      return 0; 
    int hash = ch[off];
    hash = (hash << 7) + ch[off + len - 1];
    if (len > 16)
      hash = (hash << 7) + ch[off + len / 4]; 
    if (len > 8)
      hash = (hash << 7) + ch[off + len / 2]; 
    return hash;
  }
  
  private void ensureEntityCapacity() {
    int entitySize = (this.entityReplacementBuf != null) ? this.entityReplacementBuf.length : 0;
    if (this.entityEnd >= entitySize) {
      int newSize = (this.entityEnd > 7) ? (2 * this.entityEnd) : 8;
      String[] newEntityName = new String[newSize];
      char[][] newEntityNameBuf = new char[newSize][];
      String[] newEntityReplacement = new String[newSize];
      char[][] newEntityReplacementBuf = new char[newSize][];
      if (this.entityName != null) {
        System.arraycopy(this.entityName, 0, newEntityName, 0, this.entityEnd);
        System.arraycopy(this.entityNameBuf, 0, newEntityNameBuf, 0, this.entityEnd);
        System.arraycopy(this.entityReplacement, 0, newEntityReplacement, 0, this.entityEnd);
        System.arraycopy(this.entityReplacementBuf, 0, newEntityReplacementBuf, 0, this.entityEnd);
      } 
      this.entityName = newEntityName;
      this.entityNameBuf = newEntityNameBuf;
      this.entityReplacement = newEntityReplacement;
      this.entityReplacementBuf = newEntityReplacementBuf;
      if (!this.allStringsInterned) {
        int[] newEntityNameHash = new int[newSize];
        if (this.entityNameHash != null)
          System.arraycopy(this.entityNameHash, 0, newEntityNameHash, 0, this.entityEnd); 
        this.entityNameHash = newEntityNameHash;
      } 
    } 
  }
  
  private int bufLoadFactor = 95;
  
  private float bufferLoadFactor = this.bufLoadFactor / 100.0F;
  
  private char[] buf = new char[(Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 256];
  
  private int bufSoftLimit = (int)(this.bufferLoadFactor * this.buf.length);
  
  private boolean preventBufferCompaction;
  
  private int bufAbsoluteStart;
  
  private int bufStart;
  
  private int bufEnd;
  
  private int pos;
  
  private int posStart;
  
  private int posEnd;
  
  private char[] pc = new char[(Runtime.getRuntime().freeMemory() > 1000000L) ? 8192 : 64];
  
  private int pcStart;
  
  private int pcEnd;
  
  private boolean usePC;
  
  private boolean seenStartTag;
  
  private boolean seenEndTag;
  
  private boolean pastEndTag;
  
  private boolean seenAmpersand;
  
  private boolean seenMarkup;
  
  private boolean seenDocdecl;
  
  private boolean tokenize;
  
  private String text;
  
  private String entityRefName;
  
  private String xmlDeclVersion;
  
  private Boolean xmlDeclStandalone;
  
  private String xmlDeclContent;
  
  private void reset() {
    this.location = null;
    this.lineNumber = 1;
    this.columnNumber = 1;
    this.seenRoot = false;
    this.reachedEnd = false;
    this.eventType = 0;
    this.emptyElementTag = false;
    this.depth = 0;
    this.attributeCount = 0;
    this.namespaceEnd = 0;
    this.entityEnd = 0;
    setupFromTemplate();
    this.reader = null;
    this.inputEncoding = null;
    this.preventBufferCompaction = false;
    this.bufAbsoluteStart = 0;
    this.bufEnd = this.bufStart = 0;
    this.pos = this.posStart = this.posEnd = 0;
    this.pcEnd = this.pcStart = 0;
    this.usePC = false;
    this.seenStartTag = false;
    this.seenEndTag = false;
    this.pastEndTag = false;
    this.seenAmpersand = false;
    this.seenMarkup = false;
    this.seenDocdecl = false;
    this.xmlDeclVersion = null;
    this.xmlDeclStandalone = null;
    this.xmlDeclContent = null;
    resetStringCache();
  }
  
  public void setupFromTemplate() {
    if (this.replacementMapTemplate != null) {
      int length = this.replacementMapTemplate.entityEnd;
      this.entityName = this.replacementMapTemplate.entityName;
      this.entityNameBuf = this.replacementMapTemplate.entityNameBuf;
      this.entityReplacement = this.replacementMapTemplate.entityReplacement;
      this.entityReplacementBuf = this.replacementMapTemplate.entityReplacementBuf;
      this.entityNameHash = this.replacementMapTemplate.entityNameHash;
      this.entityEnd = length;
    } 
  }
  
  public void setFeature(String name, boolean state) throws XmlPullParserException {
    if (name == null)
      throw new IllegalArgumentException("feature name should not be null"); 
    if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
      if (this.eventType != 0)
        throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, null); 
      this.processNamespaces = state;
    } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
      if (state)
        throw new XmlPullParserException("interning names in this implementation is not supported"); 
    } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
      if (state)
        throw new XmlPullParserException("processing DOCDECL is not supported"); 
    } else if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name)) {
      this.roundtripSupported = state;
    } else {
      throw new XmlPullParserException("unsupported feature " + name);
    } 
  }
  
  public boolean getFeature(String name) {
    if (name == null)
      throw new IllegalArgumentException("feature name should not be null"); 
    if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name))
      return this.processNamespaces; 
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name))
      return false; 
    if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name))
      return false; 
    if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name))
      return this.roundtripSupported; 
    return false;
  }
  
  public void setProperty(String name, Object value) throws XmlPullParserException {
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      this.location = (String)value;
    } else {
      throw new XmlPullParserException("unsupported property: '" + name + "'");
    } 
  }
  
  public Object getProperty(String name) {
    if (name == null)
      throw new IllegalArgumentException("property name should not be null"); 
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(name))
      return this.xmlDeclVersion; 
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(name))
      return this.xmlDeclStandalone; 
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(name))
      return this.xmlDeclContent; 
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name))
      return this.location; 
    return null;
  }
  
  public void setInput(Reader in) throws XmlPullParserException {
    reset();
    this.reader = in;
    if (this.reader instanceof XmlReader) {
      XmlReader xsr = (XmlReader)this.reader;
      this.fileEncoding = xsr.getEncoding();
    } else if (this.reader instanceof InputStreamReader) {
      InputStreamReader isr = (InputStreamReader)this.reader;
      if (isr.getEncoding() != null)
        this.fileEncoding = isr.getEncoding().toUpperCase(); 
    } 
  }
  
  public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
    XmlStreamReader xmlStreamReader;
    if (inputStream == null)
      throw new IllegalArgumentException("input stream can not be null"); 
    try {
      if (inputEncoding != null) {
        Reader reader = ReaderFactory.newReader(inputStream, inputEncoding);
      } else {
        xmlStreamReader = ReaderFactory.newXmlReader(inputStream);
      } 
    } catch (UnsupportedEncodingException une) {
      throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + une, this, une);
    } catch (IOException e) {
      throw new XmlPullParserException("could not create reader : " + e, this, e);
    } 
    setInput((Reader)xmlStreamReader);
    this.inputEncoding = inputEncoding;
  }
  
  public String getInputEncoding() {
    return this.inputEncoding;
  }
  
  public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
    if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
      String tmp = replacementText.substring(1, replacementText.length() - 1);
      for (int i = 0; i < this.entityName.length; i++) {
        if (this.entityName[i] != null && this.entityName[i].equals(tmp))
          replacementText = this.entityReplacement[i]; 
      } 
    } 
    ensureEntityCapacity();
    char[] entityNameCharData = entityName.toCharArray();
    this.entityName[this.entityEnd] = newString(entityNameCharData, 0, entityName.length());
    this.entityNameBuf[this.entityEnd] = entityNameCharData;
    this.entityReplacement[this.entityEnd] = replacementText;
    this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
    if (!this.allStringsInterned)
      this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, (this.entityNameBuf[this.entityEnd]).length); 
    this.entityEnd++;
  }
  
  public int getNamespaceCount(int depth) throws XmlPullParserException {
    if (!this.processNamespaces || depth == 0)
      return 0; 
    if (depth < 0 || depth > this.depth)
      throw new IllegalArgumentException("namespace count may be for depth 0.." + this.depth + " not " + depth); 
    return this.elNamespaceCount[depth];
  }
  
  public String getNamespacePrefix(int pos) throws XmlPullParserException {
    if (pos < this.namespaceEnd)
      return this.namespacePrefix[pos]; 
    throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
  }
  
  public String getNamespaceUri(int pos) throws XmlPullParserException {
    if (pos < this.namespaceEnd)
      return this.namespaceUri[pos]; 
    throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
  }
  
  public String getNamespace(String prefix) {
    if (prefix != null) {
      for (int i = this.namespaceEnd - 1; i >= 0; i--) {
        if (prefix.equals(this.namespacePrefix[i]))
          return this.namespaceUri[i]; 
      } 
      if ("xml".equals(prefix))
        return "http://www.w3.org/XML/1998/namespace"; 
      if ("xmlns".equals(prefix))
        return "http://www.w3.org/2000/xmlns/"; 
    } else {
      for (int i = this.namespaceEnd - 1; i >= 0; i--) {
        if (this.namespacePrefix[i] == null)
          return this.namespaceUri[i]; 
      } 
    } 
    return null;
  }
  
  public int getDepth() {
    return this.depth;
  }
  
  private static int findFragment(int bufMinPos, char[] b, int start, int end) {
    if (start < bufMinPos) {
      start = bufMinPos;
      if (start > end)
        start = end; 
      return start;
    } 
    if (end - start > 65)
      start = end - 10; 
    int i = start + 1;
    while (--i > bufMinPos) {
      if (end - i > 65)
        break; 
      char c = b[i];
      if (c == '<' && start - i > 10)
        break; 
    } 
    return i;
  }
  
  public String getPositionDescription() {
    String fragment = null;
    if (this.posStart <= this.pos) {
      int start = findFragment(0, this.buf, this.posStart, this.pos);
      if (start < this.pos)
        fragment = new String(this.buf, start, this.pos - start); 
      if (this.bufAbsoluteStart > 0 || start > 0)
        fragment = "..." + fragment; 
    } 
    return " " + TYPES[this.eventType] + ((fragment != null) ? (" seen " + printable(fragment) + "...") : "") + " " + (
      (this.location != null) ? this.location : "") + "@" + getLineNumber() + ":" + getColumnNumber();
  }
  
  public int getLineNumber() {
    return this.lineNumber;
  }
  
  public int getColumnNumber() {
    return this.columnNumber;
  }
  
  public boolean isWhitespace() throws XmlPullParserException {
    if (this.eventType == 4 || this.eventType == 5) {
      if (this.usePC) {
        for (int j = this.pcStart; j < this.pcEnd; j++) {
          if (!isS(this.pc[j]))
            return false; 
        } 
        return true;
      } 
      for (int i = this.posStart; i < this.posEnd; i++) {
        if (!isS(this.buf[i]))
          return false; 
      } 
      return true;
    } 
    if (this.eventType == 7)
      return true; 
    throw new XmlPullParserException("no content available to check for whitespaces");
  }
  
  public String getText() {
    if (this.eventType == 0 || this.eventType == 1)
      return null; 
    if (this.eventType == 6)
      return this.text; 
    if (this.text == null)
      if (!this.usePC || this.eventType == 2 || this.eventType == 3) {
        this.text = new String(this.buf, this.posStart, this.posEnd - this.posStart);
      } else {
        this.text = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
      }  
    return this.text;
  }
  
  public char[] getTextCharacters(int[] holderForStartAndLength) {
    if (this.eventType == 4) {
      if (this.usePC) {
        holderForStartAndLength[0] = this.pcStart;
        holderForStartAndLength[1] = this.pcEnd - this.pcStart;
        return this.pc;
      } 
      holderForStartAndLength[0] = this.posStart;
      holderForStartAndLength[1] = this.posEnd - this.posStart;
      return this.buf;
    } 
    if (this.eventType == 2 || this.eventType == 3 || this.eventType == 5 || this.eventType == 9 || this.eventType == 6 || this.eventType == 8 || this.eventType == 7 || this.eventType == 10) {
      holderForStartAndLength[0] = this.posStart;
      holderForStartAndLength[1] = this.posEnd - this.posStart;
      return this.buf;
    } 
    if (this.eventType == 0 || this.eventType == 1) {
      holderForStartAndLength[1] = -1;
      holderForStartAndLength[0] = -1;
      return null;
    } 
    throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
  }
  
  public String getNamespace() {
    if (this.eventType == 2)
      return this.processNamespaces ? this.elUri[this.depth] : ""; 
    if (this.eventType == 3)
      return this.processNamespaces ? this.elUri[this.depth] : ""; 
    return null;
  }
  
  public String getName() {
    if (this.eventType == 2)
      return this.elName[this.depth]; 
    if (this.eventType == 3)
      return this.elName[this.depth]; 
    if (this.eventType == 6) {
      if (this.entityRefName == null)
        this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart); 
      return this.entityRefName;
    } 
    return null;
  }
  
  public String getPrefix() {
    if (this.eventType == 2)
      return this.elPrefix[this.depth]; 
    if (this.eventType == 3)
      return this.elPrefix[this.depth]; 
    return null;
  }
  
  public boolean isEmptyElementTag() throws XmlPullParserException {
    if (this.eventType != 2)
      throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, null); 
    return this.emptyElementTag;
  }
  
  public int getAttributeCount() {
    if (this.eventType != 2)
      return -1; 
    return this.attributeCount;
  }
  
  public String getAttributeNamespace(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (!this.processNamespaces)
      return ""; 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return this.attributeUri[index];
  }
  
  public String getAttributeName(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return this.attributeName[index];
  }
  
  public String getAttributePrefix(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (!this.processNamespaces)
      return null; 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return this.attributePrefix[index];
  }
  
  public String getAttributeType(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return "CDATA";
  }
  
  public boolean isAttributeDefault(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return false;
  }
  
  public String getAttributeValue(int index) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes"); 
    if (index < 0 || index >= this.attributeCount)
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index); 
    return this.attributeValue[index];
  }
  
  public String getAttributeValue(String namespace, String name) {
    if (this.eventType != 2)
      throw new IndexOutOfBoundsException("only START_TAG can have attributes" + getPositionDescription()); 
    if (name == null)
      throw new IllegalArgumentException("attribute name can not be null"); 
    if (this.processNamespaces) {
      if (namespace == null)
        namespace = ""; 
      for (int i = 0; i < this.attributeCount; i++) {
        if ((namespace == this.attributeUri[i] || namespace.equals(this.attributeUri[i])) && name
          
          .equals(this.attributeName[i]))
          return this.attributeValue[i]; 
      } 
    } else {
      if (namespace != null && namespace.length() == 0)
        namespace = null; 
      if (namespace != null)
        throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null"); 
      for (int i = 0; i < this.attributeCount; i++) {
        if (name.equals(this.attributeName[i]))
          return this.attributeValue[i]; 
      } 
    } 
    return null;
  }
  
  public int getEventType() throws XmlPullParserException {
    return this.eventType;
  }
  
  public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
    if (!this.processNamespaces && namespace != null)
      throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces declared on elements" + " (position:" + 
          getPositionDescription() + ")"); 
    if (type != getEventType() || (namespace != null && !namespace.equals(getNamespace())) || (name != null && 
      !name.equals(getName())))
      throw new XmlPullParserException("expected event " + TYPES[type] + (
          (name != null) ? (" with name '" + name + "'") : "") + ((
          namespace != null && name != null) ? " and" : "") + (
          (namespace != null) ? (" with namespace '" + namespace + "'") : "") + " but got" + (
          (type != getEventType()) ? (" " + TYPES[getEventType()]) : "") + ((
          name != null && getName() != null && !name.equals(getName())) ? (" name '" + getName() + "'") : "") + ((
          
          namespace != null && name != null && getName() != null && !name.equals(getName()) && getNamespace() != null && !namespace.equals(getNamespace())) ? " and" : "") + ((
          namespace != null && getNamespace() != null && !namespace.equals(getNamespace())) ? (
          " namespace '" + getNamespace() + "'") : 
          "") + " (position:" + 
          getPositionDescription() + ")"); 
  }
  
  public void skipSubTree() throws XmlPullParserException, IOException {
    require(2, null, null);
    int level = 1;
    while (level > 0) {
      int eventType = next();
      if (eventType == 3) {
        level--;
        continue;
      } 
      if (eventType == 2)
        level++; 
    } 
  }
  
  public String nextText() throws XmlPullParserException, IOException {
    if (getEventType() != 2)
      throw new XmlPullParserException("parser must be on START_TAG to read next text", this, null); 
    int eventType = next();
    if (eventType == 4) {
      String result = getText();
      eventType = next();
      if (eventType != 3)
        throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[
              getEventType()], this, null); 
      return result;
    } 
    if (eventType == 3)
      return ""; 
    throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, null);
  }
  
  public int nextTag() throws XmlPullParserException, IOException {
    next();
    if (this.eventType == 4 && isWhitespace())
      next(); 
    if (this.eventType != 2 && this.eventType != 3)
      throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[getEventType()], this, null); 
    return this.eventType;
  }
  
  public int next() throws XmlPullParserException, IOException {
    this.tokenize = false;
    return nextImpl();
  }
  
  public int nextToken() throws XmlPullParserException, IOException {
    this.tokenize = true;
    return nextImpl();
  }
  
  private int nextImpl() throws XmlPullParserException, IOException {
    this.text = null;
    this.pcEnd = this.pcStart = 0;
    this.usePC = false;
    this.bufStart = this.posEnd;
    if (this.pastEndTag) {
      this.pastEndTag = false;
      this.depth--;
      this.namespaceEnd = this.elNamespaceCount[this.depth];
    } 
    if (this.emptyElementTag) {
      this.emptyElementTag = false;
      this.pastEndTag = true;
      return this.eventType = 3;
    } 
    if (this.depth > 0) {
      char ch;
      if (this.seenStartTag) {
        this.seenStartTag = false;
        return this.eventType = parseStartTag();
      } 
      if (this.seenEndTag) {
        this.seenEndTag = false;
        return this.eventType = parseEndTag();
      } 
      if (this.seenMarkup) {
        this.seenMarkup = false;
        ch = '<';
      } else if (this.seenAmpersand) {
        this.seenAmpersand = false;
        ch = '&';
      } else {
        ch = more();
      } 
      this.posStart = this.pos - 1;
      boolean hadCharData = false;
      boolean needsMerging = false;
      while (true) {
        if (ch == '<') {
          if (hadCharData)
            if (this.tokenize) {
              this.seenMarkup = true;
              return this.eventType = 4;
            }  
          ch = more();
          if (ch == '/') {
            if (!this.tokenize && hadCharData) {
              this.seenEndTag = true;
              return this.eventType = 4;
            } 
            return this.eventType = parseEndTag();
          } 
          if (ch == '!') {
            ch = more();
            if (ch == '-') {
              parseComment();
              if (this.tokenize)
                return this.eventType = 9; 
              if (!this.usePC && hadCharData) {
                needsMerging = true;
              } else {
                this.posStart = this.pos;
              } 
            } else if (ch == '[') {
              parseCDSect(hadCharData);
              if (this.tokenize)
                return this.eventType = 5; 
              int cdStart = this.posStart;
              int cdEnd = this.posEnd;
              int cdLen = cdEnd - cdStart;
              if (cdLen > 0) {
                hadCharData = true;
                if (!this.usePC)
                  needsMerging = true; 
              } 
            } else {
              throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
            } 
          } else if (ch == '?') {
            parsePI();
            if (this.tokenize)
              return this.eventType = 8; 
            if (!this.usePC && hadCharData) {
              needsMerging = true;
            } else {
              this.posStart = this.pos;
            } 
          } else {
            if (isNameStartChar(ch)) {
              if (!this.tokenize && hadCharData) {
                this.seenStartTag = true;
                return this.eventType = 4;
              } 
              return this.eventType = parseStartTag();
            } 
            throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
          } 
        } else if (ch == '&') {
          if (this.tokenize && hadCharData) {
            this.seenAmpersand = true;
            return this.eventType = 4;
          } 
          int oldStart = this.posStart + this.bufAbsoluteStart;
          int oldEnd = this.posEnd + this.bufAbsoluteStart;
          parseEntityRef();
          if (this.tokenize)
            return this.eventType = 6; 
          if (this.resolvedEntityRefCharBuf == BUF_NOT_RESOLVED) {
            if (this.entityRefName == null)
              this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart); 
            throw new XmlPullParserException("could not resolve entity named '" + 
                printable(this.entityRefName) + "'", this, null);
          } 
          this.posStart = oldStart - this.bufAbsoluteStart;
          this.posEnd = oldEnd - this.bufAbsoluteStart;
          if (!this.usePC)
            if (hadCharData) {
              joinPC();
              needsMerging = false;
            } else {
              this.usePC = true;
              this.pcStart = this.pcEnd = 0;
            }  
          for (char aResolvedEntity : this.resolvedEntityRefCharBuf) {
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = aResolvedEntity;
          } 
          hadCharData = true;
        } else {
          if (needsMerging) {
            joinPC();
            needsMerging = false;
          } 
          hadCharData = true;
          boolean normalizedCR = false;
          boolean normalizeInput = (!this.tokenize || !this.roundtripSupported);
          boolean seenBracket = false;
          boolean seenBracketBracket = false;
          do {
            if (ch == ']') {
              if (seenBracket) {
                seenBracketBracket = true;
              } else {
                seenBracket = true;
              } 
            } else {
              if (seenBracketBracket && ch == '>')
                throw new XmlPullParserException("characters ]]> are not allowed in content", this, null); 
              if (seenBracket)
                seenBracketBracket = seenBracket = false; 
            } 
            if (normalizeInput)
              if (ch == '\r') {
                normalizedCR = true;
                this.posEnd = this.pos - 1;
                if (!this.usePC)
                  if (this.posEnd > this.posStart) {
                    joinPC();
                  } else {
                    this.usePC = true;
                    this.pcStart = this.pcEnd = 0;
                  }  
                if (this.pcEnd >= this.pc.length)
                  ensurePC(this.pcEnd); 
                this.pc[this.pcEnd++] = '\n';
              } else if (ch == '\n') {
                if (!normalizedCR && this.usePC) {
                  if (this.pcEnd >= this.pc.length)
                    ensurePC(this.pcEnd); 
                  this.pc[this.pcEnd++] = '\n';
                } 
                normalizedCR = false;
              } else {
                if (this.usePC) {
                  if (this.pcEnd >= this.pc.length)
                    ensurePC(this.pcEnd); 
                  this.pc[this.pcEnd++] = ch;
                } 
                normalizedCR = false;
              }  
            ch = more();
          } while (ch != '<' && ch != '&');
          this.posEnd = this.pos - 1;
          continue;
        } 
        ch = more();
      } 
    } 
    if (this.seenRoot)
      return parseEpilog(); 
    return parseProlog();
  }
  
  private int parseProlog() throws XmlPullParserException, IOException {
    char ch;
    if (this.seenMarkup) {
      ch = this.buf[this.pos - 1];
    } else {
      ch = more();
    } 
    if (this.eventType == 0) {
      if (ch == '￾')
        throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, null); 
      if (ch == '﻿') {
        ch = more();
      } else if (ch == '�') {
        ch = more();
        if (ch == '�')
          throw new XmlPullParserException("UTF-16 BOM in a UTF-8 encoded file is incompatible", this, null); 
      } 
    } 
    this.seenMarkup = false;
    boolean gotS = false;
    this.posStart = this.pos - 1;
    boolean normalizeIgnorableWS = (this.tokenize && !this.roundtripSupported);
    boolean normalizedCR = false;
    while (true) {
      if (ch == '<') {
        if (gotS && this.tokenize) {
          this.posEnd = this.pos - 1;
          this.seenMarkup = true;
          return this.eventType = 7;
        } 
        ch = more();
        if (ch == '?') {
          boolean isXMLDecl = parsePI();
          if (this.tokenize) {
            if (isXMLDecl)
              return this.eventType = 0; 
            return this.eventType = 8;
          } 
        } else if (ch == '!') {
          ch = more();
          if (ch == 'D') {
            if (this.seenDocdecl)
              throw new XmlPullParserException("only one docdecl allowed in XML document", this, null); 
            this.seenDocdecl = true;
            parseDocdecl();
            if (this.tokenize)
              return this.eventType = 10; 
          } else if (ch == '-') {
            parseComment();
            if (this.tokenize)
              return this.eventType = 9; 
          } else {
            throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
          } 
        } else {
          if (ch == '/')
            throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null); 
          if (isNameStartChar(ch)) {
            this.seenRoot = true;
            return parseStartTag();
          } 
          throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null);
        } 
      } else if (isS(ch)) {
        gotS = true;
        if (normalizeIgnorableWS)
          if (ch == '\r') {
            normalizedCR = true;
            if (!this.usePC) {
              this.posEnd = this.pos - 1;
              if (this.posEnd > this.posStart) {
                joinPC();
              } else {
                this.usePC = true;
                this.pcStart = this.pcEnd = 0;
              } 
            } 
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = '\n';
          } else if (ch == '\n') {
            if (!normalizedCR && this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = '\n';
            } 
            normalizedCR = false;
          } else {
            if (this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = ch;
            } 
            normalizedCR = false;
          }  
      } else {
        throw new XmlPullParserException("only whitespace content allowed before start tag and not " + 
            printable(ch), this, null);
      } 
      ch = more();
    } 
  }
  
  private int parseEpilog() throws XmlPullParserException, IOException {
    if (this.eventType == 1)
      throw new XmlPullParserException("already reached end of XML input", this, null); 
    if (this.reachedEnd)
      return this.eventType = 1; 
    boolean gotS = false;
    boolean normalizeIgnorableWS = (this.tokenize && !this.roundtripSupported);
    boolean normalizedCR = false;
    try {
      char ch;
      if (this.seenMarkup) {
        ch = this.buf[this.pos - 1];
      } else {
        ch = more();
      } 
      this.seenMarkup = false;
      this.posStart = this.pos - 1;
      if (!this.reachedEnd)
        do {
          if (ch == '<') {
            if (gotS && this.tokenize) {
              this.posEnd = this.pos - 1;
              this.seenMarkup = true;
              return this.eventType = 7;
            } 
            ch = more();
            if (this.reachedEnd)
              break; 
            if (ch == '?') {
              parsePI();
              if (this.tokenize)
                return this.eventType = 8; 
            } else if (ch == '!') {
              ch = more();
              if (this.reachedEnd)
                break; 
              if (ch == 'D') {
                parseDocdecl();
                if (this.tokenize)
                  return this.eventType = 10; 
              } else if (ch == '-') {
                parseComment();
                if (this.tokenize)
                  return this.eventType = 9; 
              } else {
                throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
              } 
            } else {
              if (ch == '/')
                throw new XmlPullParserException("end tag not allowed in epilog but got " + 
                    printable(ch), this, null); 
              if (isNameStartChar(ch))
                throw new XmlPullParserException("start tag not allowed in epilog but got " + 
                    printable(ch), this, null); 
              throw new XmlPullParserException("in epilog expected ignorable content and not " + 
                  printable(ch), this, null);
            } 
          } else if (isS(ch)) {
            gotS = true;
            if (normalizeIgnorableWS)
              if (ch == '\r') {
                normalizedCR = true;
                if (!this.usePC) {
                  this.posEnd = this.pos - 1;
                  if (this.posEnd > this.posStart) {
                    joinPC();
                  } else {
                    this.usePC = true;
                    this.pcStart = this.pcEnd = 0;
                  } 
                } 
                if (this.pcEnd >= this.pc.length)
                  ensurePC(this.pcEnd); 
                this.pc[this.pcEnd++] = '\n';
              } else if (ch == '\n') {
                if (!normalizedCR && this.usePC) {
                  if (this.pcEnd >= this.pc.length)
                    ensurePC(this.pcEnd); 
                  this.pc[this.pcEnd++] = '\n';
                } 
                normalizedCR = false;
              } else {
                if (this.usePC) {
                  if (this.pcEnd >= this.pc.length)
                    ensurePC(this.pcEnd); 
                  this.pc[this.pcEnd++] = ch;
                } 
                normalizedCR = false;
              }  
          } else {
            throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + 
                printable(ch), this, null);
          } 
          ch = more();
        } while (!this.reachedEnd); 
    } catch (EOFException ex) {
      this.reachedEnd = true;
    } 
    if (this.tokenize && gotS) {
      this.posEnd = this.pos;
      return this.eventType = 7;
    } 
    return this.eventType = 1;
  }
  
  public int parseEndTag() throws XmlPullParserException, IOException {
    char ch = more();
    if (!isNameStartChar(ch))
      throw new XmlPullParserException("expected name start and not " + printable(ch), this, null); 
    this.posStart = this.pos - 3;
    int nameStart = this.pos - 1 + this.bufAbsoluteStart;
    do {
      ch = more();
    } while (isNameChar(ch));
    int off = nameStart - this.bufAbsoluteStart;
    int len = this.pos - 1 - off;
    char[] cbuf = this.elRawName[this.depth];
    if (this.elRawNameEnd[this.depth] != len) {
      String startname = new String(cbuf, 0, this.elRawNameEnd[this.depth]);
      String endname = new String(this.buf, off, len);
      throw new XmlPullParserException("end tag name </" + endname + "> must match start tag name <" + startname + "> from line " + this.elRawNameLine[this.depth], this, null);
    } 
    for (int i = 0; i < len; i++) {
      if (this.buf[off++] != cbuf[i]) {
        String startname = new String(cbuf, 0, len);
        String endname = new String(this.buf, off - i - 1, len);
        throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + "> from line " + this.elRawNameLine[this.depth], this, null);
      } 
    } 
    while (isS(ch))
      ch = more(); 
    if (ch != '>')
      throw new XmlPullParserException("expected > to finsh end tag not " + printable(ch) + " from line " + this.elRawNameLine[this.depth], this, null); 
    this.posEnd = this.pos;
    this.pastEndTag = true;
    return this.eventType = 3;
  }
  
  public int parseStartTag() throws XmlPullParserException, IOException {
    this.depth++;
    this.posStart = this.pos - 2;
    this.emptyElementTag = false;
    this.attributeCount = 0;
    int nameStart = this.pos - 1 + this.bufAbsoluteStart;
    int colonPos = -1;
    char ch = this.buf[this.pos - 1];
    if (ch == ':' && this.processNamespaces)
      throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, null); 
    while (true) {
      ch = more();
      if (!isNameChar(ch))
        break; 
      if (ch == ':' && this.processNamespaces) {
        if (colonPos != -1)
          throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, null); 
        colonPos = this.pos - 1 + this.bufAbsoluteStart;
      } 
    } 
    ensureElementsCapacity();
    int elLen = this.pos - 1 - nameStart - this.bufAbsoluteStart;
    if (this.elRawName[this.depth] == null || (this.elRawName[this.depth]).length < elLen)
      this.elRawName[this.depth] = new char[2 * elLen]; 
    System.arraycopy(this.buf, nameStart - this.bufAbsoluteStart, this.elRawName[this.depth], 0, elLen);
    this.elRawNameEnd[this.depth] = elLen;
    this.elRawNameLine[this.depth] = this.lineNumber;
    String name = null;
    String prefix = null;
    if (this.processNamespaces) {
      if (colonPos != -1) {
        prefix = this.elPrefix[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, colonPos - nameStart);
        name = this.elName[this.depth] = newString(this.buf, colonPos + 1 - this.bufAbsoluteStart, this.pos - 2 - colonPos - this.bufAbsoluteStart);
      } else {
        prefix = this.elPrefix[this.depth] = null;
        name = this.elName[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
      } 
    } else {
      name = this.elName[this.depth] = newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
    } 
    while (true) {
      while (isS(ch))
        ch = more(); 
      if (ch == '>')
        break; 
      if (ch == '/') {
        if (this.emptyElementTag)
          throw new XmlPullParserException("repeated / in tag declaration", this, null); 
        this.emptyElementTag = true;
        ch = more();
        if (ch != '>')
          throw new XmlPullParserException("expected > to end empty tag not " + printable(ch), this, null); 
        break;
      } 
      if (isNameStartChar(ch)) {
        ch = parseAttribute();
        ch = more();
        continue;
      } 
      throw new XmlPullParserException("start tag unexpected character " + printable(ch), this, null);
    } 
    if (this.processNamespaces) {
      String uri = getNamespace(prefix);
      if (uri == null)
        if (prefix == null) {
          uri = "";
        } else {
          throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, null);
        }  
      this.elUri[this.depth] = uri;
      int i;
      for (i = 0; i < this.attributeCount; i++) {
        String attrPrefix = this.attributePrefix[i];
        if (attrPrefix != null) {
          String attrUri = getNamespace(attrPrefix);
          if (attrUri == null)
            throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attrPrefix, this, null); 
          this.attributeUri[i] = attrUri;
        } else {
          this.attributeUri[i] = "";
        } 
      } 
      for (i = 1; i < this.attributeCount; i++) {
        for (int j = 0; j < i; j++) {
          if (this.attributeUri[j] == this.attributeUri[i] && ((this.allStringsInterned && this.attributeName[j]
            .equals(this.attributeName[i])) || (!this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j]
            
            .equals(this.attributeName[i])))) {
            String attr1 = this.attributeName[j];
            if (this.attributeUri[j] != null)
              attr1 = this.attributeUri[j] + ":" + attr1; 
            String attr2 = this.attributeName[i];
            if (this.attributeUri[i] != null)
              attr2 = this.attributeUri[i] + ":" + attr2; 
            throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
          } 
        } 
      } 
    } else {
      for (int i = 1; i < this.attributeCount; i++) {
        for (int j = 0; j < i; j++) {
          if ((this.allStringsInterned && this.attributeName[j].equals(this.attributeName[i])) || (!this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j]
            
            .equals(this.attributeName[i]))) {
            String attr1 = this.attributeName[j];
            String attr2 = this.attributeName[i];
            throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
          } 
        } 
      } 
    } 
    this.elNamespaceCount[this.depth] = this.namespaceEnd;
    this.posEnd = this.pos;
    return this.eventType = 2;
  }
  
  private char parseAttribute() throws XmlPullParserException, IOException {
    int prevPosStart = this.posStart + this.bufAbsoluteStart;
    int nameStart = this.pos - 1 + this.bufAbsoluteStart;
    int colonPos = -1;
    char ch = this.buf[this.pos - 1];
    if (ch == ':' && this.processNamespaces)
      throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, null); 
    boolean startsWithXmlns = (this.processNamespaces && ch == 'x');
    int xmlnsPos = 0;
    ch = more();
    while (isNameChar(ch)) {
      if (this.processNamespaces) {
        if (startsWithXmlns && xmlnsPos < 5) {
          xmlnsPos++;
          if (xmlnsPos == 1) {
            if (ch != 'm')
              startsWithXmlns = false; 
          } else if (xmlnsPos == 2) {
            if (ch != 'l')
              startsWithXmlns = false; 
          } else if (xmlnsPos == 3) {
            if (ch != 'n')
              startsWithXmlns = false; 
          } else if (xmlnsPos == 4) {
            if (ch != 's')
              startsWithXmlns = false; 
          } else if (xmlnsPos == 5) {
            if (ch != ':')
              throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, null); 
          } 
        } 
        if (ch == ':') {
          if (colonPos != -1)
            throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, null); 
          colonPos = this.pos - 1 + this.bufAbsoluteStart;
        } 
      } 
      ch = more();
    } 
    ensureAttributesCapacity(this.attributeCount);
    String name = null;
    String prefix = null;
    if (this.processNamespaces) {
      if (xmlnsPos < 4)
        startsWithXmlns = false; 
      if (startsWithXmlns) {
        if (colonPos != -1) {
          int nameLen = this.pos - 2 - colonPos - this.bufAbsoluteStart;
          if (nameLen == 0)
            throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, null); 
          name = newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
        } 
      } else {
        if (colonPos != -1) {
          int prefixLen = colonPos - nameStart;
          prefix = this.attributePrefix[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, prefixLen);
          int nameLen = this.pos - 2 - colonPos - this.bufAbsoluteStart;
          name = this.attributeName[this.attributeCount] = newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
        } else {
          prefix = this.attributePrefix[this.attributeCount] = null;
          name = this.attributeName[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - nameStart - this.bufAbsoluteStart);
        } 
        if (!this.allStringsInterned)
          this.attributeNameHash[this.attributeCount] = name.hashCode(); 
      } 
    } else {
      name = this.attributeName[this.attributeCount] = newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - nameStart - this.bufAbsoluteStart);
      if (!this.allStringsInterned)
        this.attributeNameHash[this.attributeCount] = name.hashCode(); 
    } 
    while (isS(ch))
      ch = more(); 
    if (ch != '=')
      throw new XmlPullParserException("expected = after attribute name", this, null); 
    ch = more();
    while (isS(ch))
      ch = more(); 
    char delimit = ch;
    if (delimit != '"' && delimit != '\'')
      throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + 
          printable(delimit), this, null); 
    boolean normalizedCR = false;
    this.usePC = false;
    this.pcStart = this.pcEnd;
    this.posStart = this.pos;
    while (true) {
      ch = more();
      if (ch == delimit)
        break; 
      if (ch == '<')
        throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, null); 
      if (ch == '&') {
        extractEntityRef();
      } else if (ch == '\t' || ch == '\n' || ch == '\r') {
        if (!this.usePC) {
          this.posEnd = this.pos - 1;
          if (this.posEnd > this.posStart) {
            joinPC();
          } else {
            this.usePC = true;
            this.pcEnd = this.pcStart = 0;
          } 
        } 
        if (this.pcEnd >= this.pc.length)
          ensurePC(this.pcEnd); 
        if (ch != '\n' || !normalizedCR)
          this.pc[this.pcEnd++] = ' '; 
      } else if (this.usePC) {
        if (this.pcEnd >= this.pc.length)
          ensurePC(this.pcEnd); 
        this.pc[this.pcEnd++] = ch;
      } 
      normalizedCR = (ch == '\r');
    } 
    if (this.processNamespaces && startsWithXmlns) {
      String ns = null;
      if (!this.usePC) {
        ns = newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart);
      } else {
        ns = newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
      } 
      ensureNamespacesCapacity(this.namespaceEnd);
      int prefixHash = -1;
      if (colonPos != -1) {
        if (ns.length() == 0)
          throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, null); 
        this.namespacePrefix[this.namespaceEnd] = name;
        if (!this.allStringsInterned)
          prefixHash = this.namespacePrefixHash[this.namespaceEnd] = name.hashCode(); 
      } else {
        this.namespacePrefix[this.namespaceEnd] = null;
        if (!this.allStringsInterned)
          prefixHash = this.namespacePrefixHash[this.namespaceEnd] = -1; 
      } 
      this.namespaceUri[this.namespaceEnd] = ns;
      int startNs = this.elNamespaceCount[this.depth - 1];
      for (int i = this.namespaceEnd - 1; i >= startNs; i--) {
        if (((this.allStringsInterned || name == null) && this.namespacePrefix[i] == name) || (!this.allStringsInterned && name != null && this.namespacePrefixHash[i] == prefixHash && name
          .equals(this.namespacePrefix[i]))) {
          String s = (name == null) ? "default" : ("'" + name + "'");
          throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, null);
        } 
      } 
      this.namespaceEnd++;
    } else {
      if (!this.usePC) {
        this.attributeValue[this.attributeCount] = new String(this.buf, this.posStart, this.pos - 1 - this.posStart);
      } else {
        this.attributeValue[this.attributeCount] = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
      } 
      this.attributeCount++;
    } 
    this.posStart = prevPosStart - this.bufAbsoluteStart;
    return ch;
  }
  
  private static final char[] BUF_NOT_RESOLVED = new char[0];
  
  private static final char[] BUF_LT = new char[] { '<' };
  
  private static final char[] BUF_AMP = new char[] { '&' };
  
  private static final char[] BUF_GT = new char[] { '>' };
  
  private static final char[] BUF_APO = new char[] { '\'' };
  
  private static final char[] BUF_QUOT = new char[] { '"' };
  
  private char[] resolvedEntityRefCharBuf;
  
  public MXParser() {
    this.resolvedEntityRefCharBuf = BUF_NOT_RESOLVED;
    this.replacementMapTemplate = null;
  }
  
  public MXParser(EntityReplacementMap entityReplacementMap) {
    this.resolvedEntityRefCharBuf = BUF_NOT_RESOLVED;
    this.replacementMapTemplate = entityReplacementMap;
  }
  
  private int parseCharOrPredefinedEntityRef() throws XmlPullParserException, IOException {
    this.entityRefName = null;
    this.posStart = this.pos;
    int len = 0;
    this.resolvedEntityRefCharBuf = BUF_NOT_RESOLVED;
    char ch = more();
    if (ch == '#') {
      char charRef = Character.MIN_VALUE;
      ch = more();
      StringBuilder sb = new StringBuilder();
      boolean isHex = (ch == 'x');
      if (isHex) {
        while (true) {
          ch = more();
          if (ch >= '0' && ch <= '9') {
            charRef = (char)(charRef * 16 + ch - 48);
            sb.append(ch);
            continue;
          } 
          if (ch >= 'a' && ch <= 'f') {
            charRef = (char)(charRef * 16 + ch - 87);
            sb.append(ch);
            continue;
          } 
          if (ch >= 'A' && ch <= 'F') {
            charRef = (char)(charRef * 16 + ch - 55);
            sb.append(ch);
            continue;
          } 
          break;
        } 
        if (ch != ';')
          throw new XmlPullParserException("character reference (with hex value) may not contain " + 
              printable(ch), this, null); 
      } else {
        while (true) {
          if (ch >= '0' && ch <= '9') {
            charRef = (char)(charRef * 10 + ch - 48);
            sb.append(ch);
          } else {
            if (ch == ';')
              break; 
            throw new XmlPullParserException("character reference (with decimal value) may not contain " + 
                printable(ch), this, null);
          } 
          ch = more();
        } 
      } 
      boolean isValidCodePoint = true;
      try {
        int codePoint = Integer.parseInt(sb.toString(), isHex ? 16 : 10);
        isValidCodePoint = isValidCodePoint(codePoint);
        if (isValidCodePoint)
          this.resolvedEntityRefCharBuf = Character.toChars(codePoint); 
      } catch (IllegalArgumentException e) {
        isValidCodePoint = false;
      } 
      if (!isValidCodePoint)
        throw new XmlPullParserException("character reference (with " + (isHex ? "hex" : "decimal") + " value " + sb
            .toString() + ") is invalid", this, null); 
      if (this.tokenize)
        this.text = newString(this.resolvedEntityRefCharBuf, 0, this.resolvedEntityRefCharBuf.length); 
      len = this.resolvedEntityRefCharBuf.length;
    } else {
      if (!isNameStartChar(ch))
        throw new XmlPullParserException("entity reference names can not start with character '" + 
            printable(ch) + "'", this, null); 
      while (true) {
        ch = more();
        if (ch == ';')
          break; 
        if (!isNameChar(ch))
          throw new XmlPullParserException("entity reference name can not contain character " + 
              printable(ch) + "'", this, null); 
      } 
      len = this.pos - 1 - this.posStart;
      if (len == 2 && this.buf[this.posStart] == 'l' && this.buf[this.posStart + 1] == 't') {
        if (this.tokenize)
          this.text = "<"; 
        this.resolvedEntityRefCharBuf = BUF_LT;
      } else if (len == 3 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'm' && this.buf[this.posStart + 2] == 'p') {
        if (this.tokenize)
          this.text = "&"; 
        this.resolvedEntityRefCharBuf = BUF_AMP;
      } else if (len == 2 && this.buf[this.posStart] == 'g' && this.buf[this.posStart + 1] == 't') {
        if (this.tokenize)
          this.text = ">"; 
        this.resolvedEntityRefCharBuf = BUF_GT;
      } else if (len == 4 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'p' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 's') {
        if (this.tokenize)
          this.text = "'"; 
        this.resolvedEntityRefCharBuf = BUF_APO;
      } else if (len == 4 && this.buf[this.posStart] == 'q' && this.buf[this.posStart + 1] == 'u' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 't') {
        if (this.tokenize)
          this.text = "\""; 
        this.resolvedEntityRefCharBuf = BUF_QUOT;
      } 
    } 
    this.posEnd = this.pos;
    return len;
  }
  
  private void parseEntityRefInDocDecl() throws XmlPullParserException, IOException {
    parseCharOrPredefinedEntityRef();
    if (this.usePC) {
      this.posStart--;
      joinPC();
    } 
    if (this.resolvedEntityRefCharBuf != BUF_NOT_RESOLVED)
      return; 
    if (this.tokenize)
      this.text = null; 
  }
  
  private void parseEntityRef() throws XmlPullParserException, IOException {
    int len = parseCharOrPredefinedEntityRef();
    this.posEnd--;
    if (this.resolvedEntityRefCharBuf != BUF_NOT_RESOLVED)
      return; 
    this.resolvedEntityRefCharBuf = lookuEntityReplacement(len);
    if (this.resolvedEntityRefCharBuf != BUF_NOT_RESOLVED)
      return; 
    if (this.tokenize)
      this.text = null; 
  }
  
  private static boolean isValidCodePoint(int codePoint) {
    return (codePoint == 9 || codePoint == 10 || codePoint == 13 || (32 <= codePoint && codePoint <= 55295) || (57344 <= codePoint && codePoint <= 65533) || (65536 <= codePoint && codePoint <= 1114111));
  }
  
  private char[] lookuEntityReplacement(int entityNameLen) {
    if (!this.allStringsInterned) {
      int hash = fastHash(this.buf, this.posStart, this.posEnd - this.posStart);
      int i;
      label30: for (i = this.entityEnd - 1; i >= 0; i--) {
        if (hash == this.entityNameHash[i] && entityNameLen == (this.entityNameBuf[i]).length) {
          char[] entityBuf = this.entityNameBuf[i];
          for (int j = 0; j < entityNameLen; j++) {
            if (this.buf[this.posStart + j] != entityBuf[j])
              continue label30; 
          } 
          if (this.tokenize)
            this.text = this.entityReplacement[i]; 
          return this.entityReplacementBuf[i];
        } 
      } 
    } else {
      this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart);
      for (int i = this.entityEnd - 1; i >= 0; i--) {
        if (this.entityRefName == this.entityName[i]) {
          if (this.tokenize)
            this.text = this.entityReplacement[i]; 
          return this.entityReplacementBuf[i];
        } 
      } 
    } 
    return BUF_NOT_RESOLVED;
  }
  
  private void parseComment() throws XmlPullParserException, IOException {
    char ch = more();
    if (ch != '-')
      throw new XmlPullParserException("expected <!-- for comment start", this, null); 
    if (this.tokenize)
      this.posStart = this.pos; 
    int curLine = this.lineNumber;
    int curColumn = this.columnNumber - 4;
    try {
      boolean normalizeIgnorableWS = (this.tokenize && !this.roundtripSupported);
      boolean normalizedCR = false;
      boolean seenDash = false;
      boolean seenDashDash = false;
      while (true) {
        ch = more();
        if (seenDashDash && ch != '>')
          throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + 
              printable(ch), this, null); 
        if (ch == '-') {
          if (!seenDash) {
            seenDash = true;
          } else {
            seenDashDash = true;
          } 
        } else if (ch == '>') {
          if (seenDashDash)
            break; 
          seenDash = false;
        } else if (isValidCodePoint(ch)) {
          seenDash = false;
        } else {
          throw new XmlPullParserException("Illegal character 0x" + Integer.toHexString(ch) + " found in comment", this, null);
        } 
        if (normalizeIgnorableWS) {
          if (ch == '\r') {
            normalizedCR = true;
            if (!this.usePC) {
              this.posEnd = this.pos - 1;
              if (this.posEnd > this.posStart) {
                joinPC();
              } else {
                this.usePC = true;
                this.pcStart = this.pcEnd = 0;
              } 
            } 
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = '\n';
            continue;
          } 
          if (ch == '\n') {
            if (!normalizedCR && this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = '\n';
            } 
            normalizedCR = false;
            continue;
          } 
          if (this.usePC) {
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = ch;
          } 
          normalizedCR = false;
        } 
      } 
    } catch (EOFException ex) {
      throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    } 
    if (this.tokenize) {
      this.posEnd = this.pos - 3;
      if (this.usePC)
        this.pcEnd -= 2; 
    } 
  }
  
  private boolean parsePI() throws XmlPullParserException, IOException {
    if (this.tokenize)
      this.posStart = this.pos; 
    int curLine = this.lineNumber;
    int curColumn = this.columnNumber - 2;
    int piTargetStart = this.pos;
    int piTargetEnd = -1;
    boolean normalizeIgnorableWS = (this.tokenize && !this.roundtripSupported);
    boolean normalizedCR = false;
    try {
      boolean seenPITarget = false;
      boolean seenInnerTag = false;
      boolean seenQ = false;
      char ch = more();
      if (isS(ch))
        throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, null); 
      while (true) {
        if (ch == '?') {
          if (!seenPITarget)
            throw new XmlPullParserException("processing instruction PITarget name not found", this, null); 
          seenQ = true;
        } else if (ch == '>') {
          if (seenQ)
            break; 
          if (!seenPITarget)
            throw new XmlPullParserException("processing instruction PITarget name not found", this, null); 
          if (!seenInnerTag)
            throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, null); 
          seenInnerTag = false;
        } else if (ch == '<') {
          seenInnerTag = true;
        } else {
          if (piTargetEnd == -1 && isS(ch)) {
            piTargetEnd = this.pos - 1;
            if (piTargetEnd - piTargetStart >= 3)
              if ((this.buf[piTargetStart] == 'x' || this.buf[piTargetStart] == 'X') && (this.buf[piTargetStart + 1] == 'm' || this.buf[piTargetStart + 1] == 'M') && (this.buf[piTargetStart + 2] == 'l' || this.buf[piTargetStart + 2] == 'L')) {
                if (piTargetStart > 3)
                  throw new XmlPullParserException("processing instruction can not have PITarget with reserved xml name", this, null); 
                if (this.buf[piTargetStart] != 'x' && this.buf[piTargetStart + 1] != 'm' && this.buf[piTargetStart + 2] != 'l')
                  throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, null); 
                parseXmlDecl(ch);
                if (this.tokenize)
                  this.posEnd = this.pos - 2; 
                int off = piTargetStart + 3;
                int len = this.pos - 2 - off;
                this.xmlDeclContent = newString(this.buf, off, len);
                return false;
              }  
          } 
          seenQ = false;
        } 
        if (normalizeIgnorableWS)
          if (ch == '\r') {
            normalizedCR = true;
            if (!this.usePC) {
              this.posEnd = this.pos - 1;
              if (this.posEnd > this.posStart) {
                joinPC();
              } else {
                this.usePC = true;
                this.pcStart = this.pcEnd = 0;
              } 
            } 
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = '\n';
          } else if (ch == '\n') {
            if (!normalizedCR && this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = '\n';
            } 
            normalizedCR = false;
          } else {
            if (this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = ch;
            } 
            normalizedCR = false;
          }  
        seenPITarget = true;
        ch = more();
      } 
    } catch (EOFException ex) {
      throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    } 
    if (piTargetEnd == -1)
      piTargetEnd = this.pos - 2 + this.bufAbsoluteStart; 
    if (this.tokenize) {
      this.posEnd = this.pos - 2;
      if (normalizeIgnorableWS)
        this.pcEnd--; 
    } 
    return true;
  }
  
  private static final char[] VERSION = "version".toCharArray();
  
  private static final char[] NCODING = "ncoding".toCharArray();
  
  private static final char[] TANDALONE = "tandalone".toCharArray();
  
  private static final char[] YES = "yes".toCharArray();
  
  private static final char[] NO = "no".toCharArray();
  
  private static final int LOOKUP_MAX = 1024;
  
  private static final char LOOKUP_MAX_CHAR = 'Ѐ';
  
  private void parseXmlDecl(char ch) throws XmlPullParserException, IOException {
    this.preventBufferCompaction = true;
    this.bufStart = 0;
    ch = skipS(ch);
    ch = requireInput(ch, VERSION);
    ch = skipS(ch);
    if (ch != '=')
      throw new XmlPullParserException("expected equals sign (=) after version and not " + printable(ch), this, null); 
    ch = more();
    ch = skipS(ch);
    if (ch != '\'' && ch != '"')
      throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + 
          printable(ch), this, null); 
    char quotChar = ch;
    int versionStart = this.pos;
    ch = more();
    while (ch != quotChar) {
      if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '_' && ch != '.' && ch != ':' && ch != '-')
        throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + 
            printable(ch), this, null); 
      ch = more();
    } 
    int versionEnd = this.pos - 1;
    parseXmlDeclWithVersion(versionStart, versionEnd);
    this.preventBufferCompaction = false;
  }
  
  private void parseXmlDeclWithVersion(int versionStart, int versionEnd) throws XmlPullParserException, IOException {
    if (versionEnd - versionStart != 3 || this.buf[versionStart] != '1' || this.buf[versionStart + 1] != '.' || this.buf[versionStart + 2] != '0')
      throw new XmlPullParserException("only 1.0 is supported as <?xml version not '" + 
          printable(new String(this.buf, versionStart, versionEnd - versionStart)) + "'", this, null); 
    this.xmlDeclVersion = newString(this.buf, versionStart, versionEnd - versionStart);
    String lastParsedAttr = "version";
    char ch = more();
    char prevCh = ch;
    ch = skipS(ch);
    if (ch != 'e' && ch != 's' && ch != '?' && ch != '>')
      throw new XmlPullParserException("unexpected character " + printable(ch), this, null); 
    if (ch == 'e') {
      if (!isS(prevCh))
        throw new XmlPullParserException("expected a space after " + lastParsedAttr + " and not " + 
            printable(ch), this, null); 
      ch = more();
      ch = requireInput(ch, NCODING);
      ch = skipS(ch);
      if (ch != '=')
        throw new XmlPullParserException("expected equals sign (=) after encoding and not " + printable(ch), this, null); 
      ch = more();
      ch = skipS(ch);
      if (ch != '\'' && ch != '"')
        throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + 
            printable(ch), this, null); 
      char quotChar = ch;
      int encodingStart = this.pos;
      ch = more();
      if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z'))
        throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + 
            printable(ch), this, null); 
      ch = more();
      while (ch != quotChar) {
        if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9') && ch != '.' && ch != '_' && ch != '-')
          throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + 
              printable(ch), this, null); 
        ch = more();
      } 
      int encodingEnd = this.pos - 1;
      this.inputEncoding = newString(this.buf, encodingStart, encodingEnd - encodingStart);
      if ("UTF8".equals(this.fileEncoding) && this.inputEncoding.toUpperCase().startsWith("ISO-"))
        throw new XmlPullParserException("UTF-8 BOM plus xml decl of " + this.inputEncoding + " is incompatible", this, null); 
      if ("UTF-16".equals(this.fileEncoding) && this.inputEncoding.equalsIgnoreCase("UTF-8"))
        throw new XmlPullParserException("UTF-16 BOM plus xml decl of " + this.inputEncoding + " is incompatible", this, null); 
      lastParsedAttr = "encoding";
      ch = more();
      prevCh = ch;
      ch = skipS(ch);
    } 
    if (ch == 's') {
      if (!isS(prevCh))
        throw new XmlPullParserException("expected a space after " + lastParsedAttr + " and not " + 
            printable(ch), this, null); 
      ch = more();
      ch = requireInput(ch, TANDALONE);
      ch = skipS(ch);
      if (ch != '=')
        throw new XmlPullParserException("expected equals sign (=) after standalone and not " + 
            printable(ch), this, null); 
      ch = more();
      ch = skipS(ch);
      if (ch != '\'' && ch != '"')
        throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after standalone and not " + 
            printable(ch), this, null); 
      char quotChar = ch;
      ch = more();
      if (ch == 'y') {
        ch = requireInput(ch, YES);
        this.xmlDeclStandalone = Boolean.valueOf(true);
      } else if (ch == 'n') {
        ch = requireInput(ch, NO);
        this.xmlDeclStandalone = Boolean.valueOf(false);
      } else {
        throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + printable(ch), this, null);
      } 
      if (ch != quotChar)
        throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + 
            printable(ch), this, null); 
      ch = more();
      ch = skipS(ch);
    } 
    if (ch != '?')
      throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null); 
    ch = more();
    if (ch != '>')
      throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null); 
  }
  
  private void parseDocdecl() throws XmlPullParserException, IOException {
    char ch = more();
    if (ch != 'O')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    ch = more();
    if (ch != 'C')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    ch = more();
    if (ch != 'T')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    ch = more();
    if (ch != 'Y')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    ch = more();
    if (ch != 'P')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    ch = more();
    if (ch != 'E')
      throw new XmlPullParserException("expected <!DOCTYPE", this, null); 
    this.posStart = this.pos;
    int bracketLevel = 0;
    boolean normalizeIgnorableWS = (this.tokenize && !this.roundtripSupported);
    boolean normalizedCR = false;
    while (true) {
      ch = more();
      if (ch == '[') {
        bracketLevel++;
      } else if (ch == ']') {
        bracketLevel--;
      } else {
        if (ch == '>' && bracketLevel == 0)
          break; 
        if (ch == '&') {
          extractEntityRefInDocDecl();
          continue;
        } 
      } 
      if (normalizeIgnorableWS) {
        if (ch == '\r') {
          normalizedCR = true;
          if (!this.usePC) {
            this.posEnd = this.pos - 1;
            if (this.posEnd > this.posStart) {
              joinPC();
            } else {
              this.usePC = true;
              this.pcStart = this.pcEnd = 0;
            } 
          } 
          if (this.pcEnd >= this.pc.length)
            ensurePC(this.pcEnd); 
          this.pc[this.pcEnd++] = '\n';
          continue;
        } 
        if (ch == '\n') {
          if (!normalizedCR && this.usePC) {
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = '\n';
          } 
          normalizedCR = false;
          continue;
        } 
        if (this.usePC) {
          if (this.pcEnd >= this.pc.length)
            ensurePC(this.pcEnd); 
          this.pc[this.pcEnd++] = ch;
        } 
        normalizedCR = false;
      } 
    } 
    this.posEnd = this.pos - 1;
    this.text = null;
  }
  
  private void extractEntityRefInDocDecl() throws XmlPullParserException, IOException {
    this.posEnd = this.pos - 1;
    int prevPosStart = this.posStart;
    parseEntityRefInDocDecl();
    this.posStart = prevPosStart;
  }
  
  private void extractEntityRef() throws XmlPullParserException, IOException {
    this.posEnd = this.pos - 1;
    if (!this.usePC) {
      boolean hadCharData = (this.posEnd > this.posStart);
      if (hadCharData) {
        joinPC();
      } else {
        this.usePC = true;
        this.pcStart = this.pcEnd = 0;
      } 
    } 
    parseEntityRef();
    if (this.resolvedEntityRefCharBuf == BUF_NOT_RESOLVED) {
      if (this.entityRefName == null)
        this.entityRefName = newString(this.buf, this.posStart, this.posEnd - this.posStart); 
      throw new XmlPullParserException("could not resolve entity named '" + printable(this.entityRefName) + "'", this, null);
    } 
    for (char aResolvedEntity : this.resolvedEntityRefCharBuf) {
      if (this.pcEnd >= this.pc.length)
        ensurePC(this.pcEnd); 
      this.pc[this.pcEnd++] = aResolvedEntity;
    } 
  }
  
  private void parseCDSect(boolean hadCharData) throws XmlPullParserException, IOException {
    char ch = more();
    if (ch != 'C')
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null); 
    ch = more();
    if (ch != 'D')
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null); 
    ch = more();
    if (ch != 'A')
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null); 
    ch = more();
    if (ch != 'T')
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null); 
    ch = more();
    if (ch != 'A')
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null); 
    ch = more();
    if (ch != '[')
      throw new XmlPullParserException("expected <![CDATA[ for comment start", this, null); 
    int cdStart = this.pos + this.bufAbsoluteStart;
    int curLine = this.lineNumber;
    int curColumn = this.columnNumber;
    boolean normalizeInput = (!this.tokenize || !this.roundtripSupported);
    try {
      if (normalizeInput)
        if (hadCharData)
          if (!this.usePC)
            if (this.posEnd > this.posStart) {
              joinPC();
            } else {
              this.usePC = true;
              this.pcStart = this.pcEnd = 0;
            }    
      boolean seenBracket = false;
      boolean seenBracketBracket = false;
      boolean normalizedCR = false;
      while (true) {
        ch = more();
        if (ch == ']') {
          if (!seenBracket) {
            seenBracket = true;
          } else {
            seenBracketBracket = true;
          } 
        } else if (ch == '>') {
          if (seenBracket && seenBracketBracket)
            break; 
          seenBracketBracket = false;
          seenBracket = false;
        } else if (seenBracket) {
          seenBracket = false;
        } 
        if (normalizeInput) {
          if (ch == '\r') {
            normalizedCR = true;
            this.posStart = cdStart - this.bufAbsoluteStart;
            this.posEnd = this.pos - 1;
            if (!this.usePC)
              if (this.posEnd > this.posStart) {
                joinPC();
              } else {
                this.usePC = true;
                this.pcStart = this.pcEnd = 0;
              }  
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = '\n';
            continue;
          } 
          if (ch == '\n') {
            if (!normalizedCR && this.usePC) {
              if (this.pcEnd >= this.pc.length)
                ensurePC(this.pcEnd); 
              this.pc[this.pcEnd++] = '\n';
            } 
            normalizedCR = false;
            continue;
          } 
          if (this.usePC) {
            if (this.pcEnd >= this.pc.length)
              ensurePC(this.pcEnd); 
            this.pc[this.pcEnd++] = ch;
          } 
          normalizedCR = false;
        } 
      } 
    } catch (EOFException ex) {
      throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    } 
    if (normalizeInput)
      if (this.usePC)
        this.pcEnd -= 2;  
    this.posStart = cdStart - this.bufAbsoluteStart;
    this.posEnd = this.pos - 3;
  }
  
  private void fillBuf() throws IOException, XmlPullParserException {
    if (this.reader == null)
      throw new XmlPullParserException("reader must be set before parsing is started"); 
    if (this.bufEnd > this.bufSoftLimit) {
      boolean compact = (!this.preventBufferCompaction && (this.bufStart > this.bufSoftLimit || this.bufStart >= this.buf.length / 2));
      if (compact) {
        System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
      } else {
        int newSize = 2 * this.buf.length;
        char[] newBuf = new char[newSize];
        System.arraycopy(this.buf, this.bufStart, newBuf, 0, this.bufEnd - this.bufStart);
        this.buf = newBuf;
        if (this.bufLoadFactor > 0)
          this.bufSoftLimit = (int)(this.bufferLoadFactor * this.buf.length); 
      } 
      this.bufEnd -= this.bufStart;
      this.pos -= this.bufStart;
      this.posStart -= this.bufStart;
      this.posEnd -= this.bufStart;
      this.bufAbsoluteStart += this.bufStart;
      this.bufStart = 0;
    } 
    int len = Math.min(this.buf.length - this.bufEnd, 8192);
    int ret = this.reader.read(this.buf, this.bufEnd, len);
    if (ret > 0) {
      this.bufEnd += ret;
      return;
    } 
    if (ret == -1) {
      if (this.bufAbsoluteStart == 0 && this.pos == 0)
        throw new EOFException("input contained no data"); 
      if (this.seenRoot && this.depth == 0) {
        this.reachedEnd = true;
        return;
      } 
      StringBuilder expectedTagStack = new StringBuilder();
      if (this.depth > 0)
        if (this.elRawName == null || this.elRawName[this.depth] == null) {
          String tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
          expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
        } else {
          expectedTagStack.append(" - expected end tag");
          if (this.depth > 1)
            expectedTagStack.append("s"); 
          expectedTagStack.append(" ");
          int i;
          for (i = this.depth; i > 0; i--) {
            if (this.elRawName == null || this.elRawName[i] == null) {
              String tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
              expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
            } else {
              String tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
              expectedTagStack.append("</").append(tagName).append('>');
            } 
          } 
          expectedTagStack.append(" to close");
          for (i = this.depth; i > 0; i--) {
            if (i != this.depth)
              expectedTagStack.append(" and"); 
            if (this.elRawName == null || this.elRawName[i] == null) {
              String tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
              expectedTagStack.append(" start tag <").append(tagName).append(">");
              expectedTagStack.append(" from line ").append(this.elRawNameLine[i]);
            } else {
              String tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
              expectedTagStack.append(" start tag <").append(tagName).append(">");
              expectedTagStack.append(" from line ").append(this.elRawNameLine[i]);
            } 
          } 
          expectedTagStack.append(", parser stopped on");
        }  
      throw new EOFException("no more data available" + expectedTagStack.toString() + 
          getPositionDescription());
    } 
    throw new IOException("error reading input, returned " + ret);
  }
  
  private char more() throws IOException, XmlPullParserException {
    if (this.pos >= this.bufEnd) {
      fillBuf();
      if (this.reachedEnd)
        throw new EOFException("no more data available" + getPositionDescription()); 
    } 
    char ch = this.buf[this.pos++];
    if (ch == '\n') {
      this.lineNumber++;
      this.columnNumber = 1;
    } else {
      this.columnNumber++;
    } 
    return ch;
  }
  
  private void ensurePC(int end) {
    int newSize = (end > 8192) ? (2 * end) : 16384;
    char[] newPC = new char[newSize];
    System.arraycopy(this.pc, 0, newPC, 0, this.pcEnd);
    this.pc = newPC;
  }
  
  private void joinPC() {
    int len = this.posEnd - this.posStart;
    int newEnd = this.pcEnd + len + 1;
    if (newEnd >= this.pc.length)
      ensurePC(newEnd); 
    System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, len);
    this.pcEnd += len;
    this.usePC = true;
  }
  
  private char requireInput(char ch, char[] input) throws XmlPullParserException, IOException {
    for (char anInput : input) {
      if (ch != anInput)
        throw new XmlPullParserException("expected " + printable(anInput) + " in " + new String(input) + " and not " + 
            printable(ch), this, null); 
      ch = more();
    } 
    return ch;
  }
  
  private char skipS(char ch) throws XmlPullParserException, IOException {
    while (isS(ch))
      ch = more(); 
    return ch;
  }
  
  private static final boolean[] lookupNameStartChar = new boolean[1024];
  
  private static final boolean[] lookupNameChar = new boolean[1024];
  
  private static void setName(char ch) {
    lookupNameChar[ch] = true;
  }
  
  private static void setNameStart(char ch) {
    lookupNameStartChar[ch] = true;
    setName(ch);
  }
  
  static {
    setNameStart(':');
    char ch;
    for (ch = 'A'; ch <= 'Z'; ch = (char)(ch + 1))
      setNameStart(ch); 
    setNameStart('_');
    for (ch = 'a'; ch <= 'z'; ch = (char)(ch + 1))
      setNameStart(ch); 
    for (ch = 'À'; ch <= '˿'; ch = (char)(ch + 1))
      setNameStart(ch); 
    for (ch = 'Ͱ'; ch <= 'ͽ'; ch = (char)(ch + 1))
      setNameStart(ch); 
    for (ch = 'Ϳ'; ch < 'Ѐ'; ch = (char)(ch + 1))
      setNameStart(ch); 
    setName('-');
    setName('.');
    for (ch = '0'; ch <= '9'; ch = (char)(ch + 1))
      setName(ch); 
    setName('·');
    for (ch = '̀'; ch <= 'ͯ'; ch = (char)(ch + 1))
      setName(ch); 
  }
  
  private static boolean isNameStartChar(char ch) {
    return (ch < 'Ѐ') ? lookupNameStartChar[ch] : (
      (ch <= '‧' || (ch >= '‪' && ch <= '↏') || (ch >= '⠀' && ch <= '￯')));
  }
  
  private static boolean isNameChar(char ch) {
    return (ch < 'Ѐ') ? lookupNameChar[ch] : (
      (ch <= '‧' || (ch >= '‪' && ch <= '↏') || (ch >= '⠀' && ch <= '￯')));
  }
  
  private static boolean isS(char ch) {
    return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t');
  }
  
  private static String printable(char ch) {
    if (ch == '\n')
      return "\\n"; 
    if (ch == '\r')
      return "\\r"; 
    if (ch == '\t')
      return "\\t"; 
    if (ch == '\'')
      return "\\'"; 
    if (ch > '' || ch < ' ')
      return "\\u" + Integer.toHexString(ch); 
    return "" + ch;
  }
  
  private static String printable(String s) {
    if (s == null)
      return null; 
    int sLen = s.length();
    StringBuilder buf = new StringBuilder(sLen + 10);
    for (int i = 0; i < sLen; i++)
      buf.append(printable(s.charAt(i))); 
    s = buf.toString();
    return s;
  }
}
