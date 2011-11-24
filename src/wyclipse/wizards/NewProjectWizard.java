package wyclipse.wizards;

import java.util.Arrays;

import org.eclipse.core.runtime.*;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IJavaElement;
// Should not extend this, but seems that I can!
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import wyclipse.Activator;

/**
 * This Wizard is responsible for constructing a new Whiley project. In fact, it
 * basically reuses everything from the existing JDT new project wizard, with
 * the minor addition of appending the Whiley Nature and Builder onto the
 * project.
 * 
 * @author djp
 * 
 */
public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {
	private NewJavaProjectWizardPageOne page1;
	private NewJavaProjectWizardPageTwo page2;
	private IConfigurationElement configElement;	
	
	/**
	 * Constructor for WhileyModuleNewWizard.
	 */
	public NewProjectWizard() {
		super();
		setWindowTitle("New Whiley Project");
	}
	
	/**
	 * Add pages to the wizard.
	 */
	public void addPages() {
		page1 = new NewJavaProjectWizardPageOne();
		page2 = new NewJavaProjectWizardPageTwo(page1);
		page1.setTitle("Create a Whiley Project");
		page2.setTitle("Settings");
		page2.setDescription("Define the build settings");
		addPage(page1);
		addPage(page2);
	}
	
	public IJavaElement getCreatedElement() {
		return page2.getJavaProject();
	}

	public void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		page2.performFinish(monitor);
	}
	
	public void setInitializationData(IConfigurationElement config,
			String property, Object data) {
		configElement = config;
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard.
	 */
	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if(result) {
			// Project construction succeeded. Now, we need to add the Whiley
			// nature to the new project, in order that we can build Whiley
			// files!
			try {
				IProject project = page2.getJavaProject().getProject();
				IProjectDescription desc = project.getDescription();
				
				// First, append Whiley Nature onto list of natures
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = Arrays.copyOf(oldNatures, oldNatures.length+1);
				newNatures[oldNatures.length] = Activator.WYCLIPSE_NATURE_ID;							
				desc.setNatureIds(newNatures);

				// Second, append Whiley Builder onto list of builders
				ICommand[] oldBuilders = desc.getBuildSpec();
				ICommand[] newBuilders = Arrays.copyOf(oldBuilders, oldBuilders.length+1);
				ICommand buildCommand = desc.newCommand();
				// TODO: is the ordering here important? We definitely want wyjc
				// to run first.
				buildCommand.setBuilderName(Activator.WYCLIPSE_BUILDER_ID);
				newBuilders[oldBuilders.length] = buildCommand;
				desc.setBuildSpec(newBuilders);		
				
				// done
				project.setDescription(desc,null);
				BasicNewProjectResourceWizard.updatePerspective(configElement);
				
			} catch(CoreException e) {
				return false; // I guess??
			}
		}
		
		return result;
	}
	
	/**
	 * This method is called when 'Cancel' button is pressed in the wizard. In
	 * this case, we need to undo what has been done.
	 */	
	@Override
    public boolean performCancel() {
        page2.performCancel();
        return super.performCancel();
    }

	
}