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
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place navigator and outline to left of
        // editor area.
        IFolderLayout left =
                layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);
        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea);
       // bottom.addView(IPageLayout.ID_PROGRESS_VIEW);
        //left.addView(IPageLayout.ID_RES_NAV);
        bottom.addView("org.eclipse.ui.console.ConsoleView");
        left.addView(IPageLayout.ID_PROJECT_EXPLORER);
        //left.addView(IPageLayout.ID_OUTLINE);
}

	private void defineActions(IPageLayout arg) {
		
		arg.addNewWizardShortcut("wyclipse.wizards.NewModuleWizard");
		arg.addNewWizardShortcut("wyclipse.wizards.ProjectWizard");
		System.out.println("Added New Wizards");
	}

}
