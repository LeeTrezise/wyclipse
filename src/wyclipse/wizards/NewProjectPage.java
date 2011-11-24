package wyclipse.wizards;

import java.io.*;
import java.net.URL;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class NewProjectPage extends WizardPage {
	private Text projectName;
	private Button defaultLocation;
	private Text location;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewProjectPage(ISelection selection) {
		super("wizardPage");
		setTitle("Create a Whiley Project");
		setDescription("Enter a project name.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;	
		layout.marginWidth = 20;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Project Name:");

		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		projectName.setLayoutData(gd);
		
		defaultLocation = new Button(container, SWT.CHECK);				
		defaultLocation.setText("Use default location");

		location = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		location.setLayoutData(gd);
		initialiseLocation();
		
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseLocation();
			}
		});
		
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		setControl(container);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowseLocation() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Choose a directory for project contents");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				location.setText(((Path) result[0]).toString());
			}
		}
	}
	
	private void initialiseLocation() {		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		//URL path = FileLocator.resolve(new URL("workspace:/"));
		IPath path = root.getRawLocation();
		location.setText(path.toOSString());		
	}
	
	public String getLocation() {
		return location.getText();
	}
	
	public String getProjectName() {
		return projectName.getText();
	}
}