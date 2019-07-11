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

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import com.google.common.base.Joiner;

import org.bdgenomics.adam.rdd.sequence.SequenceDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Alphabet;
import org.bdgenomics.formats.avro.Sequence;
import org.bdgenomics.formats.avro.Reference;

import org.dishevelled.identify.StripeTableCellRenderer;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Sequence view.
 *
 * @author  Michael Heuer
 */
final class SequenceView extends LabelFieldPanel {
    private final SequenceModel model;
    private final SequenceTable table;

    /**
     * Create a new sequence view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    SequenceView(final SequenceDataset dataset) {
        super();
        model = new SequenceModel(dataset);
        table = new SequenceTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Sequences", layoutSequenceView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutSequenceView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Sequence count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Sequences currently viewing:", new CountLabel<Sequence>(model.getSequences()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Sequence model.
     */
    static class SequenceModel {
        private final SequenceDataset dataset;
        private final EventList<Sequence> sequences;
        private final EventList<Reference> references;


        /**
         * Create a new sequence model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        SequenceModel(final SequenceDataset dataset) {
            this.dataset = dataset;
            sequences = GlazedLists.eventList(new ArrayList<Sequence>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));
        }

        void take(final int take) {
            new SwingWorker<List<Sequence>, Void>() {
                @Override
                public List<Sequence> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Sequence> result = get();

                        sequences.getReadWriteLock().writeLock().lock();
                        try {
                            sequences.clear();
                            sequences.addAll(result);
                        }
                        finally {
                            sequences.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        SequenceDataset getDataset() {
            return dataset;
        }

        EventList<Sequence> getSequences() {
            return sequences;
        }

        EventList<Reference> getReferences() {
            return references;
        }
    }

    /**
     * Sequence table.
     */
    static class SequenceTable extends ExplorerTable<Sequence> {
        private final SequenceModel model;
        private static final String[] PROPERTY_NAMES = { "name", "description", "alphabet", "length", "sequence" };
        private static final String[] COLUMN_LABELS = { "Name", "Description", "Alphabet", "Length", "Sequence" };
        private static final TableFormat<Sequence> TABLE_FORMAT = GlazedLists.tableFormat(Sequence.class, PROPERTY_NAMES, COLUMN_LABELS);


        /**
         * Create a new sequence table with the specified model.
         *
         * @param model model, must not be null
         */
        SequenceTable(final SequenceModel model) {
            super("Sequences:", model.getSequences(), TABLE_FORMAT);
            this.model = model;

            StripeTableCellRenderer renderer = new StripeTableCellRenderer();
            getTable().setDefaultRenderer(Alphabet.class, renderer);
        }


        @Override
        protected String transferableString(final Sequence s) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(s.name, s.description, s.alphabet, s.length, s.sequence);
        }

        @Override
        public void add() {
            if (model.getSequences().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getSequences().size() * 2);
            }
        }
    }
}
