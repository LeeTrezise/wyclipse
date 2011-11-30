package wyclipse.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

public class WhileyAutoEditStrategy implements IAutoEditStrategy {

	
	
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
				// TODO Auto-generated catch block
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
			String s = d.getPartition(c.length).getType();
			int p = (c.offset == d.getLength() ? c.offset-1 : c.offset);
			int start = d.getLineInformationOfOffset(p).getOffset();
			int end = findEndOfWhiteSpace(d, start, c.offset);
			StringBuffer buf = new StringBuffer(c.text);
			if(end > start) {
				buf.append(d.get(start, end-start));
			}
			
			if(lastWord(d, c.offset).trim().endsWith(":")) {
				c.text = buf.toString() + "\t";
			} else if (s.equals(WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT)) {
			c.text =  buf.toString() + "*";}
			else {
				c.text = buf.toString();
			}
		}catch(BadLocationException e) {
			System.out.println("Excepting");
			e.printStackTrace();
		}
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
