package com.ibm.icu.text;

import com.ibm.icu.util.ULocale;

public interface RbnfLenientScannerProvider {
  RbnfLenientScanner get(ULocale paramULocale, String paramString);
}
