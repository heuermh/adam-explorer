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
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.formats.avro.Reference;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.identify.StripeTableCellRenderer;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Reference view.
 *
 * @author  Michael Heuer
 */
final class ReferenceView extends LabelFieldPanel {

    /**
     * Create a new reference view with the specified sequences.
     *
     * @param sequences sequences, must not be null
     */
    ReferenceView(final EventList<Reference> sequences) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new ReferenceTable(sequences));
    }

    /**
     * Reference table.
     */
    static class ReferenceTable extends ElementsTable<Reference> {
        private static final String[] PROPERTY_NAMES = { "index", "name", "length", "assembly", "species", "md5", "sourceUri", "sourceAccessions"};
        private static final String[] COLUMN_LABELS = { "Index", "Name", "Length", "Assembly", "Species", "Checksum (md5)", "Source URI", "Source Accessions" };
        private static final TableFormat<Reference> TABLE_FORMAT = GlazedLists.tableFormat(Reference.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new reference table with the specified sequences.
         *
         * @param sequences sequences, must not be null
         */
        ReferenceTable(final EventList<Reference> sequences) {
            super("Sequences:", sequences, TABLE_FORMAT);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }
    }
}
