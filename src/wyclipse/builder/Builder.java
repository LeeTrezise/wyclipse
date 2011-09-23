package wyclipse.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import wyil.util.SyntaxError;

public class Builder extends IncrementalProjectBuilder {
	
	public static final String PATH_TO_STDLIB = "lib" + File.separatorChar + "wyrt.jar"; 
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if(kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else if(kind == IncrementalProjectBuilder.INCREMENTAL_BUILD
				|| kind == IncrementalProjectBuilder.AUTO_BUILD) {
			IResourceDelta delta = getDelta(getProject());
			if(delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta,monitor);
			}
		} else if(kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			cleanBuild(monitor);
			fullBuild(monitor);
		}
		return null;
	}
	
	protected void fullBuild(IProgressMonitor monitor) {
		//compile(files,"-bp",PATH_TO_STDLIB);
	}
	
	protected void cleanBuild(IProgressMonitor monitor) {
		System.out.println("Builder.cleanBuilder called");
	}
	
	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		System.out.println("Builder.incrementalBuilder called on " + delta);
		ArrayList<String> deltaFiles = identifyChangedFiles(delta);
		compile(deltaFiles, "-bp", PATH_TO_STDLIB);		
	}
	
	protected void compile(ArrayList<String> files, String... options) {
		String[] args = new String[files.size()+options.length];
		for(int i=0;i!=options.length;++i) {
			args[i] = options[i];
		}
		for(int i=0;i!=files.size();++i) {
			args[i+options.length] = files.get(i);
		}		
		try {
			wyjc.Main.run(args);
		} catch(SyntaxError error) {
			
		}
	}
	
	protected ArrayList<String> identifyChangedFiles(IResourceDelta delta) {
		final ArrayList<String> files = new ArrayList<String>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					IPath file = delta.getResource().getRawLocation();
					if(file != null) {
						files.add(file.toOSString());
					}
					return true; // visit children as well.
				}
			});
		} catch(CoreException e) {
			e.printStackTrace();
		}	
		return files;
	}
}
