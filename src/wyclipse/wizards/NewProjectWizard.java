package wyclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import wyclipse.Activator;

public class NewProjectWizard extends Wizard implements INewWizard {
	private NewProjectPage page;
	private ISelection selection;

	/**
	 * Constructor for WhileyModuleNewWizard.
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewProjectPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String projectName = page.getProjectName();
		final String location = page.getLocation();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					IProject project = createProject(projectName, location, monitor);
					setProjectDescription(project, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private IProject createProject(String projectName, String location,
			IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + location + " / " + projectName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if(!project.exists()) {
			project.create(monitor);
			project.open(monitor);			
		} else {
			MessageDialog.openError(getShell(), "Project already exists", "");
		}
		monitor.worked(1);	
		return project;
	}
	
	private void setProjectDescription(IProject project,
			IProgressMonitor monitor) throws CoreException {
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { Activator.WYCLIPSE_NATURE_ID });
		ICommand buildCommand = desc.newCommand();
		buildCommand.setBuilderName(Activator.WYCLIPSE_BUILDER_ID);
		desc.setBuildSpec(new ICommand[] { buildCommand });		
		project.setDescription(desc, monitor);
	}
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}