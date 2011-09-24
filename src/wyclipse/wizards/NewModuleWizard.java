package wyclipse.wizards;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class NewModuleWizard extends Wizard {
	private MainPage mainPage;
	
	
	public void addPages() {
		mainPage = new MainPage("Whiley Module Page");
		addPage(mainPage);
	}
	
	public boolean performFinish() {
		return false;
	}
	
	private static class MainPage extends WizardPage {
		private Text sourceFolder;
		private Text pkg;
		private Text name;

		protected MainPage(String pageName) {
			super(pageName);
			setTitle("Whiley Module");
			setDescription("Create a new Whiley module.");
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			composite.setLayout(layout);
			setControl(composite);
			new Label(composite, SWT.NONE).setText("Source folder:");
			sourceFolder = new Text(composite, SWT.NONE);
			new Label(composite, SWT.NONE).setText("Package:");
			pkg = new Text(composite, SWT.NONE);
			new Label(composite, SWT.NONE).setText("Name:");
			name = new Text(composite, SWT.NONE);
		}
	}
}
