package wyclipse.launchers;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;


public class WhileyLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject();
		//IJavaProject jprog = (IJavaProject) proj;
		TreeSelection tree = (TreeSelection) selection;
		IStructuredSelection struct = (IStructuredSelection) selection;
		//Struct gives the file that needs to be executeed. 
		File f = (File)struct.toArray()[0];
		//System.out.println(jprog);
		//IType t =(IType) jprog.getJavaProject();
		//TODO Still working on.
		
		
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub
		
	}
	protected void launch(IType type, String mode) {
        try {
          ILaunchConfiguration config  = null; //TODO
            if (config != null) {
             config.launch(mode, null);
            }
        } catch (CoreException e) {
            /* Handle exceptions*/
        }
    }
	
	
	
}