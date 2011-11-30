package wyclipse.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.actions.WorkspaceAction;

public class WhileyNature implements IProjectNature {
	IProject project;
	@Override
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
			
		String[] newNatures = new String[natures.length+1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = "wyclipse.whileynature";
		desc.setNatureIds(newNatures);
		project.setDescription(desc, null);
		
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;

	}

}
