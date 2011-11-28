package wyclipse.editor;

import org.eclipse.jdt.internal.ui.text.JavaPartitionScanner;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;

public class Configuration extends SourceViewerConfiguration {
	private Scanner scanner;
	private ColorManager manager = new ColorManager();
	
	public Configuration() {		
	}
	
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, 
		    String contentType) {
		        IAutoEditStrategy strategy= (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) 
		        ? new WhileyAutoEditStrategy() : new DefaultIndentLineAutoEditStrategy());
		        return new IAutoEditStrategy[] { strategy };
		    }
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(new Scanner());
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		ddr = new DefaultDamagerRepairer(new RuleBasedPartitionScanner());
		pr.setDamager(ddr, JavaPartitionScanner.JAVA_MULTI_LINE_COMMENT);
		pr.setRepairer(ddr, JavaPartitionScanner.JAVA_MULTI_LINE_COMMENT);
		return pr;		
	}
	
	public ITextHover getTextHover(ISourceViewer sv, String contentType) {
		return new DefaultTextHover(sv);		
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer view) {
		ContentAssistant ca = new ContentAssistant();
		IContentAssistProcessor pr = new CompletionProcessor();
		ca.setContentAssistProcessor(pr, IDocument.DEFAULT_CONTENT_TYPE);
		ca.setInformationControlCreator(getInformationControlCreator(view));
		return ca;
	}
	
}