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

import org.bdgenomics.adam.models.SequenceRecord;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Sequence record view.
 *
 * @author  Michael Heuer
 */
final class SequenceRecordView extends LabelFieldPanel {

    /**
     * Create a new sequence record view with the specified sequence records.
     *
     * @param sequenceRecords sequence records, must not be null
     */
    SequenceRecordView(final EventList<SequenceRecord> sequenceRecords) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new SequenceRecordTable(sequenceRecords));
    }

    /**
     * Sequence record table.
     */
    static class SequenceRecordTable extends ElementsTable<SequenceRecord> {
        private static final String[] PROPERTY_NAMES = { "name", "length" };
        private static final String[] COLUMN_LABELS = { "Name", "Length" };
        private static final TableFormat<SequenceRecord> TABLE_FORMAT = GlazedLists.tableFormat(SequenceRecord.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new sequence record table with the specified sequence records.
         *
         * @param sequenceRecords sequence records, must not be null
         */
        SequenceRecordTable(final EventList<SequenceRecord> sequenceRecords) {
            super("Sequences:", sequenceRecords, TABLE_FORMAT);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
        }
    }
}
