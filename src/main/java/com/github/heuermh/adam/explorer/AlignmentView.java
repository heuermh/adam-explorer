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

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;

import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.bdgenomics.adam.rdd.GenomicDataset;

import org.bdgenomics.adam.rdd.read.AlignmentRecordDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.AlignmentRecord;
import org.bdgenomics.formats.avro.ProcessingStep;
import org.bdgenomics.formats.avro.ReadGroup;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Alignment view.
 *
 * @author  Michael Heuer
 */
final class AlignmentView extends LabelFieldPanel {
    private final AlignmentModel model;
    private final AlignmentTable table;

    /**
     * Create a new alignment view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    AlignmentView(final AlignmentRecordDataset dataset) {
        super();
        model = new AlignmentModel(dataset);
        table = new AlignmentTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Alignments", layoutAlignmentView());
        tabbedPane.add("Sequences", new ReferenceView(model.getSequences()));
        tabbedPane.add("Read Groups", new ReadGroupView(model.getReadGroups()));
        tabbedPane.add("Processing Steps", new ProcessingStepView(model.getProcessingSteps()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutAlignmentView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Alignment count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Alignments currently viewing:", new CountLabel<AlignmentRecord>(model.getAlignments()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Alignment model.
     */
    static class AlignmentModel {
        private final AlignmentRecordDataset dataset;
        private final EventList<Reference> sequences;
        private final EventList<ReadGroup> readGroups;
        private final EventList<ProcessingStep> processingSteps;
        private final EventList<AlignmentRecord> alignments;

        /**
         * Create a new alignment model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        AlignmentModel(final AlignmentRecordDataset dataset) {
            this.dataset = dataset;
            alignments = GlazedLists.eventList(new ArrayList<AlignmentRecord>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            List<org.bdgenomics.adam.models.ReadGroup> rg = JavaConversions.seqAsJavaList(dataset.readGroups().readGroups());
            readGroups = GlazedLists.eventList(rg.stream().map(v -> v.toMetadata()).collect(Collectors.toList()));
            processingSteps = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.processingSteps()));
        }

        void take(final int take) {
            new SwingWorker<List<AlignmentRecord>, Void>() {
                @Override
                public List<AlignmentRecord> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<AlignmentRecord> result = get();

                        alignments.getReadWriteLock().writeLock().lock();
                        try {
                            alignments.clear();
                            alignments.addAll(result);
                        }
                        finally {
                            alignments.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        AlignmentRecordDataset getDataset() {
            return dataset;
        }

        EventList<AlignmentRecord> getAlignments() {
            return alignments;
        }

        EventList<Reference> getSequences() {
            return sequences;
        }

        EventList<ReadGroup> getReadGroups() {
            return readGroups;
        }

        EventList<ProcessingStep> getProcessingSteps() {
            return processingSteps;
        }
    }

    /**
     * Alignment table.
     */
    static class AlignmentTable extends ElementsTable<AlignmentRecord> {
        private final AlignmentModel model;
        private static final String[] PROPERTY_NAMES = { "referenceName", "start", "end", "readName", "readGroupSampleId", "readGroupId" };
        private static final String[] COLUMN_LABELS = { "Reference Name", "Start", "End", "Read Name", "Sample", "Read Group" };
        private static final TableFormat<AlignmentRecord> TABLE_FORMAT = GlazedLists.tableFormat(AlignmentRecord.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new alignment table with the specified model.
         *
         * @param model model, must not be null
         */
        AlignmentTable(final AlignmentModel model) {
            super("Alignments:", model.getAlignments(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getAlignments().size() * 2);
        }
    }
}
