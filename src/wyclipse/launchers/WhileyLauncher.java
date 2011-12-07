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

package wyclipse.launchers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.osgi.framework.Bundle;

import wyclipse.Activator;

public class WhileyLauncher extends JavaLaunchDelegate {

	private static final String WHILEY_RUNTIME_JAR = "wyrt.jar";
	public static ILaunchConfiguration conf;
	/**
	 * The main objective here is to add the Whiley runtime onto the classpath.
	 */
	@Override	
	
	public String[] getClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		String[] classpath = super.getClasspath(configuration);
		String[] newClasspath = Arrays.copyOf(classpath, classpath.length + 1);
		// FIXME: do we really need this?
		Bundle whileyBundle = Platform.getBundle("wyclipse");
		Enumeration<URL> enu = whileyBundle.findEntries("lib",
				WHILEY_RUNTIME_JAR, false);
		if (enu != null && enu.hasMoreElements()) {
			try {
				URL jar = FileLocator.resolve(enu.nextElement());
				newClasspath[classpath.length] = jar.getFile();
			} catch (IOException e) {
				throw new CoreException(
						new Status(Status.ERROR, Activator.WYCLIPSE_BUILDER_ID,
								"Could not find $jarName on the class path.  Please add it manually"));
			}
		} else {
			throw new CoreException(
					new Status(Status.ERROR, Activator.WYCLIPSE_BUILDER_ID,
							"Could not find $jarName on the class path.  Please add it manually"));
		}
		conf = configuration;
		return newClasspath;
	}	
	public ILaunchManager getManager() {
		return getLaunchManager();
	}
}
	   
