package wyclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;

import wyclipse.wizards.NewModuleWizard;

public class OpenNewModuleView implements IWorkbenchWindowActionDelegate, IViewActionDelegate {
	private Shell shell;
	public static final String ID = "wyclipse.newModuleAction";

	@Override
	public void run(IAction action) {
		NewModuleWizard wizard = new NewModuleWizard();		
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.open();
    }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {
		shell = window.getShell();
	}
	
	@Override
	public void init(IViewPart window) {
		shell = window.getSite().getShell();
	}
}
