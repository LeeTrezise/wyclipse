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
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = Arrays.copyOf(oldEntries,oldEntries.length+1); 
        newEntries[oldEntries.length] = containerEntry;
        javaProject.setRawClasspath(newEntries, null);
    }

	/**
	 * Find the location of the Whiley Runtime Jar (wyrt.jar). This is shipped
	 * with wyclipse, so should be located in the lib/ folder of its bundle.
	 * 
	 * @return
	 * @throws IOException
	 */
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
