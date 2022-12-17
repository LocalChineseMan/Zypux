package org.codehaus.plexus.archiver.ear;

import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.zip.ConcurrentJarCreator;

@Named("ear")
public class EarArchiver extends JarArchiver {
  private File deploymentDescriptor;
  
  private boolean descriptorAdded;
  
  public void setAppxml(File descr) throws ArchiverException {
    this.deploymentDescriptor = descr;
    if (!this.deploymentDescriptor.exists())
      throw new ArchiverException("Deployment descriptor: " + this.deploymentDescriptor + " does not exist."); 
    addFile(descr, "META-INF/application.xml");
  }
  
  public void addArchive(File fileName) throws ArchiverException {
    addDirectory(fileName.getParentFile(), "/", new String[] { fileName
          
          .getName() }, null);
  }
  
  public void addArchives(File directoryName, String[] includes, String[] excludes) throws ArchiverException {
    addDirectory(directoryName, "/", includes, excludes);
  }
  
  protected void initZipOutputStream(ConcurrentJarCreator zOut) throws ArchiverException, IOException {
    if (this.deploymentDescriptor == null && !isInUpdateMode())
      throw new ArchiverException("appxml attribute is required"); 
    super.initZipOutputStream(zOut);
  }
  
  protected void zipFile(ArchiveEntry entry, ConcurrentJarCreator zOut, String vPath, int mode) throws IOException, ArchiverException {
    if (vPath.equalsIgnoreCase("META-INF/application.xml")) {
      if (this.deploymentDescriptor == null || 
        !ResourceUtils.isCanonicalizedSame(entry.getResource(), this.deploymentDescriptor) || this.descriptorAdded) {
        getLogger().warn("Warning: selected " + this.archiveType + " files include a META-INF/application.xml which will be ignored (please use appxml attribute to " + this.archiveType + " task)");
      } else {
        zipFile(entry, zOut, vPath);
        this.descriptorAdded = true;
      } 
    } else {
      zipFile(entry, zOut, vPath);
    } 
  }
  
  protected void cleanUp() throws IOException {
    this.descriptorAdded = false;
    super.cleanUp();
  }
}
