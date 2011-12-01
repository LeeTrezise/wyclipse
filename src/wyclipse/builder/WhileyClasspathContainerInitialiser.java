package wyclipse.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

/**
 * Holds the necessary runtime library files for compiling and running Whiley
 * files (the Whiley standard library).
 * 
 * @author djp
 * 
 */
public class WhileyClasspathContainerInitialiser extends ClasspathContainerInitializer {
	    
    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {    
        return true;
    }

    @Override
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject javaProject,
            IClasspathContainer containerSuggestion) throws CoreException {
    	// what to do?
    }

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		IClasspathContainer container = new WhileyClasspathContainer(
				project.getProject());
		JavaCore.setClasspathContainer(containerPath,
				new IJavaProject[]{project},
				new IClasspathContainer[]{container}, null);
	}
}
