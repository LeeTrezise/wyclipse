package wyclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class Builder extends IncrementalProjectBuilder {
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if(kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else if(kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
			IResourceDelta delta = getDelta(getProject());
			if(delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta,monitor);
			}
		}
		return null;
	}
	
	protected void fullBuild(IProgressMonitor monitor) {
		System.out.println("Builder.fullBuilder called");
	}
	
	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		System.out.println("Builder.incrementalBuilder called on " + delta);
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					System.out.println("Changed: " + delta.getResource().getRawLocation());
					return true; // visit children as well.
				}
			});
		} catch(CoreException e) {
			e.printStackTrace();
		}		
	}
}
