package wyclipse.editor;


import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import wyc.stages.WhileyLexer;



public class Scanner extends RuleBasedScanner {

		public Scanner() {
			IToken keyword = new Token(new TextAttribute(new Color(null, 127, 0, 85), null, SWT.BOLD));
			IToken comment = new Token(new TextAttribute(new Color(null, 63,127,95)));
			IToken string = new Token(new TextAttribute(new Color(null, 42, 0, 255)));
			WordRule rule = new WordRule(new IWordDetector() {
				@Override
				public boolean isWordStart(char c) {
					return Character.isJavaIdentifierStart(c);
				}
				@Override
				public boolean isWordPart(char c) {
					return Character.isJavaIdentifierPart(c);
				}
			});
			for(String s: WhileyLexer.keywords) {
				rule.addWord(s, keyword);
			}
				rule.addWord("from", keyword); // HACK. TODO FIX
			IRule[] rules = new IRule[6];
			rules[0] = new SingleLineRule("\"", "\"", string, '\\');
			// Add a rule for single quotes
			rules[1] = new SingleLineRule("'", "'", string, '\\');
			// Add generic whitespace rule.
			rules[2] = new WhitespaceRule(new WhitespaceDetector());
			
			rules[3] = rule;
			rules[4] = new EndOfLineRule("//", comment);
			rules[5] = new MultiLineRule("/*", "", comment, (char) 0, true);
			setRules(rules);
		}
		
		
		
		
	}
