package wyclipse.builder;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

import wyc.Compiler;
import wyc.Pipeline;
import wyil.ModuleLoader;
import wyil.Transform;
import wyil.util.SyntaxError;
import wyjc.io.ClassFileLoader;
import wyjc.transforms.ClassWriter;

public class Builder extends IncrementalProjectBuilder {
	private static final boolean verbose = false;
	private static final String WYRT_PATH = "lib/wyrt.jar";

	private ArrayList<String> WHILEYPATH;	
	private ClassFileLoader classFileLoader;
	private ModuleLoader moduleLoader;
	private List<Transform> compilerStages;
	private Compiler compiler;
	
	public Builder() {
		initialiseWhileyPath();
		initialiseCompiler();
	}
	
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
	
	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {		
		ArrayList<IResource> resources = identifyChangedResources(delta);
		clearMarkers(resources);	
		compile(identifyCompileableResources(resources));		
	}		
	
	protected void initialiseWhileyPath() {
		this.WHILEYPATH = new ArrayList<String>();
		// Determine the BOOTHPATH (the path to the Whiley Runtime Jar, wyrt.jar)
		try {
			Bundle bundle = Platform.getBundle("wyclipse");
			URL fileURL = FileLocator.resolve(bundle.getEntry(WYRT_PATH));
			String BOOTPATH = fileURL.getPath();
			this.WHILEYPATH.add(BOOTPATH);
		} catch(IOException e) {
			// hmmm, what should I do here?
		}
						
	}
	
	protected void initialiseCompiler() {		
		this.classFileLoader = new ClassFileLoader();		
		this.moduleLoader = new ModuleLoader(WHILEYPATH, classFileLoader);
		ArrayList<Pipeline.Template> templates = new ArrayList(Pipeline.defaultPipeline);
		templates.add(new Pipeline.Template(ClassWriter.class,Collections.EMPTY_MAP));
		Pipeline pipeline = new Pipeline(templates, moduleLoader);		
		compilerStages = pipeline.instantiate();
		compiler = new Compiler(moduleLoader,compilerStages);
		if(verbose) {
			compiler.setLogOut(System.err);
		}
		moduleLoader.setLogger(compiler);		
	}
	
	protected void compile(List<IFile> compileableResources) throws CoreException {
		HashMap<String,IFile> resourceMap = new HashMap<String,IFile>();
		try {
			ArrayList<File> files = new ArrayList<File>();
			for(IFile resource : compileableResources) {
				File file = resource.getRawLocation().toFile();
				files.add(file);
				resourceMap.put(file.getAbsolutePath(), resource);
			}
			compiler.compile(files);
		} catch(SyntaxError e) {				
			IFile resource = resourceMap.get(e.filename());
			if(resource != null) { 
				highlightSyntaxError(resource,e);
			}
			System.out.println("SYNTAX ERROR: " + e);
		} catch(IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This simply recurses the delta and strips out the resources which have
	 * changed.
	 * 
	 * @param delta
	 * @return
	 */
	protected ArrayList<IResource> identifyChangedResources(IResourceDelta delta) {
		final ArrayList<IResource> files = new ArrayList<IResource>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					IResource resource = delta.getResource();
					if(resource != null) {						
						files.add(resource);
					}
					return true; // visit children as well.
				}
			});
		} catch(CoreException e) {
			e.printStackTrace();
		}	
		return files;
	}
	
	/**
	 * Remove all markers on those resources to be compiled.
	 * @param resources
	 * @throws CoreException
	 */
	protected void clearMarkers(List<IResource> resources) throws CoreException {
		for (IResource resource : resources) {
			resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		}
	}
	
	/**
	 * Identify those resources which have changed, and which are allowed to be
	 * compiled. Resources which cannot be compiled include those which are not
	 * source files, or are not located in a designated source folder.
	 * 
	 * @param resources
	 * @return
	 */
	protected ArrayList<IFile> identifyCompileableResources(List<IResource> resources) {
		ArrayList<IFile> files = new ArrayList<IFile>();
		for (IResource resource : resources) {
			if (resource.getType() == IResource.FILE
					&& resource.getFileExtension().equals("whiley")) {
				files.add((IFile)resource);
			}
		}
		return files;
	}
	
	protected void highlightSyntaxError(IResource resource, SyntaxError err)
			throws CoreException {
		IMarker m = resource.createMarker(IMarker.PROBLEM);
		m.setAttribute(IMarker.CHAR_START, err.start());
		m.setAttribute(IMarker.CHAR_END, err.end()+1);
		m.setAttribute(IMarker.MESSAGE, err.msg());
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}	
}
