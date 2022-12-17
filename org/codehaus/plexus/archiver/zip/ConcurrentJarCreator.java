package org.codehaus.plexus.archiver.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;
import org.apache.commons.compress.archivers.zip.StreamCompressor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
import org.apache.commons.compress.utils.IOUtils;
import org.codehaus.plexus.archiver.util.Streams;

public class ConcurrentJarCreator {
  private final boolean compressAddedZips;
  
  private final ScatterZipOutputStream metaInfDir;
  
  private final ScatterZipOutputStream manifest;
  
  private final ScatterZipOutputStream directories;
  
  private final ScatterZipOutputStream synchronousEntries;
  
  private final ParallelScatterZipCreator parallelScatterZipCreator;
  
  private long zipCloseElapsed;
  
  private static class DeferredSupplier implements ScatterGatherBackingStoreSupplier {
    private int threshold;
    
    DeferredSupplier(int threshold) {
      this.threshold = threshold;
    }
    
    public ScatterGatherBackingStore get() throws IOException {
      return (ScatterGatherBackingStore)new DeferredScatterOutputStream(this.threshold);
    }
  }
  
  public static ScatterZipOutputStream createDeferred(ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
    ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
    StreamCompressor sc = StreamCompressor.create(-1, bs);
    return new ScatterZipOutputStream(bs, sc);
  }
  
  public ConcurrentJarCreator(int nThreads) throws IOException {
    this(true, nThreads);
  }
  
  public ConcurrentJarCreator(boolean compressAddedZips, int nThreads) throws IOException {
    this.compressAddedZips = compressAddedZips;
    ScatterGatherBackingStoreSupplier defaultSupplier = new DeferredSupplier(100000000 / nThreads);
    this.metaInfDir = createDeferred(defaultSupplier);
    this.manifest = createDeferred(defaultSupplier);
    this.directories = createDeferred(defaultSupplier);
    this.synchronousEntries = createDeferred(defaultSupplier);
    this.parallelScatterZipCreator = new ParallelScatterZipCreator(Executors.newFixedThreadPool(nThreads), defaultSupplier);
  }
  
  public void addArchiveEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source, boolean addInParallel) throws IOException {
    int method = zipArchiveEntry.getMethod();
    if (method == -1)
      throw new IllegalArgumentException("Method must be set on the supplied zipArchiveEntry"); 
    String zipEntryName = zipArchiveEntry.getName();
    if ("META-INF".equals(zipEntryName) || "META-INF/".equals(zipEntryName)) {
      if (zipArchiveEntry.isDirectory())
        zipArchiveEntry.setMethod(0); 
      this.metaInfDir.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source));
    } else if ("META-INF/MANIFEST.MF".equals(zipEntryName)) {
      this.manifest.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source));
    } else if (zipArchiveEntry.isDirectory() && !zipArchiveEntry.isUnixSymlink()) {
      this.directories.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, () -> Streams.EMPTY_INPUTSTREAM));
    } else if (addInParallel) {
      this.parallelScatterZipCreator.addArchiveEntry(() -> createEntry(zipArchiveEntry, source));
    } else {
      this.synchronousEntries.addArchiveEntry(createEntry(zipArchiveEntry, source));
    } 
  }
  
  public void writeTo(ZipArchiveOutputStream targetStream) throws IOException, ExecutionException, InterruptedException {
    this.metaInfDir.writeTo(targetStream);
    this.manifest.writeTo(targetStream);
    this.directories.writeTo(targetStream);
    this.synchronousEntries.writeTo(targetStream);
    this.parallelScatterZipCreator.writeTo(targetStream);
    long startAt = System.currentTimeMillis();
    targetStream.close();
    this.zipCloseElapsed = System.currentTimeMillis() - startAt;
    this.metaInfDir.close();
    this.manifest.close();
    this.directories.close();
    this.synchronousEntries.close();
  }
  
  public String getStatisticsMessage() {
    return this.parallelScatterZipCreator.getStatisticsMessage() + " Zip Close: " + this.zipCloseElapsed + "ms";
  }
  
  private ZipArchiveEntryRequest createEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier inputStreamSupplier) {
    if (this.compressAddedZips)
      return ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, inputStreamSupplier); 
    InputStream is = inputStreamSupplier.get();
    byte[] header = new byte[4];
    try {
      int read = is.read(header);
      int compressionMethod = zipArchiveEntry.getMethod();
      if (isZipHeader(header))
        compressionMethod = 0; 
      zipArchiveEntry.setMethod(compressionMethod);
      return ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, prependBytesToStream(header, read, is));
    } catch (IOException e) {
      IOUtils.closeQuietly(is);
      throw new UncheckedIOException(e);
    } 
  }
  
  private boolean isZipHeader(byte[] header) {
    return (header[0] == 80 && header[1] == 75 && header[2] == 3 && header[3] == 4);
  }
  
  private InputStreamSupplier prependBytesToStream(byte[] bytes, int len, InputStream stream) {
    return () -> (len > 0) ? new SequenceInputStream(new ByteArrayInputStream(bytes, 0, len), stream) : stream;
  }
}
