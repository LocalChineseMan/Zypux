package org.codehaus.plexus.util.xml.pull;

public class EntityReplacementMap {
  final String[] entityName;
  
  final char[][] entityNameBuf;
  
  final String[] entityReplacement;
  
  final char[][] entityReplacementBuf;
  
  int entityEnd;
  
  final int[] entityNameHash;
  
  public EntityReplacementMap(String[][] replacements) {
    int length = replacements.length;
    this.entityName = new String[length];
    this.entityNameBuf = new char[length][];
    this.entityReplacement = new String[length];
    this.entityReplacementBuf = new char[length][];
    this.entityNameHash = new int[length];
    for (String[] replacement : replacements)
      defineEntityReplacementText(replacement[0], replacement[1]); 
  }
  
  private void defineEntityReplacementText(String entityName, String replacementText) {
    if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
      String tmp = replacementText.substring(1, replacementText.length() - 1);
      for (int i = 0; i < this.entityName.length; i++) {
        if (this.entityName[i] != null && this.entityName[i].equals(tmp))
          replacementText = this.entityReplacement[i]; 
      } 
    } 
    char[] entityNameCharData = entityName.toCharArray();
    this.entityName[this.entityEnd] = newString(entityNameCharData, 0, entityName.length());
    this.entityNameBuf[this.entityEnd] = entityNameCharData;
    this.entityReplacement[this.entityEnd] = replacementText;
    this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
    this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, (this.entityNameBuf[this.entityEnd]).length);
    this.entityEnd++;
  }
  
  private String newString(char[] cbuf, int off, int len) {
    return new String(cbuf, off, len);
  }
  
  private static int fastHash(char[] ch, int off, int len) {
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
  
  public static final EntityReplacementMap defaultEntityReplacementMap = new EntityReplacementMap(new String[][] { 
        { "nbsp", "??" }, { "iexcl", "??" }, { "cent", "??" }, { "pound", "??" }, { "curren", "??" }, { "yen", "??" }, { "brvbar", "??" }, { "sect", "??" }, { "uml", "??" }, { "copy", "??" }, 
        { "ordf", "??" }, { "laquo", "??" }, { "not", "??" }, { "shy", "??" }, { "reg", "??" }, { "macr", "??" }, { "deg", "??" }, { "plusmn", "??" }, { "sup2", "??" }, { "sup3", "??" }, 
        { "acute", "??" }, { "micro", "??" }, { "para", "??" }, { "middot", "??" }, { "cedil", "??" }, { "sup1", "??" }, { "ordm", "??" }, { "raquo", "??" }, { "frac14", "??" }, { "frac12", "??" }, 
        { "frac34", "??" }, { "iquest", "??" }, { "Agrave", "??" }, { "Aacute", "??" }, { "Acirc", "??" }, { "Atilde", "??" }, { "Auml", "??" }, { "Aring", "??" }, { "AElig", "??" }, { "Ccedil", "??" }, 
        { "Egrave", "??" }, { "Eacute", "??" }, { "Ecirc", "??" }, { "Euml", "??" }, { "Igrave", "??" }, { "Iacute", "??" }, { "Icirc", "??" }, { "Iuml", "??" }, { "ETH", "??" }, { "Ntilde", "??" }, 
        { "Ograve", "??" }, { "Oacute", "??" }, { "Ocirc", "??" }, { "Otilde", "??" }, { "Ouml", "??" }, { "times", "??" }, { "Oslash", "??" }, { "Ugrave", "??" }, { "Uacute", "??" }, { "Ucirc", "??" }, 
        { "Uuml", "??" }, { "Yacute", "??" }, { "THORN", "??" }, { "szlig", "??" }, { "agrave", "??" }, { "aacute", "??" }, { "acirc", "??" }, { "atilde", "??" }, { "auml", "??" }, { "aring", "??" }, 
        { "aelig", "??" }, { "ccedil", "??" }, { "egrave", "??" }, { "eacute", "??" }, { "ecirc", "??" }, { "euml", "??" }, { "igrave", "??" }, { "iacute", "??" }, { "icirc", "??" }, { "iuml", "??" }, 
        { "eth", "??" }, { "ntilde", "??" }, { "ograve", "??" }, { "oacute", "??" }, { "ocirc", "??" }, { "otilde", "??" }, { "ouml", "??" }, { "divide", "??" }, { "oslash", "??" }, { "ugrave", "??" }, 
        { "uacute", "??" }, { "ucirc", "??" }, { "uuml", "??" }, { "yacute", "??" }, { "thorn", "??" }, { "yuml", "??" }, { "OElig", "??" }, { "oelig", "??" }, { "Scaron", "??" }, { "scaron", "??" }, 
        { "Yuml", "??" }, { "circ", "??" }, { "tilde", "??" }, { "ensp", "???" }, { "emsp", "???" }, { "thinsp", "???" }, { "zwnj", "???" }, { "zwj", "???" }, { "lrm", "???" }, { "rlm", "???" }, 
        { "ndash", "???" }, { "mdash", "???" }, { "lsquo", "???" }, { "rsquo", "???" }, { "sbquo", "???" }, { "ldquo", "???" }, { "rdquo", "???" }, { "bdquo", "???" }, { "dagger", "???" }, { "Dagger", "???" }, 
        { "permil", "???" }, { "lsaquo", "???" }, { "rsaquo", "???" }, { "euro", "???" }, { "fnof", "??" }, { "Alpha", "??" }, { "Beta", "??" }, { "Gamma", "??" }, { "Delta", "??" }, { "Epsilon", "??" }, 
        { "Zeta", "??" }, { "Eta", "??" }, { "Theta", "??" }, { "Iota", "??" }, { "Kappa", "??" }, { "Lambda", "??" }, { "Mu", "??" }, { "Nu", "??" }, { "Xi", "??" }, { "Omicron", "??" }, 
        { "Pi", "??" }, { "Rho", "??" }, { "Sigma", "??" }, { "Tau", "??" }, { "Upsilon", "??" }, { "Phi", "??" }, { "Chi", "??" }, { "Psi", "??" }, { "Omega", "??" }, { "alpha", "??" }, 
        { "beta", "??" }, { "gamma", "??" }, { "delta", "??" }, { "epsilon", "??" }, { "zeta", "??" }, { "eta", "??" }, { "theta", "??" }, { "iota", "??" }, { "kappa", "??" }, { "lambda", "??" }, 
        { "mu", "??" }, { "nu", "??" }, { "xi", "??" }, { "omicron", "??" }, { "pi", "??" }, { "rho", "??" }, { "sigmaf", "??" }, { "sigma", "??" }, { "tau", "??" }, { "upsilon", "??" }, 
        { "phi", "??" }, { "chi", "??" }, { "psi", "??" }, { "omega", "??" }, { "thetasym", "??" }, { "upsih", "??" }, { "piv", "??" }, { "bull", "???" }, { "hellip", "???" }, { "prime", "???" }, 
        { "Prime", "???" }, { "oline", "???" }, { "frasl", "???" }, { "weierp", "???" }, { "image", "???" }, { "real", "???" }, { "trade", "???" }, { "alefsym", "???" }, { "larr", "???" }, { "uarr", "???" }, 
        { "rarr", "???" }, { "darr", "???" }, { "harr", "???" }, { "crarr", "???" }, { "lArr", "???" }, { "uArr", "???" }, { "rArr", "???" }, { "dArr", "???" }, { "hArr", "???" }, { "forall", "???" }, 
        { "part", "???" }, { "exist", "???" }, { "empty", "???" }, { "nabla", "???" }, { "isin", "???" }, { "notin", "???" }, { "ni", "???" }, { "prod", "???" }, { "sum", "???" }, { "minus", "???" }, 
        { "lowast", "???" }, { "radic", "???" }, { "prop", "???" }, { "infin", "???" }, { "ang", "???" }, { "and", "???" }, { "or", "???" }, { "cap", "???" }, { "cup", "???" }, { "int", "???" }, 
        { "there4", "???" }, { "sim", "???" }, { "cong", "???" }, { "asymp", "???" }, { "ne", "???" }, { "equiv", "???" }, { "le", "???" }, { "ge", "???" }, { "sub", "???" }, { "sup", "???" }, 
        { "nsub", "???" }, { "sube", "???" }, { "supe", "???" }, { "oplus", "???" }, { "otimes", "???" }, { "perp", "???" }, { "sdot", "???" }, { "lceil", "???" }, { "rceil", "???" }, { "lfloor", "???" }, 
        { "rfloor", "???" }, { "lang", "???" }, { "rang", "???" }, { "loz", "???" }, { "spades", "???" }, { "clubs", "???" }, { "hearts", "???" }, { "diams", "???" } });
}
