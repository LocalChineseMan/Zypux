package org.apache.http.cookie;

import java.util.Date;

public interface SetCookie extends Cookie {
  void setValue(String paramString);
  
  void setComment(String paramString);
  
  void setExpiryDate(Date paramDate);
  
  void setDomain(String paramString);
  
  void setPath(String paramString);
  
  void setSecure(boolean paramBoolean);
  
  void setVersion(int paramInt);
}
