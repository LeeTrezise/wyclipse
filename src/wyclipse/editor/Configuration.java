package wyclipse.editor;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

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
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(new Scanner(manager));
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;		
	}
	
	public ITextHover getTextHover(ISourceViewer sv, String contentType) {
		return new DefaultTextHover(sv);		
	}
}