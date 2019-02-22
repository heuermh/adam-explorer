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

import htsjdk.variant.vcf.VCFHeaderLine;

import org.bdgenomics.adam.rdd.variant.VariantDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Variant;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Variant view.
 *
 * @author  Michael Heuer
 */
final class VariantView extends LabelFieldPanel {
    private final VariantModel model;
    private final VariantTable table;

    /**
     * Create a new variant view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    VariantView(final VariantDataset dataset) {
        super();
        model = new VariantModel(dataset);
        table = new VariantTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Variants", layoutVariantView());
        tabbedPane.add("References", new ReferenceView(model.getReferences()));
        tabbedPane.add("Header Lines", new HeaderLineView(model.getHeaderLines()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutVariantView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Variant count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Variants currently viewing:", new CountLabel<Variant>(model.getVariants()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Variant model.
     */
    static class VariantModel {
        private final VariantDataset dataset;
        private final EventList<Reference> references;
        private final EventList<VCFHeaderLine> headerLines;
        private final EventList<Variant> variants;

        /**
         * Create a new variant model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        VariantModel(final VariantDataset dataset) {
            this.dataset = dataset;
            variants = GlazedLists.eventList(new ArrayList<Variant>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());
            references = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));

            headerLines = GlazedLists.eventList(JavaConversions.seqAsJavaList(dataset.headerLines()));
        }

        void take(final int take) {
            new SwingWorker<List<Variant>, Void>() {
                @Override
                public List<Variant> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Variant> result = get();

                        variants.getReadWriteLock().writeLock().lock();
                        try {
                            variants.clear();
                            variants.addAll(result);
                        }
                        finally {
                            variants.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        VariantDataset getDataset() {
            return dataset;
        }

        EventList<Variant> getVariants() {
            return variants;
        }

        EventList<Reference> getReferences() {
            return references;
        }

        EventList<VCFHeaderLine> getHeaderLines() {
            return headerLines;
        }
    }

    /**
     * Variant table.
     */
    static class VariantTable extends ElementsTable<Variant> {
        private final VariantModel model;
        private static final String[] PROPERTY_NAMES = { "referenceName", "start", "end", "referenceAllele", "alternateAllele" };
        private static final String[] COLUMN_LABELS = { "Reference Name", "Start", "End", "Ref", "Alt" };
        private static final TableFormat<Variant> TABLE_FORMAT = GlazedLists.tableFormat(Variant.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new variant table with the specified model.
         *
         * @param model model, must not be null
         */
        VariantTable(final VariantModel model) {
            super("Variants:", model.getVariants(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
        }

        @Override
        public void add() {
            if (model.getVariants().isEmpty()) {
                model.take(10);
            }
            else {
                model.take(model.getVariants().size() * 2);
            }
        }
    }
}
