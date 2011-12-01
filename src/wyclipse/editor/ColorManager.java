package wyclipse.editor;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


public class ColorManager {

	protected Map fColorTable = new HashMap(10);

	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	public static final RGB KEYWORD_COLOR = new RGB(127, 0, 85);
	public static final RGB DEFAULT_COLOR = new RGB(0, 0, 0);
	public static final RGB STRING_COLOR = new RGB(42, 0, 255);
	public static final RGB COMMENT_COLOR = new RGB(63, 127, 95);
	
	public static final java.awt.Color KEYWORD_COLOR_C = new java.awt.Color(127, 0, 85);
	public static final  java.awt.Color DEFAULT_COLOR_C = new java.awt.Color(0, 0, 0);
	public static final  java.awt.Color STRING_COLOR_C = new java.awt.Color(42, 0, 255);
	public static final  java.awt.Color COMMENT_COLOR_C = new java.awt.Color(63, 127, 95);
	
	
}
