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

import htsjdk.variant.vcf.VCFHeaderLine;

import org.bdgenomics.adam.ds.variant.GenotypeDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Genotype;
import org.bdgenomics.formats.avro.Sample;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Genotype view.
 *
 * @author  Michael Heuer
 */
final class GenotypeView extends LabelFieldPanel {
    private final GenotypeModel model;
    private final GenotypeTable table;

    /**
     * Create a new genotype view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    GenotypeView(final GenotypeDataset dataset) {
        super();
        model = new GenotypeModel(dataset);
        table = new GenotypeTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Genotypes", layoutGenotypeView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        tabbedPane.add("Samples", new SampleView(model.getSamples()));
        tabbedPane.add("Header Lines", new HeaderLineView(model.getHeaderLines()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutGenotypeView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Genotype count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Genotypes currently viewing:", new CountLabel<Genotype>(model.getGenotypes()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Genotype model.
     */
    static class GenotypeModel {
        private final GenotypeDataset dataset;
        private final EventList<Reference> references;
        private final EventList<Sample> samples;
        private final EventList<VCFHeaderLine> headerLines;
        private final EventList<Genotype> genotypes;

        /**
         * Create a new genotype model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        GenotypeModel(final GenotypeDataset dataset) {
            this.dataset = dataset;
            genotypes = GlazedLists.eventList(new ArrayList<Genotype>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.references().records());
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            samples = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.samples()));
            headerLines = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.headerLines()));
        }

        void take(final int take) {
            new SwingWorker<List<Genotype>, Void>() {
                @Override
                public List<Genotype> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Genotype> result = get();

                        genotypes.getReadWriteLock().writeLock().lock();
                        try {
                            genotypes.clear();
                            genotypes.addAll(result);
                        }
                        finally {
                            genotypes.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        GenotypeDataset getDataset() {
            return dataset;
        }

        EventList<Genotype> getGenotypes() {
            return genotypes;
        }

        EventList<Reference> getReferences() {
            return references;
        }

        EventList<Sample> getSamples() {
            return samples;
        }

        EventList<VCFHeaderLine> getHeaderLines() {
            return headerLines;
        }
    }

    /**
     * Genotype table.
     */
    static class GenotypeTable extends ExplorerTable<Genotype> {
        private final GenotypeModel model;
        private static final String[] PROPERTY_NAMES = { "referenceName", "start", "end", "variant.referenceAllele", "variant.alternateAllele", "alleles", "sampleId" };
        private static final String[] COLUMN_LABELS = { "Reference Name", "Start", "End", "Ref", "Alt", "Alleles", "Sample" };
        private static final TableFormat<Genotype> TABLE_FORMAT = GlazedLists.tableFormat(Genotype.class, PROPERTY_NAMES, COLUMN_LABELS);


        /**
         * Create a new genotype table with the specified model.
         *
         * @param model model, must not be null
         */
        GenotypeTable(final GenotypeModel model) {
            super("Genotypes:", model.getGenotypes(), TABLE_FORMAT);
            this.model = model;
        }


        @Override
        protected String transferableString(final Genotype g) {
            return Joiner
                .on("\t")
                .useForNull("")
                .join
                (
                 g.referenceName,
                 g.start,
                 g.end,
                 g.getVariant().getReferenceAllele(),
                 g.getVariant().getAlternateAllele(),
                 g.alleles,
                 g.sampleId
                 );
        }

        @Override
        public void add() {
            if (model.getGenotypes().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getGenotypes().size() * 2);
            }
        }
    }
}
