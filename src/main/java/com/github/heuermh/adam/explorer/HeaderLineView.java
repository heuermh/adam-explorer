/**
 * Copyright 2018-2019 held jointly by the individual authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.heuermh.adam.explorer;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;

import htsjdk.variant.vcf.VCFHeaderLine;

import org.dishevelled.eventlist.view.ElementsList;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Header line view.
 *
 * @author  Michael Heuer
 */
final class HeaderLineView extends LabelFieldPanel {

    /**
     * Create a new header line view with the specified header lines.
     *
     * @param headerLines header lines, must not be null
     */
    HeaderLineView(final EventList<VCFHeaderLine> headerLines) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new HeaderLineList(headerLines));
    }

    /**
     * Header line list.
     */
    static class HeaderLineList extends ElementsList<VCFHeaderLine> {

        /**
         * Create a new header line list with the specified header lines.
         *
         * @param headerLines header lines, must not be null
         */
        HeaderLineList(final EventList<VCFHeaderLine> headerLines) {
            super("Header lines:", headerLines);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
        }
    }
}
