package wyclipse.editor;


import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
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

//public class Scanner implements ITokenScanner {
public class Scanner extends RuleBasedScanner {
	private ColorManager manager;
	private List<WhileyLexer.Token> tokens;
	public Scanner() {
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
		
		Token keyword = new Token(new TextAttribute(new Color(null, 127, 0, 85), null, SWT.BOLD));
		Token comment = new Token(new TextAttribute(new Color(null, 63,127,95)));
		Token string = new Token(new TextAttribute(new Color(null, 42, 0, 255)));
		//for(WhileyLexer.Token tok: tokens) {
		//	rule.addWord(tok.text, keyword);
		//}
		for(String s: WhileyLexer.keywords) {
			rule.addWord(s, keyword);
		}
		setRules(new IRule[] {
				rule,
				new EndOfLineRule("//", comment),
				new MultiLineRule("/*", "*/", comment, (char) 0, true),
				new SingleLineRule("\"", "\"", string, '\\'),
				new SingleLineRule("'", "'", string, '\\'),
				new WhitespaceRule(new IWhitespaceDetector() {
					
					@Override
					public boolean isWhitespace(char c) {
						return Character.isWhitespace(c);
					}
				})		
				
		});
		
		
	}

	}
