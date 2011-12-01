package wyclipse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

import wyclipse.builder.WhileyClasspathContainer;

public class WhileyCore {
	
	private static final String WHILEY_RUNTIME_JAR = "wyrt.jar";
	
	/**
	 * Add the Whiley Classpath Container to a given project.
	 * 
	 * @param javaProject
	 */
	public static void addWhileyClasspathContainer(IJavaProject javaProject) throws CoreException {
		IClasspathEntry containerEntry = JavaCore.newContainerEntry(
				WhileyClasspathContainer.CONTAINER_PATH);
		addClassPathEntry(javaProject, containerEntry);        
    }

    /**
     * Adds a classpath entry to the end of a project's classpath
     * 
     * @param project
     *            The project to add the entry to.
     * @param entry
     *            The classpath entry to add.
     */
    public static void addClassPathEntry(IJavaProject project,
            IClasspathEntry entry) throws JavaModelException {
    	IClasspathEntry[] oldEntries = project.getRawClasspath();
        IClasspathEntry[] newEntries = Arrays.copyOf(oldEntries,oldEntries.length+1); 
        newEntries[oldEntries.length] = entry;
        project.setRawClasspath(newEntries, null);
    }

	public static IPath getWhileyRuntimeJar() throws IOException {
    	Bundle whileyBundle = Platform.getBundle("wyclipse");
    	URL location = whileyBundle.getEntry("lib/" + WHILEY_RUNTIME_JAR);    	
    	location = FileLocator.resolve(location);
    	try {
    		return URIUtil.toPath(location.toURI());
    	} catch(URISyntaxException e) {
    		// should be impossible, right?
    		return null;
    	}
    }
}
