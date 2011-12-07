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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import wyclipse.builder.IBinaryRoot.IEntry;
import wyil.util.path.Path.*;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public class ISourceRoot implements Root {
	
	private final IContainer dir;	
	private final IBinaryRoot bindir;
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 * @throws IOException
	 */
	public ISourceRoot(IContainer dir, IBinaryRoot bindir) {
		this.dir = dir;			
		this.bindir = bindir;
	}
			
	public boolean exists(PkgID pkg) {
		String pkgname = pkg.toString().replace('.', '/');
		return dir.exists(new Path(pkgname));
	}
	
	public List<Entry> list(PkgID pkg) throws CoreException {
		Path path = new Path(pkg.toString().replace('.','/'));
		IResource member = dir.findMember(path);
		
		if (member.exists() && member instanceof IContainer) {
			IContainer container = (IContainer) member;
			ArrayList<Entry> entries = new ArrayList<Entry>();

			for (IResource file : container.members()) {
				if (file instanceof IFile
						&& file.getFileExtension().equals("whiley")) {
					String name = file.getName();
					ModuleID mid = new ModuleID(pkg, name);
					Entry srcEntry = new IEntry(mid, (IFile) file);
					Entry binEntry = null;

					// Now, see if there exists a binary version of this file
					// which has
					// a modification date no earlier. Binary files are always
					// preferred
					// over source entries.

					if (bindir != null) {
						binEntry = bindir.lookup(mid);
					} else {
						IPath bpath = file.getFullPath().removeFileExtension()
								.addFileExtension("class");
						IResource binFile = dir.findMember(bpath);
						if (binFile.exists() && binFile instanceof IFile) {
							binEntry = new IEntry(mid, (IFile) binFile);
						}
					}

					if (binEntry != null
							&& binEntry.lastModified() >= srcEntry
									.lastModified()) {
						entries.add(binEntry);
					} else {
						entries.add(srcEntry);
					}
				}
			}

			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	public Entry lookup(ModuleID mid) throws Exception {
		IPath path = new Path(mid.toString().replace('.', '/')).addFileExtension("whiley");
		IResource srcFile = dir.findMember(path);

		if (srcFile.exists() && srcFile instanceof IFile) {
			Entry srcEntry = new IEntry(mid, (IFile) srcFile);
			Entry binEntry = null;
			
			// Now, see if there exists a binary version of this file which has
			// a modification date no earlier. Binary files are always preferred
			// over source entries.
			
			if (bindir != null) {
				binEntry = bindir.lookup(mid);					
			} else {
				path = path.removeFileExtension().addFileExtension("class");
				IResource binFile = dir.findMember(path);				
				if(binFile.exists() && binFile instanceof IFile) {
					binEntry = new IEntry(mid,(IFile) binFile);
				}
			}
			
			if (binEntry != null && binEntry.lastModified() >= srcEntry.lastModified()) {
				return binEntry;
			} else {
				return srcEntry;
			}
		} else {
			return null; // not found
		}		
	}

	public String toString() {
		return dir.toString();
	}
}
