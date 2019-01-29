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

import org.bdgenomics.adam.rdd.fragment.FragmentDataset;

import org.bdgenomics.adam.models.SequenceRecord;

import org.bdgenomics.formats.avro.Reference;
import org.bdgenomics.formats.avro.Fragment;

import org.dishevelled.eventlist.view.CountLabel;
import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.layout.LabelFieldPanel;

import scala.collection.JavaConversions;

/**
 * Fragment view.
 *
 * @author  Michael Heuer
 */
final class FragmentView extends LabelFieldPanel {
    private final FragmentModel model;
    private final FragmentTable table;

    /**
     * Create a new fragment view with the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    FragmentView(final FragmentDataset dataset) {
        super();
        model = new FragmentModel(dataset);
        table = new FragmentTable(model);
        layoutComponents();
        model.take(10);
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Fragments", layoutFragmentView());
        tabbedPane.add("Sequences", new ReferenceView(model.getSequences()));
        addFinalField(tabbedPane);
    }

    private LabelFieldPanel layoutFragmentView() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.addField("Fragment count:", new DatasetCountLabel(model.getDataset()));
        panel.addField("Fragments currently viewing:", new CountLabel<Fragment>(model.getFragments()));
        panel.addSpacing(12);
        panel.addFinalField(table);
        return panel;
    }

    /**
     * Fragment model.
     */
    static class FragmentModel {
        private final FragmentDataset dataset;
        private final EventList<Reference> sequences;
        private final EventList<Fragment> fragments;

        /**
         * Create a new fragment model with the specified dataset.
         *
         * @param dataset dataset, must not be null
         */
        FragmentModel(final FragmentDataset dataset) {
            this.dataset = dataset;
            fragments = GlazedLists.eventList(new ArrayList<Fragment>());

            List<SequenceRecord> s = JavaConversions.seqAsJavaList(dataset.sequences().records());;
            sequences = GlazedLists.eventList(s.stream().map(v -> v.toADAMReference()).collect(Collectors.toList()));
        }

        void take(final int take) {
            new SwingWorker<List<Fragment>, Void>() {
                @Override
                public List<Fragment> doInBackground() {
                    return dataset.jrdd().take(take);
                }

                @Override
                public void done() {
                    try {
                        List<Fragment> result = get();

                        fragments.getReadWriteLock().writeLock().lock();
                        try {
                            fragments.clear();
                            fragments.addAll(result);
                        }
                        finally {
                            fragments.getReadWriteLock().writeLock().unlock();
                        }
                    }
                    catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
            }.execute();
        }

        FragmentDataset getDataset() {
            return dataset;
        }

        EventList<Fragment> getFragments() {
            return fragments;
        }

        EventList<Reference> getSequences() {
            return sequences;
        }
    }

    /**
     * Fragment table.
     */
    static class FragmentTable extends ElementsTable<Fragment> {
        private final FragmentModel model;
        private static final String[] PROPERTY_NAMES = { "name", "readGroupId", "insertSize", "alignments" };
        private static final String[] COLUMN_LABELS = { "Name", "Read Group", "Insert Size", "Alignments" };
        private static final TableFormat<Fragment> TABLE_FORMAT = GlazedLists.tableFormat(Fragment.class, PROPERTY_NAMES, COLUMN_LABELS);

        /**
         * Create a new fragment table with the specified model.
         *
         * @param model model, must not be null
         */
        FragmentTable(final FragmentModel model) {
            super("Fragments:", model.getFragments(), TABLE_FORMAT);

            this.model = model;
            getPasteAction().setEnabled(false);
            getToolBar().displayIcons();
            getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
            StripeTableCellRenderer.install(getTable());
        }

        @Override
        public void add() {
            model.take(model.getFragments().size() * 2);
        }
    }
}
