package wyclipse.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class Editor extends TextEditor {
	public Editor() {
		super();		
		setSourceViewerConfiguration(new Configuration());
		System.out.println("COLOR: " + getPreferenceStore().getString(PREFERENCE_COLOR_FOREGROUND));
	}
	public void dispose() {
		super.dispose();
	}

}
