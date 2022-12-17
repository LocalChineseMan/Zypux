package org.codehaus.plexus.archiver.util;

public class FilePermission {
  private final boolean executable;
  
  private final boolean ownerOnlyExecutable;
  
  private final boolean ownerOnlyReadable;
  
  private final boolean readable;
  
  private final boolean ownerOnlyWritable;
  
  private final boolean writable;
  
  public FilePermission(boolean executable, boolean ownerOnlyExecutable, boolean ownerOnlyReadable, boolean readable, boolean ownerOnlyWritable, boolean writable) {
    this.executable = executable;
    this.ownerOnlyExecutable = ownerOnlyExecutable;
    this.ownerOnlyReadable = ownerOnlyReadable;
    this.readable = readable;
    this.ownerOnlyWritable = ownerOnlyWritable;
    this.writable = writable;
  }
  
  public boolean isExecutable() {
    return this.executable;
  }
  
  public boolean isOwnerOnlyExecutable() {
    return this.ownerOnlyExecutable;
  }
  
  public boolean isOwnerOnlyReadable() {
    return this.ownerOnlyReadable;
  }
  
  public boolean isReadable() {
    return this.readable;
  }
  
  public boolean isOwnerOnlyWritable() {
    return this.ownerOnlyWritable;
  }
  
  public boolean isWritable() {
    return this.writable;
  }
  
  public String toString() {
    return "FilePermission [executable=" + this.executable + ", ownerOnlyExecutable=" + this.ownerOnlyExecutable + ", ownerOnlyReadable=" + this.ownerOnlyReadable + ", readable=" + this.readable + ", ownerOnlyWritable=" + this.ownerOnlyWritable + ", writable=" + this.writable + "]";
  }
}
