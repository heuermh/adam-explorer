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

import org.bdgenomics.formats.avro.ProcessingStep;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

/**
 * Processing step view.
 *
 * @author  Michael Heuer
 */
final class ProcessingStepView extends LabelFieldPanel {

    /**
     * Create a new processing step view with the specified processing steps.
     *
     * @param processingSteps processing steps, must not be null
     */
    ProcessingStepView(final EventList<ProcessingStep> processingSteps) {
        super();
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setOpaque(false);
        addFinalField(new ProcessingStepTable(processingSteps));
    }

    /**
     * Processing step table.
     */
    static class ProcessingStepTable extends ElementsTable<ProcessingStep> {
        private static final String[] PROPERTY_NAMES = { "id", "previousId", "programName", "version", "commandLine", "description" };
        private static final String[] COLUMN_LABELS = { "Step", "Previous Step", "Program Name", "Version", "Command Line", "Description" };
        private static final TableFormat<ProcessingStep> TABLE_FORMAT = GlazedLists.tableFormat(ProcessingStep.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new processing step table with the specified processing steps.
         *
         * @param processingSteps processing steps, must not be null
         */
        ProcessingStepTable(final EventList<ProcessingStep> processingSteps) {
            super("Processing steps:", processingSteps, TABLE_FORMAT);

            getAddAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }
    }
}
