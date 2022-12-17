package io.netty.handler.codec.http.multipart;

public interface FileUpload extends HttpData {
  String getFilename();
  
  void setFilename(String paramString);
  
  void setContentType(String paramString);
  
  String getContentType();
  
  void setContentTransferEncoding(String paramString);
  
  String getContentTransferEncoding();
  
  FileUpload copy();
  
  FileUpload duplicate();
  
  FileUpload retain();
  
  FileUpload retain(int paramInt);
}
