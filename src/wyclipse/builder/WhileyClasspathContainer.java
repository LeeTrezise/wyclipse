package wyclipse.builder;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import wyclipse.WhileyCore;

/**
 * Holds the necessary runtime library files for compiling and running Whiley
 * files (the Whiley standard library).
 * 
 * @author djp
 * 
 */
public class WhileyClasspathContainer implements IClasspathContainer {	
	public static final Path CONTAINER_PATH = new Path("wyclipse.WHILEY_CONTAINER");
	
	private IClasspathEntry[] entries;
	private IProject project;
	
	public WhileyClasspathContainer(IProject project) {
		this.project = project;
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		if (entries == null) {
			try {
				IPath wyrtPath = WhileyCore.getWhileyRuntimeJar();
				IClasspathAttribute[] extraAttributes = new IClasspathAttribute[0];
				IClasspathEntry entry = JavaCore.newLibraryEntry(wyrtPath,
						null, null, null, extraAttributes, false);
				entries = new IClasspathEntry[]{entry};
			} catch(IOException e) {
				// could find runtime jar, so just leave off the class path
				entries = new IClasspathEntry[0];
			} 
		}
        return entries;
    }

	@Override
	public String getDescription() {
        return "Whiley System Library";
    }

	@Override
    public int getKind() {
        return K_APPLICATION;
    }

	@Override
    public IPath getPath() {
        return CONTAINER_PATH;
    }

}
