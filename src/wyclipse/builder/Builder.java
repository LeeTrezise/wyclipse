package wyclipse.builder;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.osgi.framework.Bundle;

import wyc.Compiler;
import wyc.NameResolver;
import wyc.Pipeline;
import wyc.Pipeline.Template;
import wyc.stages.BackPropagation;
import wyc.stages.TypePropagation;
import wyclipse.Activator;
import wyil.io.WyilFileWriter;
import wyil.path.*;
import wyil.path.Path;
import wyil.ModuleLoader;
import wyil.Transform;
import wyil.transforms.CoercionCheck;
import wyil.transforms.ConstraintInline;
import wyil.transforms.DeadCodeElimination;
import wyil.transforms.DefiniteAssignment;
import wyil.transforms.LiveVariablesAnalysis;
import wyil.transforms.ModuleCheck;
import wyil.util.SyntaxError;
import wyjc.io.ClassFileLoader;

public class Builder extends IncrementalProjectBuilder {
	private static final boolean verbose = false;

	private NameResolver nameResolver;
	private List<Transform> compilerStages;
	private Compiler compiler;

	public Builder() {
		
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if(compiler == null) {
			initialiseCompiler();
		}
		
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD
				|| kind == IncrementalProjectBuilder.AUTO_BUILD) {
			IResourceDelta delta = getDelta(getProject());			
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void fullBuild(IProgressMonitor monitor) throws CoreException {
		// Force a complete reinitialisation of the compiler. This is necessary
		// in case things changed, such as the CLASSPATH, etc.		
		initialiseCompiler();
					
		ArrayList<IFile> sourceFiles = identifyAllCompileableResources(); 
		clearSourceFileMarkers(sourceFiles);
				
		compile(sourceFiles);
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		ArrayList<IResource> resources = identifyChangedResources(delta);
		
		// First, check whether any important resources have changed (e.g.
		// classpath). If so, then we need to reinitialise the compiler
		// accordingly.
		for(IResource resource : resources) {
			if(isClassPath(resource)) {				
				fullBuild(monitor);
				return;
			}
		}
		
		ArrayList<IFile> sourceFiles = identifyCompileableResources(resources); 
		clearSourceFileMarkers(sourceFiles);
		
		compile(sourceFiles);
	}
	
	/**
	 * Clean all derived files from this project.
	 */
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);

		// =====================================================
		// first, delete everything in the default output folder
		// =====================================================
		
		IPath defaultOutputLocation = javaProject.getOutputLocation();
		IFolder defaultOutputContainer = workspaceRoot
				.getFolder(defaultOutputLocation);

		if (defaultOutputContainer != null) {
			ArrayList<IFile> files = new ArrayList<IFile>();
			addMatchingFiles(defaultOutputContainer, "class", files);
			for (IFile file : files) {
				file.delete(true, monitor);
			}
		}

		// ========================================================
		// second, delete everything in the specific output folders.
		// ========================================================

		// TODO

	}

	protected void initialisePaths(ArrayList<Path.Root> whileypath,
			ArrayList<Path.Root> sourcepath, IContainer outputDirectory)
			throws CoreException {
		IProject project = (IProject) getProject();	
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IBinaryRoot bindir;
		
		if(outputDirectory != null) {
			bindir = new IBinaryRoot(outputDirectory);
		} else {
			bindir = null;
		}
		
		if (javaProject != null) {
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				switch (e.getEntryKind()) {
				case IClasspathEntry.CPE_LIBRARY: {
					IPath location = e.getPath();
					try {
						whileypath.add(new JarFileRoot(location.toOSString()));
					} catch (IOException ex) {
						// ignore entries which don't exist
					}
					break;
				}
				case IClasspathEntry.CPE_SOURCE: {
					IPath location = e.getPath();
					IFolder folder = workspaceRoot.getFolder(location);
					sourcepath.add(new ISourceRoot(folder, bindir));
					break;
				}
				case IClasspathEntry.CPE_CONTAINER:
					System.out.println("ADDING CONTAINER?: " + e.getPath());
					break;
				}
			}
		}
	}

	protected void initialiseCompiler() throws CoreException {		
		IProject project = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);

		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();		

		// =========================================================
		// Initialise whiley and source paths
		// =========================================================
		IContainer outputDirectory = workspaceRoot.getFolder(javaProject.getOutputLocation());		
		ArrayList<Path.Root> whileypath = new ArrayList<Path.Root>();
		ArrayList<Path.Root> sourcepath = new ArrayList<Path.Root>();
		initialisePaths(whileypath,sourcepath,outputDirectory);
		
		// =========================================================
		// Construct name resolver
		// =========================================================
		
		this.nameResolver = new NameResolver(sourcepath,whileypath);
		this.nameResolver.setModuleReader("class",new ClassFileLoader());
		
		// =========================================================
		// Construct and configure pipeline
		// =========================================================
				
		compilerStages = new ArrayList<Transform>();
		compilerStages.add(new TypePropagation(nameResolver));					
		compilerStages.add(new DefiniteAssignment(nameResolver));
		compilerStages.add(new ModuleCheck(nameResolver));							
		compilerStages.add(new BackPropagation(nameResolver));		
		compilerStages.add(new CoercionCheck(nameResolver));
		compilerStages.add(new DeadCodeElimination(nameResolver));
		compilerStages.add(new LiveVariablesAnalysis(nameResolver));
		compilerStages.add(new ClassWriter(nameResolver, outputDirectory));
		
		// =========================================================
		// Construct and configure the compiler
		// =========================================================
				
		compiler = new Compiler(nameResolver, compilerStages);
		if (verbose) {
			compiler.setLogOut(System.err);
		}
		nameResolver.setLogger(compiler);
	}

	protected void compile(List<IFile> compileableResources)
			throws CoreException {

		HashMap<String, IFile> resourceMap = new HashMap<String, IFile>();
		try {
			ArrayList<File> files = new ArrayList<File>();
			for (IFile resource : compileableResources) {
				File file = resource.getLocation().toFile();
				files.add(file);				
				resourceMap.put(file.getAbsolutePath(), resource);
			}
						
			compiler.compile(files);			
		} catch (SyntaxError e) {
			IFile resource = resourceMap.get(e.filename());
			highlightSyntaxError(resource, e);
		} catch (Exception e) {			
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
					if (resource != null) {
						files.add(resource);
					}
					return true; // visit children as well.
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * Identify those resources which have changed, and which are allowed to be
	 * compiled. Resources which cannot be compiled include those which are not
	 * source files, or are not located in a designated source folder.
	 * 
	 * @param resources
	 * @return
	 */
	protected ArrayList<IFile> identifyCompileableResources(
			List<IResource> resources) throws CoreException {
		
		// First, identify source folders		
		
		IProject project = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);		
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		// FIXME: the following information about source folders could be
		// extracted from the compiler?
		
		ArrayList<IPath> sourceFolders = new ArrayList<IPath>(); 
				
		if (javaProject != null) {			
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				switch (e.getEntryKind()) {
					case IClasspathEntry.CPE_SOURCE :			
						IFolder folder = workspaceRoot.getFolder(e.getPath());						
						sourceFolders.add(folder.getLocation());
						break;					
				}
			}
		}
		
		ArrayList<IFile> files = new ArrayList<IFile>();
		for (IResource resource : resources) {
			if (resource.getType() == IResource.FILE
					&& resource.getFileExtension().equals("whiley")
					&& containedInFolders(resource.getLocation(), sourceFolders)) {
				files.add((IFile) resource);
			}
		}
		return files;
	}

	/**
	 * Identify those resources which have changed, and which are allowed to be
	 * compiled. Resources which cannot be compiled include those which are not
	 * source files, or are not located in a designated source folder.
	 * 
	 * @param resources
	 * @return
	 */
	protected ArrayList<IFile> identifyAllCompileableResources()
			throws CoreException {
		
		// First, identify source folders		
		
		IProject project = (IProject) getProject();
		IJavaProject javaProject = (IJavaProject) project
				.getNature(JavaCore.NATURE_ID);		
		IWorkspace workspace = project.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		
		ArrayList<IContainer> sourceFolders = new ArrayList<IContainer>(); 
				
		if (javaProject != null) {			
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				switch (e.getEntryKind()) {
					case IClasspathEntry.CPE_SOURCE :			
						IFolder folder = workspaceRoot.getFolder(e.getPath());						
						sourceFolders.add(folder);
						break;					
				}
			}
		}
		
		ArrayList<IFile> files = new ArrayList<IFile>();
		for(IContainer root : sourceFolders) {		
			addMatchingFiles(root,"whiley",files);							
		}
		
		return files;
	}
	
	protected boolean containedInFolders(IPath path,
			ArrayList<IPath> folders) {
		for(IPath folder : folders) {			
			if(folder.isPrefixOf(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all markers on those resources to be compiled. It is assumed that
	 * those resources supplied are only whiley source files.
	 * 
	 * @param resources
	 * @throws CoreException
	 */
	protected void clearSourceFileMarkers(List<IFile> resources) throws CoreException {
		for (IResource resource : resources) {			
			resource.deleteMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);			
		}
	}	
	
	protected void highlightSyntaxError(IResource resource, SyntaxError err)
			throws CoreException {
		// IMarker m = resource.createMarker(IMarker.PROBLEM);
		//IMarker m = resource.createMarker(Activator.WYCLIPSE_MARKER_ID);
		IMarker m = resource.createMarker("wyclipse.whileymarker");
		m.setAttribute(IMarker.CHAR_START, err.start());
		m.setAttribute(IMarker.CHAR_END, err.end() + 1);
		m.setAttribute(IMarker.MESSAGE, err.msg());
		m.setAttribute(IMarker.LOCATION, "Whiley File");
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	
		
	}
	
	private static boolean isClassPath(IResource resource) {
		return resource instanceof IFile && resource.getName().equals(".classpath");
	}
	
	private static void addMatchingFiles(IContainer resource,
			final String extension, final ArrayList<IFile> files) {
		IResourceVisitor visitor = new IResourceVisitor() {
			public boolean visit(IResource resource) {
				if (resource.getType() == IResource.FILE
						&& resource.getFileExtension().equals(extension)) {
					files.add((IFile) resource);
				}
				return true; // visit children as well.
			}
		};
	}
}
