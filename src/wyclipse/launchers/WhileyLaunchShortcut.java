package wyclipse.launchers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;



public class WhileyLaunchShortcut extends
        JavaApplicationLaunchShortcut {
 
    public static final String WHILEY_LAUNCH_ID = "wyclipse.launchers.WhileyLauncher"; //$NON-NLS-1$
 
    public static final String WHILEY_FILE_EXTENSION = "whiley"; //$NON-NLS-1$
 
    public static final String JAVA_FILE_EXTENSION = "java"; //$NON-NLS-1$. 
     
    
 
    /**
     *
     * @param objects
     *            selected objects
     * @return corresponding Java elements
     */
 

    /**
     * Returns the AspectJ launch config type
     */
    protected static ILaunchConfigurationType getAJConfigurationType() {
        return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(WHILEY_LAUNCH_ID);
   }

    protected ILaunchConfiguration createConfiguration(IType type) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
        try {
            ILaunchConfigurationType configType = getAJConfigurationType();
           wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(type.getElementName()));
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, type.getJavaProject().getElementName());
            config = wc.doSave();
        } catch (CoreException exception) {
            reportErorr(exception);    
       }
        return config;
    }
 
    /**
     * Returns the singleton launch manager.
    *
    * @return launch manager
    */
    private ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }
 
    /**
     * Opens an error dialog on the given exception.
     *
     * @param exception
     */
    protected void reportErorr(CoreException exception) {
        MessageDialog.openError(getShell(), LauncherMessages.JavaLaunchShortcut_3, exception.getStatus().getMessage()); 
    }
 
     
}
   

