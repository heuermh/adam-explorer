/**
 * Copyright 2018-2021 held jointly by the individual authors.
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

import com.google.common.base.Joiner;

import org.bdgenomics.formats.avro.Reference;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Reference view.
 *
 * @author  Michael Heuer
 */
final class ReferenceView extends LabelFieldPanel {

    /**
     * Create a new reference view with the specified references.
     *
     * @param references references, must not be null
     */
    ReferenceView(final EventList<Reference> references) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new ReferenceTable(references));
    }

    /**
     * Reference table.
     */
    static class ReferenceTable extends ExplorerTable<Reference> {
        private static final String[] PROPERTY_NAMES = { "index", "name", "length", "assembly", "species", "md5", "sourceUri", "sourceAccessions"};
        private static final String[] COLUMN_LABELS = { "Index", "Name", "Length", "Assembly", "Species", "Checksum (md5)", "Source URI", "Source Accessions" };
        private static final TableFormat<Reference> TABLE_FORMAT = GlazedLists.tableFormat(Reference.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new reference table with the specified references.
         *
         * @param references references, must not be null
         */
        ReferenceTable(final EventList<Reference> references) {
            super("References:", references, TABLE_FORMAT);
            getAddAction().setEnabled(false);
        }


        @Override
        protected String transferableString(final Reference r) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(r.index, r.name, r.length, r.assembly, r.species, r.md5, r.sourceUri, r.sourceAccessions);
        }
    }
}
