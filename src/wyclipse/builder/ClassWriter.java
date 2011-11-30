// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyclipse.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.*;
import wyjc.io.ClassFileBuilder;
import wyjvm.io.ClassFileWriter;
import wyjvm.lang.ClassFile;

public class ClassWriter implements Transform {
	private ClassFileBuilder classBuilder;
	private IContainer outputDirectory = null;
	
	public ClassWriter(ModuleLoader loader, IContainer outputDirectory) {
		classBuilder = new ClassFileBuilder(loader, wyjc.Main.MAJOR_VERSION,
				wyjc.Main.MINOR_VERSION);
		this.outputDirectory = outputDirectory;
	}	
		
	public void apply(Module m) throws IOException {		
		ClassFile cfile = classBuilder.build(m);						
		// calculate filename
		String filename = m.id().fileName().replace('.', File.separatorChar);
		IFile file = outputDirectory.getFile(new Path(filename)
				.addFileExtension(SuffixConstants.EXTENSION_class));		
		try {
			writeClassFileContents(cfile,file);
		} catch(CoreException e) {
			// ouch.
		}
	}	
	
	private void writeClassFileContents(ClassFile clazz, IFile file) throws CoreException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		ClassFileWriter writer = new ClassFileWriter(out,null);			
		writer.write(clazz);
		out.flush();
		byte[] contents = out.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(contents);
		file.create(input, IResource.FORCE | IResource.DERIVED, null);		
	}
}
