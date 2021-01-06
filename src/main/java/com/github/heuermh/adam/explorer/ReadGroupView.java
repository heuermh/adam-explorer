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

import org.bdgenomics.formats.avro.ReadGroup;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Read group view.
 *
 * @author  Michael Heuer
 */
final class ReadGroupView extends LabelFieldPanel {

    /**
     * Create a new read group view with the specified read groups.
     *
     * @param readGroups read groups, must not be null
     */
    ReadGroupView(final EventList<ReadGroup> readGroups) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new ReadGroupTable(readGroups));
    }

    /**
     * Read group table.
     */
    static class ReadGroupTable extends ExplorerTable<ReadGroup> {
        private static final String[] PROPERTY_NAMES = { "id", "sampleId", "description", "sequencingCenter", "runDateEpoch", "flowOrder", "keySequence", "library", "predictedMedianInsertSize", "platform", "platformModel", "platformUnit" };
        private static final String[] COLUMN_LABELS = { "Identifier", "Sample", "Description", "Sequencing Center", "Run Date", "Flow Order", "Key Sequence", "Library", "Insert Size", "Platform", "Platform Model", "Platform Unit" };
        private static final TableFormat<ReadGroup> TABLE_FORMAT = GlazedLists.tableFormat(ReadGroup.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new read group table with the specified read groups.
         *
         * @param readGroups read groups, must not be null
         */
        ReadGroupTable(final EventList<ReadGroup> readGroups) {
            super("Read groups:", readGroups, TABLE_FORMAT);
            getAddAction().setEnabled(false);
        }


        @Override
        protected String transferableString(final ReadGroup rg) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join
                (
                 rg.id,
                 rg.sampleId,
                 rg.description,
                 rg.sequencingCenter,
                 rg.runDateEpoch,
                 rg.flowOrder,
                 rg.keySequence,
                 rg.library,
                 rg.predictedMedianInsertSize,
                 rg.platform,
                 rg.platformModel,
                 rg.platformUnit
                 );
        }
    }
}
