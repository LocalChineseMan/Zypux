package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.inject.Named;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.iq80.snappy.SnappyFramedInputStream;

@Named("tar")
public class TarUnArchiver extends AbstractUnArchiver {
  public TarUnArchiver() {}
  
  public TarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
  
  private UntarCompressionMethod compression = UntarCompressionMethod.NONE;
  
  public void setCompression(UntarCompressionMethod method) {
    this.compression = method;
  }
  
  public void setEncoding(String encoding) {
    getLogger().warn("The TarUnArchiver doesn't support the encoding attribute");
  }
  
  protected void execute() throws ArchiverException {
    execute(getSourceFile(), getDestDirectory(), getFileMappers());
  }
  
  protected void execute(String path, File outputDirectory) {
    execute(new File(path), getDestDirectory(), getFileMappers());
  }
  
  protected void execute(File sourceFile, File destDirectory, FileMapper[] fileMappers) throws ArchiverException {
    try {
      getLogger().info("Expanding: " + sourceFile + " into " + destDirectory);
      TarFile tarFile = new TarFile(sourceFile);
      TarArchiveInputStream tis = new TarArchiveInputStream(decompress(this.compression, sourceFile, Streams.bufferedInputStream(Streams.fileInputStream(sourceFile))));
      try {
        TarArchiveEntry te;
        while ((te = tis.getNextTarEntry()) != null) {
          TarResource fileInfo = new TarResource(tarFile, te);
          if (isSelected(te.getName(), (PlexusIoResource)fileInfo)) {
            String symlinkDestination = te.isSymbolicLink() ? te.getLinkName() : null;
            extractFile(sourceFile, destDirectory, (InputStream)tis, te.getName(), te.getModTime(), te.isDirectory(), 
                (te.getMode() != 0) ? Integer.valueOf(te.getMode()) : null, symlinkDestination, fileMappers);
          } 
        } 
        getLogger().debug("expand complete");
        tis.close();
      } catch (Throwable throwable) {
        try {
          tis.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException ioe) {
      throw new ArchiverException("Error while expanding " + sourceFile.getAbsolutePath(), ioe);
    } 
  }
  
  private InputStream decompress(UntarCompressionMethod compression, File file, InputStream istream) throws IOException, ArchiverException {
    if (compression == UntarCompressionMethod.GZIP)
      return Streams.bufferedInputStream(new GZIPInputStream(istream)); 
    if (compression == UntarCompressionMethod.BZIP2)
      return (InputStream)new BZip2CompressorInputStream(istream); 
    if (compression == UntarCompressionMethod.SNAPPY)
      return (InputStream)new SnappyFramedInputStream(istream, true); 
    if (compression == UntarCompressionMethod.XZ)
      return (InputStream)new XZCompressorInputStream(istream); 
    return istream;
  }
  
  public enum UntarCompressionMethod {
    NONE("none"),
    GZIP("gzip"),
    BZIP2("bzip2"),
    SNAPPY("snappy"),
    XZ("xz");
    
    final String value;
    
    UntarCompressionMethod(String value) {
      this.value = value;
    }
  }
}
