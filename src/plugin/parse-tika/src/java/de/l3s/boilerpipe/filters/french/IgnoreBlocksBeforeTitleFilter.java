/**
 * boilerpipe
 *
 * Copyright (c) 2009 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.filters.french;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Marks all blocks as "non-content" that occur after blocks that have been
 * marked {@link DefaultLabels#INDICATES_END_OF_TEXT}. These marks are ignored
 * unless a minimum number of words in content blocks occur before this mark (default: 60).
 * This can be used in conjunction with an upstream {@link TerminatingBlocksFinder}.
 *
 * @author Christian Kohlschütter
 * @see TerminatingBlocksFinder
 */
public final class IgnoreBlocksBeforeTitleFilter extends HeuristicFilterBase implements BoilerpipeFilter {
    public static final IgnoreBlocksBeforeTitleFilter DEFAULT_INSTANCE = new IgnoreBlocksBeforeTitleFilter(
            60);
    private final int minNumWords;

    private final List<TextBlock> buffer = new ArrayList<>();


    /**
     * Returns the singleton instance for DeleteBlocksAfterContentFilter.
     */
    public static IgnoreBlocksBeforeTitleFilter getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public IgnoreBlocksBeforeTitleFilter(final int minNumWords) {
        this.minNumWords = minNumWords;
    }

    public boolean process(TextDocument doc)
            throws BoilerpipeProcessingException {
        boolean changes = false;

        buffer.clear();

        boolean foundBeginOfText = false;
        for (Iterator<TextBlock> it = doc.getTextBlocks().iterator(); it.hasNext();) {
            TextBlock block = it.next();

            if (block.hasLabel(DefaultLabels.TITLE)) {
                foundBeginOfText = true;
                for (TextBlock tb:buffer) {
                    tb.setIsContent(false);
                }
                buffer.clear();
            }
            if (block.isContent() && !foundBeginOfText) {
                block.setIsContent(false);
                changes = true;
            } else if (block.isContent()) {
                buffer.add(block);
            }
        }

        return changes;
    }
}
