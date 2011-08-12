package wyclipse.editor;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class Scanner extends RuleBasedScanner {

	public Scanner() {
		IRule[] rules = new IRule[2];
		
		setRules(rules);
	}
}
