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

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

import wyclipse.WhileyCore;

/**
 * Holds the necessary runtime library files for compiling and running Whiley
 * files (the Whiley standard library).
 * 
 * @author djp
 * 
 */
public class WhileyClasspathContainer implements IClasspathContainer {	
	public static final Path CONTAINER_PATH = new Path("wyclipse.WHILEY_CONTAINER");
	
	private IClasspathEntry[] entries;
	private IProject project;
	
	public WhileyClasspathContainer(IProject project) {
		this.project = project;
	}
	
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		if (entries == null) {
			try {
				IPath wyrtPath = wyclipse.Activator.WHILEY_RUNTIME_JAR_IPATH;
				IClasspathAttribute[] extraAttributes = new IClasspathAttribute[0];
				IClasspathEntry entry = JavaCore.newLibraryEntry(wyrtPath,
						null, null, null, extraAttributes, false);
				entries = new IClasspathEntry[]{entry};
			} catch(Exception e) {
				// could not find runtime jar, so just leave off the class path
				entries = new IClasspathEntry[0];
			} 
		}
        return entries;
    }

	@Override
	public String getDescription() {
        return "Whiley System Library";
    }

	@Override
    public int getKind() {
        return K_APPLICATION;
    }

	@Override
    public IPath getPath() {
        return CONTAINER_PATH;
    }

}
