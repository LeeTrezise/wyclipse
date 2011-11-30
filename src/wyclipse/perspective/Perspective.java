package wyclipse.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout arg) {
		defineActions(arg);
		defineLayout(arg);
	}

	public void defineLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        IFolderLayout left =layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);
        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea);
        bottom.addView("org.eclipse.ui.console.ConsoleView");
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        left.addView(IPageLayout.ID_PROJECT_EXPLORER);
        
}

	private void defineActions(IPageLayout arg) {
		arg.addNewWizardShortcut("wyclipse.newProjectWizard");
		arg.addNewWizardShortcut("wyclipse.newModuleWizard");
	}

}
