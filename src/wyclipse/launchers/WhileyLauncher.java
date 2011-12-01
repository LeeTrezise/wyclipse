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
	   
