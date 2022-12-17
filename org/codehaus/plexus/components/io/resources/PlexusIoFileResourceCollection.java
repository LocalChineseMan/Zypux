package org.codehaus.plexus.components.io.resources;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.PlexusIoResourceConsumer;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

@Named("files")
public class PlexusIoFileResourceCollection extends AbstractPlexusIoResourceCollectionWithAttributes {
  public static final String ROLE_HINT = "files";
  
  private File baseDir;
  
  private boolean isFollowingSymLinks = true;
  
  private Comparator<String> filenameComparator;
  
  public PlexusIoResource resolve(PlexusIoResource resource) throws IOException {
    return resource;
  }
  
  public InputStream getInputStream(PlexusIoResource resource) throws IOException {
    return resource.getContents();
  }
  
  public String getName(PlexusIoResource resource) {
    return resource.getName();
  }
  
  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }
  
  public File getBaseDir() {
    return this.baseDir;
  }
  
  public boolean isFollowingSymLinks() {
    return this.isFollowingSymLinks;
  }
  
  public void setFollowingSymLinks(boolean pIsFollowingSymLinks) {
    this.isFollowingSymLinks = pIsFollowingSymLinks;
  }
  
  public void setDefaultAttributes(int uid, String userName, int gid, String groupName, int fileMode, int dirMode) {
    setDefaultFileAttributes(createDefaults(uid, userName, gid, groupName, fileMode));
    setDefaultDirAttributes(createDefaults(uid, userName, gid, groupName, dirMode));
  }
  
  public void setOverrideAttributes(int uid, String userName, int gid, String groupName, int fileMode, int dirMode) {
    setOverrideFileAttributes(createDefaults(uid, userName, gid, groupName, fileMode));
    setOverrideDirAttributes(createDefaults(uid, userName, gid, groupName, dirMode));
  }
  
  private static PlexusIoResourceAttributes createDefaults(int uid, String userName, int gid, String groupName, int mode) {
    return (PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(uid), userName, Integer.valueOf(gid), groupName, 
        (mode >= 0) ? mode : -1);
  }
  
  public void setPrefix(String prefix) {
    char nonSeparator = (File.separatorChar == '/') ? '\\' : '/';
    super.setPrefix(StringUtils.replace(prefix, nonSeparator, File.separatorChar));
  }
  
  private void addResources(List<PlexusIoResource> result, String[] resources) throws IOException {
    File dir = getBaseDir();
    for (String name : resources) {
      String sourceDir = name.replace('\\', '/');
      File f = new File(dir, sourceDir);
      FileAttributes fattrs = new FileAttributes(f);
      PlexusIoResourceAttributes attrs = mergeAttributes((PlexusIoResourceAttributes)fattrs, fattrs.isDirectory());
      String remappedName = getName(name);
      PlexusIoResource resource = ResourceFactory.createResource(f, remappedName, null, getStreamTransformer(), attrs);
      if (isSelected(resource))
        result.add(resource); 
    } 
  }
  
  public Stream stream() {
    return new Stream() {
        public void forEach(PlexusIoResourceConsumer resourceConsumer) throws IOException {
          Iterator<PlexusIoResource> resources = PlexusIoFileResourceCollection.this.getResources();
          while (resources.hasNext()) {
            PlexusIoResource next = resources.next();
            if (PlexusIoFileResourceCollection.this.isSelected(next))
              resourceConsumer.accept(next); 
          } 
          if (resources instanceof Closeable)
            ((Closeable)resources).close(); 
        }
        
        public void forEach(ExecutorService es, final PlexusIoResourceConsumer resourceConsumer) throws IOException {
          Iterator<PlexusIoResource> resources = PlexusIoFileResourceCollection.this.getResources();
          while (resources.hasNext()) {
            final PlexusIoResource next = resources.next();
            Callable<?> future = new Callable() {
                public Object call() throws Exception {
                  resourceConsumer.accept(next);
                  return this;
                }
              };
            es.submit(future);
          } 
          if (resources instanceof Closeable)
            ((Closeable)resources).close(); 
        }
      };
  }
  
  public Iterator<PlexusIoResource> getResources() throws IOException {
    DirectoryScanner ds = new DirectoryScanner();
    File dir = getBaseDir();
    ds.setBasedir(dir);
    String[] inc = getIncludes();
    if (inc != null && inc.length > 0)
      ds.setIncludes(inc); 
    String[] exc = getExcludes();
    if (exc != null && exc.length > 0)
      ds.setExcludes(exc); 
    if (isUsingDefaultExcludes())
      ds.addDefaultExcludes(); 
    ds.setCaseSensitive(isCaseSensitive());
    ds.setFollowSymlinks(isFollowingSymLinks());
    ds.setFilenameComparator(this.filenameComparator);
    ds.scan();
    List<PlexusIoResource> result = new ArrayList<>();
    if (isIncludingEmptyDirectories()) {
      String[] dirs = ds.getIncludedDirectories();
      addResources(result, dirs);
    } 
    String[] files = ds.getIncludedFiles();
    addResources(result, files);
    return result.iterator();
  }
  
  public boolean isConcurrentAccessSupported() {
    return true;
  }
  
  public void setFilenameComparator(Comparator<String> filenameComparator) {
    this.filenameComparator = filenameComparator;
  }
}
