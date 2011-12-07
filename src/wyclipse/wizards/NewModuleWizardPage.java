// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyclipse.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import wyclipse.Activator;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class NewModuleWizardPage extends NewClassWizardPage {	
			
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewModuleWizardPage() {
		//super(true,"NewModuleWizardPage");
		super();
		setTitle("Whiley Module");
		setDescription("Create a Whiley Module.");		
	}

	public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        doStatusUpdate();
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
	
	 private void doStatusUpdate() {
         IStatus[] status = new IStatus[] {
                 fContainerStatus,
                 fPackageStatus, 
                 fTypeNameStatus };
         updateStatus(status);
     }

     @Override
     protected void handleFieldChanged(String fieldName) {
         super .handleFieldChanged(fieldName);
         doStatusUpdate();
     }
	
	@Override
	protected IStatus typeNameChanged() {
		StatusInfo status = (StatusInfo) super.typeNameChanged();
		IPackageFragment pack = getPackageFragment();
		if (pack != null) {
			// Check whether the Whiley nature needs to be added or not.
			IJavaProject project = getJavaProject();
			try {
				if (!project.getProject().hasNature(
						Activator.WYCLIPSE_NATURE_ID)) {
					status.setWarning(project.getElementName()
							+ " is not a whiley project.  Whiley Nature will be added so Whiley files can be compiled!");
				}
			} catch (CoreException e) {
				status.setError("Exception when accessing project natures for "
						+ project.getElementName());
			}
		}

		// could check exclusion filters here
		
		return status;
	}		
	
	@Override
	public void createType(IProgressMonitor monitor)
            throws CoreException, InterruptedException {
        if (monitor == null) {
        	monitor = new NullProgressMonitor();
        }

		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 8);

		IPackageFragmentRoot root = getPackageFragmentRoot();
		IPackageFragment pack = getPackageFragment();
		if (pack == null) {
			pack = root.getPackageFragment(""); //$NON-NLS-1$
		}

		if (!pack.exists()) {
			String packName = pack.getElementName();
			pack = root.createPackageFragment(packName, true,
					new SubProgressMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
		
		String unitName = getCompilationUnitName(getTypeName());
		ICompilationUnit unit = pack.createCompilationUnit(unitName, "", false,
				new SubProgressMonitor(monitor, 2));
		try {				
			unit.becomeWorkingCopy(new SubProgressMonitor(monitor, 1)); 

			monitor.done();
		} finally {
			if(unit != null) {
				unit.discardWorkingCopy();
			}
			monitor.done();
		}
	}
}