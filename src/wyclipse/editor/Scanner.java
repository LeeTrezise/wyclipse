// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// Copyright (c) 2011, Lee Trezise(Lee.Trezise@gmail.com)
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
