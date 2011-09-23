package wyclipse.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import wyc.Compiler;
import wyc.Pipeline;
import wyil.ModuleLoader;
import wyil.Transform;
import wyil.util.SyntaxError;
import wyjc.io.ClassFileLoader;
import wyjc.transforms.ClassWriter;

public class Builder extends IncrementalProjectBuilder {
		
	private ClassFileLoader classFileLoader;
	private ModuleLoader moduleLoader;
	private List<Transform> compilerStages;
	private Compiler compiler;
	
	private String BOOTPATH = "lib" + File.separatorChar + "wyrt.jar";
	private ArrayList<String> WHILEYPATH;
	
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
	
	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {		
		ArrayList<File> deltaFiles = identifyChangedFiles(delta);
		try {
			compiler.compile(deltaFiles);
		} catch(SyntaxError e) {
			System.out.println("SYNTAX ERROR: " + e);
		} catch(IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This simply compiles the given list of files using the Whiley-to-Java
	 * compiler.
	 * 
	 * @param files
	 * @param options
	 */
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
	
	protected void initialiseWhileyPath() {
		this.WHILEYPATH = new ArrayList<String>();
		this.WHILEYPATH.add(BOOTPATH);
	}
	
	protected void initialiseCompiler() {		
		this.classFileLoader = new ClassFileLoader();		
		this.moduleLoader = new ModuleLoader(WHILEYPATH, classFileLoader);
		ArrayList<Pipeline.Template> templates = new ArrayList(Pipeline.defaultPipeline);
		templates.add(new Pipeline.Template(ClassWriter.class,Collections.EMPTY_MAP));
		Pipeline pipeline = new Pipeline(templates, moduleLoader);		
		compilerStages = pipeline.instantiate();
		compiler = new Compiler(moduleLoader,compilerStages);		
		moduleLoader.setLogger(compiler);		
	}
	
	/**
	 * This simply recurses the delta and strips out the files which have
	 * changed, and that can be recompiled.
	 * 
	 * @param delta
	 * @return
	 */
	protected ArrayList<File> identifyChangedFiles(IResourceDelta delta) {
		final ArrayList<File> files = new ArrayList<File>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					IPath file = delta.getResource().getRawLocation();
					if(file != null) {						
						files.add(file.toFile());
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
