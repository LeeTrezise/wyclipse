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
