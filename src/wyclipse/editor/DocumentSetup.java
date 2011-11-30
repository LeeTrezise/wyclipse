package wyclipse.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension3;
import org.eclipse.jface.text.rules.FastPartitioner;

public class DocumentSetup implements IDocumentSetupParticipant  {

	@Override
	public void setup(IDocument document) {

		
		if(document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
	        IDocumentPartitioner partitioner = new FastPartitioner(new WhileyPartitioner(), WhileyPartitioner.WHILEY_PARTITION_TYPES);
	        ((IDocumentPartitionerExtension3)partitioner).connect((IDocument)extension3, false);
	        extension3.setDocumentPartitioner(WhileyPartitioner.WHILEY_MULTI_LINE_COMMENT, partitioner);
	        extension3.setDocumentPartitioner(WhileyPartitioner.WHILEY_SINGLE_LINE, partitioner);
           
            
		}else {
			 IDocumentPartitioner partitioner = new FastPartitioner(new WhileyPartitioner(), WhileyPartitioner.WHILEY_PARTITION_TYPES);
			 partitioner.connect(document);
			 document.setDocumentPartitioner(partitioner);
			 		}
		

	}

}
