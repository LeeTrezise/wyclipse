package wyclipse.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class Editor extends TextEditor {
	public Editor() {
		super();
		setSourceViewerConfiguration(new Configuration());		
	}
	public void dispose() {
		super.dispose();
	}	
}
