package wyclipse.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import wyclipse.Activator;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class NewModuleWizardPage extends NewTypeWizardPage {	
	private Text sourceLocation;
	private Text packageName;
	private Text moduleName;
		
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewModuleWizardPage() {
		super(true,"NewModuleWizardPage");
		setTitle("Whiley Module");
		setDescription("Create a Whiley Module.");		
	}

	public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
    }
	
	public String getCompilationUnitName(String moduleName) {
		return moduleName + ".whiley";
	}
		
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();		
		layout.numColumns = 4;
		container.setLayout(layout);
		
		createContainerControls(container,4);
		createPackageControls(container,4);
		createSeparator(container,4);
		createTypeNameControls(container,4);
							
		setControl(container);
	}			
	
	/**
	 * This method is basically responsible for checking that the module name
	 * entered makes sense.
	 */
	protected IStatus typeNameChanged() {
		StatusInfo status = (StatusInfo) super.typeNameChanged();
		IPackageFragment pack = getPackageFragment();
		if (pack == null) {
			return status;
		}

		IJavaProject project = pack.getJavaProject();
		try {
			if (!project.getProject().hasNature(Activator.WYCLIPSE_NATURE_ID)) {
				status.setWarning(project.getElementName()
						+ " is not a whiley project.  Whiley Nature will be added so Whiley files can be compiled!");
			}
		} catch (CoreException e) {
			status.setError("Exception when accessing project natures for "
					+ project.getElementName());
		}

		String typeName = getTypeName();

		// must not exist
		if (!isEnclosingTypeSelected()
				&& (status.getSeverity() < IStatus.ERROR)) {
			if (pack != null) {
				IType type = null;
				try {
					type = project.findType(pack.getElementName(), typeName);
				} catch (JavaModelException e) {
					// can ignore
				}
				if (type != null && type.getPackageFragment().equals(pack)) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists);
				}
			}
		}

		return status;
	}
}