package wyclipse.wizards;

import org.eclipse.core.runtime.*;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IJavaElement;
// Should not extend this, but seems that I can!
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;

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
	
	/**
	 * Constructor for WhileyModuleNewWizard.
	 */
	public NewProjectWizard() {
		super();
		setWindowTitle("New Whiley Project");
	}
	
	/**
	 * Adding the page to the wizard.
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
		// do nout for now
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
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
				desc.setNatureIds(new String[] { Activator.WYCLIPSE_NATURE_ID });
				ICommand buildCommand = desc.newCommand();
				buildCommand.setBuilderName(Activator.WYCLIPSE_BUILDER_ID);
				desc.setBuildSpec(new ICommand[] { buildCommand });		
				project.setDescription(desc,null);
			} catch(CoreException e) {
				return false; // I guess??
			}
		}
		return result;
	}
	
	@Override
    public boolean performCancel() {
        page2.performCancel();
        return super.performCancel();
    }
}