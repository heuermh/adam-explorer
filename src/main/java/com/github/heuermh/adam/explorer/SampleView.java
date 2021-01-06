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

import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Sample view.
 *
 * @author  Michael Heuer
 */
final class SampleView extends LabelFieldPanel {

    /**
     * Create a new sample view with the specified samples.
     *
     * @param samples samples, must not be null
     */
    SampleView(final EventList<Sample> samples) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new SampleTable(samples));
    }

    /**
     * Sample table.
     */
    static class SampleTable extends ExplorerTable<Sample> {
        private static final String[] PROPERTY_NAMES = { "name", "id" };
        private static final String[] COLUMN_LABELS = { "Name", "Identifier" }; // todo: include processing step table?
        private static final TableFormat<Sample> TABLE_FORMAT = GlazedLists.tableFormat(Sample.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new sample table with the specified samples.
         *
         * @param samples samples, must not be null
         */
        SampleTable(final EventList<Sample> samples) {
            super("Samples:", samples, TABLE_FORMAT);
            getAddAction().setEnabled(false);
        }


        @Override
        protected String transferableString(final Sample s) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(s.name, s.id);
        }
    }
}
