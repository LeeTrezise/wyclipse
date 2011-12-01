package wyclipse.editor;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class WhileyPartitioner extends RuleBasedPartitionScanner{
		public final static String WHILEY_SINGLE_LINE = "__WHILEY_SINGLE";
		public final static String WHILEY_MULTI_LINE_COMMENT = "__WHILEY_MULTI";
		public final static String[] WHILEY_PARTITION_TYPES = new String[] {WHILEY_SINGLE_LINE, WHILEY_MULTI_LINE_COMMENT};
		
         /**
          * Creates the partitioner and sets up the appropriate rules.
          */
         public WhileyPartitioner() {
             super ();
             IToken multiComment = new Token(WHILEY_MULTI_LINE_COMMENT);
             IToken singleComment = new Token(WHILEY_SINGLE_LINE);
             IPredicateRule[] rules = new IPredicateRule[2];
             rules[0] = new MultiLineRule("/*", "*/", multiComment);
             rules[1] = new EndOfLineRule("//", singleComment);
             setPredicateRules(rules);
         }
     
}
