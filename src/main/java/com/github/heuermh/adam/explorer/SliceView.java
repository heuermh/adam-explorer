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

import org.bdgenomics.adam.ds.sequence.SliceDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Alphabet;
import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Sample;
import org.bdgenomics.formats.avro.Slice;

import org.dishevelled.identify.StripeTableCellRenderer;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Slice view.
 *
 * @author  Michael Heuer
 */
final class SliceView extends LabelFieldPanel {
    private final SliceModel model;
    private final SliceTable table;

    /**
     * Create a new slice view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    SliceView(final SliceDataset dataset) {
        super();
        model = new SliceModel(dataset);
        table = new SliceTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Slices", layoutSliceView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        tabbedPane.add("Samples", new SampleView(model.getSamples()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutSliceView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Slice count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Slices currently viewing:", new CountLabel<Slice>(model.getSlices()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Slice model.
     */
    static class SliceModel {
        private final SliceDataset dataset;
        private final EventList<Slice> slices;
        private final EventList<Reference> references;
        private final EventList<Sample> samples;


        /**
         * Create a new slice model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        SliceModel(final SliceDataset dataset) {
            this.dataset = dataset;
            slices = GlazedLists.eventList(new ArrayList<Slice>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.references().records());;
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            samples = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.samples()));
        }

        void take(final int take) {
            new SwingWorker<List<Slice>, Void>() {
                @Override
                public List<Slice> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Slice> result = get();

                        slices.getReadWriteLock().writeLock().lock();
                        try {
                            slices.clear();
                            slices.addAll(result);
                        }
                        finally {
                            slices.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        SliceDataset getDataset() {
            return dataset;
        }

        EventList<Slice> getSlices() {
            return slices;
        }

        EventList<Reference> getReferences() {
            return references;
        }

        EventList<Sample> getSamples() {
            return samples;
        }
    }

    /**
     * Slice table.
     */
    static class SliceTable extends ExplorerTable<Slice> {
        private final SliceModel model;
        private static final String[] PROPERTY_NAMES = { "name", "description", "sampleId", "alphabet", "start", "end", "length", "index", "slices", "totalLength", "sequence" };
        private static final String[] COLUMN_LABELS = { "Name", "Description", "Sample", "Alphabet", "Start", "End", "Length", "Index", "Slices", "Total Length", "Sequence" };
        private static final TableFormat<Slice> TABLE_FORMAT = GlazedLists.tableFormat(Slice.class, PROPERTY_NAMES, COLUMN_LABELS);


        /**
         * Create a new slice table with the specified model.
         *
         * @param model model, must not be null
         */
        SliceTable(final SliceModel model) {
            super("Slices:", model.getSlices(), TABLE_FORMAT);
            this.model = model;

            StripeTableCellRenderer renderer = new StripeTableCellRenderer();
            getTable().setDefaultRenderer(Alphabet.class, renderer);
        }


        @Override
        protected String transferableString(final Slice s) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(s.getName(), s.getDescription(), s.getSampleId(), s.getAlphabet(), s.getStart(), s.getEnd(), s.getLength(), s.getIndex(), s.getSlices(), s.getTotalLength(), s.getSequence());
        }

        @Override
        public void add() {
            if (model.getSlices().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getSlices().size() * 2);
            }
        }
    }
}
