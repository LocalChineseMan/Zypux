package com.ibm.icu.text;

interface RBNFPostProcessor {
  void init(RuleBasedNumberFormat paramRuleBasedNumberFormat, String paramString);
  
  void process(StringBuffer paramStringBuffer, NFRuleSet paramNFRuleSet);
}
