package wyclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

public class NewModuleWizard extends NewElementWizard {
	private NewModuleWizardPage page;
	
	/**
	 * Constructor for WhileyModuleNewWizard.
	 */
	public NewModuleWizard() {
		super();
		setWindowTitle("Whiley Module");
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		super.addPages();
		page = new NewModuleWizardPage();
		addPage(page);
		page.init(getSelection());
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. 
	 */
	public boolean performFinish() {
		boolean res = super.performFinish();
		return res;
	}
	
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		page.createType(monitor);
	}
	
	public IJavaElement getCreatedElement() {
		return page.getCreatedType();
	}
}