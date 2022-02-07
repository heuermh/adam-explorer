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

import org.bdgenomics.adam.ds.read.ReadDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Alphabet;
import org.bdgenomics.formats.avro.Read;
import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.identify.StripeTableCellRenderer;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Read view.
 *
 * @author  Michael Heuer
 */
final class ReadView extends LabelFieldPanel {
    private final ReadModel model;
    private final ReadTable table;

    /**
     * Create a new read view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    ReadView(final ReadDataset dataset) {
        super();
        model = new ReadModel(dataset);
        table = new ReadTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Reads", layoutReadView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        tabbedPane.add("Samples", new SampleView(model.getSamples()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutReadView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Read count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Reads currently viewing:", new CountLabel<Read>(model.getReads()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Read model.
     */
    static class ReadModel {
        private final ReadDataset dataset;
        private final EventList<Read> reads;
        private final EventList<Reference> references;
        private final EventList<Sample> samples;


        /**
         * Create a new read model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        ReadModel(final ReadDataset dataset) {
            this.dataset = dataset;
            reads = GlazedLists.eventList(new ArrayList<Read>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.references().records());;
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            samples = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.samples()));
        }

        void take(final int take) {
            new SwingWorker<List<Read>, Void>() {
                @Override
                public List<Read> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Read> result = get();

                        reads.getReadWriteLock().writeLock().lock();
                        try {
                            reads.clear();
                            reads.addAll(result);
                        }
                        finally {
                            reads.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        ReadDataset getDataset() {
            return dataset;
        }

        EventList<Read> getReads() {
            return reads;
        }

        EventList<Reference> getReferences() {
            return references;
        }

        EventList<Sample> getSamples() {
            return samples;
        }
    }

    /**
     * Read table.
     */
    static class ReadTable extends ExplorerTable<Read> {
        private final ReadModel model;
        private static final String[] PROPERTY_NAMES = { "name", "description", "sampleId", "alphabet", "length", "sequence", "qualityScores" };
        private static final String[] COLUMN_LABELS = { "Name", "Description", "Sample", "Alphabet", "Length", "Sequence", "Quality Scores" };
        private static final TableFormat<Read> TABLE_FORMAT = GlazedLists.tableFormat(Read.class, PROPERTY_NAMES, COLUMN_LABELS);


        /**
         * Create a new read table with the specified model.
         *
         * @param model model, must not be null
         */
        ReadTable(final ReadModel model) {
            super("Reads:", model.getReads(), TABLE_FORMAT);
            this.model = model;

            StripeTableCellRenderer renderer = new StripeTableCellRenderer();
            getTable().setDefaultRenderer(Alphabet.class, renderer);
        }


        @Override
        protected String transferableString(final Read r) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(r.getName(), r.getDescription(), r.getSampleId(), r.getAlphabet(), r.getLength(), r.getSequence(), r.getQualityScores());
        }

        @Override
        public void add() {
            if (model.getReads().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getReads().size() * 2);
            }
        }
    }
}
