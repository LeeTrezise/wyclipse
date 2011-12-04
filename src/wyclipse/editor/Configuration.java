package wyclipse.editor;

import org.eclipse.jdt.internal.ui.javaeditor.JavaMarkerAnnotation;
import org.eclipse.jdt.internal.ui.text.java.hover.AnnotationHover;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;



public class Configuration extends SourceViewerConfiguration {
	private Scanner scanner;
	private ColorManager manager = new ColorManager();
	
	public Configuration() {		
	}
	@Override
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT,
			WhileyPartitioner.WHILEY_SINGLE_LINE };
	}
	
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, 
		    String contentType) {
				//System.out.println(contentType);
		        IAutoEditStrategy strategy= (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) ||
		        		WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT.equals(contentType)
		        ? new WhileyAutoEditStrategy() : new DefaultIndentLineAutoEditStrategy());
		        return new IAutoEditStrategy[] { new WhileyAutoEditStrategy() };
		    }
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(new Scanner());
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		ddr = new DefaultDamagerRepairer(new Scanner());
		pr.setRepairer(ddr, WhileyPartitioner.WHILEY_SINGLE_LINE);
		pr.setDamager(ddr, WhileyPartitioner.WHILEY_SINGLE_LINE);
		NonRuleBasedDamagerRepairer ndr =
				new NonRuleBasedDamagerRepairer(
					new TextAttribute(
						manager.getColor(new RGB(63, 127, 95))));
		pr.setRepairer(ndr, WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT);
		pr.setDamager(ndr, WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT);
		return pr;		
	}
	
	public ITextHover getTextHover(ISourceViewer sv, String contentType) {
		return new DefaultTextHover(sv);		
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}
	
	
	
}