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

import java.text.Normalizer;
import java.util.regex.Pattern;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.labels.DefaultLabels;

/**
 * Finds blocks which are potentially indicating the end of an article text and marks
 * them with {@link DefaultLabels#INDICATES_END_OF_TEXT}. This can be used in conjunction
 * with a downstream {@link IgnoreBlocksAfterContentFilter}.
 *
 * @author Christian Kohlschütter
 * @see IgnoreBlocksAfterContentFilter
 */
public class TerminatingBlocksFinder implements BoilerpipeFilter {
    public static final TerminatingBlocksFinder INSTANCE = new TerminatingBlocksFinder();

    /**
     * Returns the singleton instance for TerminatingBlocksFinder.
     */
    public static TerminatingBlocksFinder getInstance() {
        return INSTANCE;
    }

    public boolean process(TextDocument doc)
            throws BoilerpipeProcessingException {
        boolean changes = false;

        for (TextBlock tb : doc.getTextBlocks()) {
            if (tb.getNumWords() < 20) {
                final String text = normalize(tb.getText().trim());
                if (text.contains("accessibilite")
                        || text.contains("mentions legales")
                        ) {
                    tb.addLabel(DefaultLabels.INDICATES_END_OF_TEXT);
                    changes = true;
                }
            }
        }

        return changes;
    }

    String normalize(String input) {
        // split diacritics
        String spd = Normalizer.normalize(input, Normalizer.Form.NFD);
        // remove
        spd = spd.replaceAll("\\p{M}", "");
        return spd.toLowerCase();
    }

}
