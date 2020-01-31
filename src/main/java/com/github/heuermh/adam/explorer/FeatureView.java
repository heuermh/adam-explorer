/**
 * Copyright 2018-2020 held jointly by the individual authors.
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

import org.bdgenomics.adam.rdd.feature.FeatureDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Feature;
import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Feature view.
 *
 * @author  Michael Heuer
 */
final class FeatureView extends LabelFieldPanel {
    private final FeatureModel model;
    private final FeatureTable table;

    /**
     * Create a new feature view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    FeatureView(final FeatureDataset dataset) {
        super();
        model = new FeatureModel(dataset);
        table = new FeatureTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Features", layoutFeatureView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        tabbedPane.add("Samples", new SampleView(model.getSamples()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutFeatureView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Feature count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Features currently viewing:", new CountLabel<Feature>(model.getFeatures()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Feature model.
     */
    static class FeatureModel {
        private final FeatureDataset dataset;
        private final EventList<Feature> features;
        private final EventList<Reference> references;
        private final EventList<Sample> samples;


        /**
         * Create a new feature model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        FeatureModel(final FeatureDataset dataset) {
            this.dataset = dataset;
            features = GlazedLists.eventList(new ArrayList<Feature>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            samples = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.samples()));
        }

        void take(final int take) {
            new SwingWorker<List<Feature>, Void>() {
                @Override
                public List<Feature> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Feature> result = get();

                        features.getReadWriteLock().writeLock().lock();
                        try {
                            features.clear();
                            features.addAll(result);
                        }
                        finally {
                            features.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        FeatureDataset getDataset() {
            return dataset;
        }

        EventList<Feature> getFeatures() {
            return features;
        }

        EventList<Reference> getReferences() {
            return references;
        }

        EventList<Sample> getSamples() {
            return samples;
        }
    }

    /**
     * Feature table.
     */
    static class FeatureTable extends ExplorerTable<Feature> {
        private final FeatureModel model;
        private static final String[] PROPERTY_NAMES = { "referenceName", "start", "end", "strand", "name", "featureId", "featureType", "score", "sampleId" };
        private static final String[] COLUMN_LABELS = { "Reference Name", "Start", "End", "Strand", "Name", "Identifier", "Type", "Score", "Sample" };
        private static final TableFormat<Feature> TABLE_FORMAT = GlazedLists.tableFormat(Feature.class, PROPERTY_NAMES, COLUMN_LABELS);


        /**
         * Create a new feature table with the specified model.
         *
         * @param model model, must not be null
         */
        FeatureTable(final FeatureModel model) {
            super("Features:", model.getFeatures(), TABLE_FORMAT);
            this.model = model;
        }


        @Override
        protected String transferableString(final Feature f) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join(f.referenceName, f.start, f.end, f.strand, f.name, f.featureId, f.featureType, f.score, f.sampleId);
        }

        @Override
        public void add() {
            if (model.getFeatures().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getFeatures().size() * 2);
            }
        }
    }
}
