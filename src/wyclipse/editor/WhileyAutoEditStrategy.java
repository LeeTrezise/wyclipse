// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// Copyright (c) 2011, Lee Trezise (Lee.Trezise@gmail.com)
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

public class WhileyAutoEditStrategy implements IAutoEditStrategy {

	private static final String[] unindentValues = new String[] {"return", "pass", "continue", "throws", "skip" };
	
	@Override
	public void customizeDocumentCommand(IDocument document,
			DocumentCommand command) {
		if(command.length == 0 && command.text != null && TextUtilities.endsWith(document.getLegalLineDelimiters(), command.text) != -1) {
			autoIndentAfterLine(document, command);
		} else if(command.text.equals("'")) {
			command.text = "''";
			configureCommand(command);
		} else if(command.text.equals("\"")) {
			command.text = "\"\"";
			configureCommand(command);
		} else if(command.text.equals("(")) {
			command.text="()";
			configureCommand(command);
		} else if(command.text.equals("[")) {
			command.text="[]";
			configureCommand(command);
		} else if(command.text.equals("*")) {
			try {
				if(document.get(command.offset-1, 1).trim().equals("/")) {
				command.text = "*\n*/";
				configureCommand(command);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void configureCommand(DocumentCommand com) {
        com.caretOffset = com.offset+1;
        com.shiftsCaret = false;
		
	}
	
	public static int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException {
		while(offset < end) {
			char c = document.getChar(offset);
			if(c != ' ' && c != '\t') {
				return offset;
			}
			offset++;
		}
		return end;
	}

	
	public static String getLineIndent(IDocument doc, int line) throws BadLocationException {
		if(line > -1) {
			int start = doc.getLineOffset(line);
			int end = start+ doc.getLineLength(line) -1;
			int endOfWhite = findEndOfWhiteSpace(doc, start, end);
			return doc.get(start, endOfWhite-start);
		} else {
			return "";
		}
	}
	/*
	 * Used under the EPL v1.0 TODO PROPER CITATION
	 */
	private void autoIndentAfterLine(IDocument d, DocumentCommand c) {
		if(c.offset == -1 || d.getLength() == 0) {
			System.out.println("Returning d=0 or offset -1");
			return;
		}	
		
		try {
			//String s = d.getPartition(c.length).getType();
			int p = (c.offset == d.getLength() ? c.offset-1 : c.offset);
			int start = d.getLineInformationOfOffset(p).getOffset();
			int end = findEndOfWhiteSpace(d, start, c.offset);
			if(matchesUnindent(getLine(d, c.offset))) {
				end--;}
			StringBuffer buf = new StringBuffer(c.text);
			if(end > start) {
				buf.append(d.get(start, end-start));
			}		
			if(lastWord(d, c.offset).trim().endsWith(":")) {
				c.text = buf.toString() + "\t";
			} else {
				c.text = buf.toString();
			}
		}catch(BadLocationException e) {
			System.out.println("Excepting");
			e.printStackTrace();
		}
	}
	private boolean matchesUnindent(String line) {
		String[] split = line.split(" ");
		for(String s: unindentValues) {
			if(s.equals(split[0]))
				return true;
		}
		return false;
	}

	private String getLine(IDocument doc, int offset) throws BadLocationException {
		try {
			for(int n=offset-1;n>=0;n--) {
				char c = doc.getChar(n);
				if(c == '\t' || c =='\n' || c == '\0') {
					//End of Line. Return.
					return doc.get(n+1, offset-n-1);
				}
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return "";
	}
	private String lastWord(IDocument doc, int offset) {
		boolean start = true;
		try {
			for(int n = offset-1;n>=0;n--) {
				char c = doc.getChar(n);
				if(start) {
					//Waiting for Actual Character
					if(!Character.isWhitespace(c)) {
						start = false;
					}
				}
				else {
				if(Character.isWhitespace(c)) {
					return doc.get(n+1, offset-n-1);
				}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
