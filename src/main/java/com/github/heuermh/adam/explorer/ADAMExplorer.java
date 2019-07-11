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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.bdgenomics.adam.rdd.feature.FeatureDataset;
import org.bdgenomics.adam.rdd.fragment.FragmentDataset;
import org.bdgenomics.adam.rdd.read.AlignmentRecordDataset;
import org.bdgenomics.adam.rdd.read.ReadDataset;
import org.bdgenomics.adam.rdd.sequence.SequenceDataset;
import org.bdgenomics.adam.rdd.sequence.SliceDataset;
import org.bdgenomics.adam.rdd.variant.GenotypeDataset;
import org.bdgenomics.adam.rdd.variant.VariantDataset;

/**
 * Interactive explorer for ADAM genomics data models.
 *
 * @author  Michael Heuer
 */
public final class ADAMExplorer {

    /**
     * Explore the specified alignments.
     *
     * @param alignments alignments to explore, must not be null
     * @return an exit code
     */
    public static int explore(final AlignmentRecordDataset alignments) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new AlignmentExplorer(alignments).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Alignment explorer.
     */
    static class AlignmentExplorer extends JFrame {

        /**
         * Create a new alignment explorer.
         *
         * @param alignments alignments to explore, must not be null
         */
        AlignmentExplorer(final AlignmentRecordDataset alignments) {
            super("Alignments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new AlignmentView(alignments));
        }
    }

    /**
     * Explore the specified features.
     *
     * @param features features to explore, must not be null
     * @return an exit code
     */
    public static int explore(final FeatureDataset features) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new FeatureExplorer(features).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Feature explorer.
     */
    static class FeatureExplorer extends JFrame {

        /**
         * Create a new feature explorer.
         *
         * @param features features to explore, must not be null
         */
        FeatureExplorer(final FeatureDataset features) {
            super("Features");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FeatureView(features));
        }
    }

    /**
     * Explore the specified fragments.
     *
     * @param fragments fragments to explore, must not be null
     * @return an exit code
     */
    public static int explore(final FragmentDataset fragments) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new FragmentExplorer(fragments).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Fragment explorer.
     */
    static class FragmentExplorer extends JFrame {

        /**
         * Create a new fragment explorer.
         *
         * @param fragments fragments to explore, must not be null
         */
        FragmentExplorer(final FragmentDataset fragments) {
            super("Fragments");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new FragmentView(fragments));
        }
    }

    /**
     * Explore the specified genotypes.
     *
     * @param genotypes genotypes to explore, must not be null
     * @return an exit code
     */
    public static int explore(final GenotypeDataset genotypes) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new GenotypeExplorer(genotypes).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Genotype explorer.
     */
    static class GenotypeExplorer extends JFrame {

        /**
         * Create a new genotype explorer.
         *
         * @param genotypes genotypes to explore, must not be null
         */
        GenotypeExplorer(final GenotypeDataset genotypes) {
            super("Genotypes");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new GenotypeView(genotypes));
        }
    }

    /**
     * Explore the specified reads.
     *
     * @param reads reads to explore, must not be null
     * @return an error code
     */
    public static int explore(final ReadDataset reads) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ReadExplorer(reads).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Read explorer.
     */
    static class ReadExplorer extends JFrame {

        /**
         * Create a new read explorer.
         *
         * @param reads reads to explore, must not be null
         */
        ReadExplorer(final ReadDataset reads) {
            super("Reads");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new ReadView(reads));
        }
    }

    /**
     * Explore the specified sequences.
     *
     * @param sequences sequences to explore, must not be null
     * @return an error code
     */
    public static int explore(final SequenceDataset sequences) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SequenceExplorer(sequences).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Sequence explorer.
     */
    static class SequenceExplorer extends JFrame {

        /**
         * Create a new sequence explorer.
         *
         * @param sequences sequences to explore, must not be null
         */
        SequenceExplorer(final SequenceDataset sequences) {
            super("Sequences");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new SequenceView(sequences));
        }
    }

    /**
     * Explore the specified slices.
     *
     * @param slices slices to explore, must not be null
     * @return an error code
     */
    public static int explore(final SliceDataset slices) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SliceExplorer(slices).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Slice explorer.
     */
    static class SliceExplorer extends JFrame {

        /**
         * Create a new slice explorer.
         *
         * @param slices slices to explore, must not be null
         */
        SliceExplorer(final SliceDataset slices) {
            super("Slices");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new SliceView(slices));
        }
    }

    /**
     * Explore the specified variants.
     *
     * @param variants variants to explore, must not be null
     * @return an exit code
     */
    public static int explore(final VariantDataset variants) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new VariantExplorer(variants).setVisible(true);
                }
            });
        return 0;
    }

    /**
     * Variant explorer.
     */
    static class VariantExplorer extends JFrame {

        /**
         * Create a new variant explorer.
         *
         * @param variants variants to explore, must not be null
         */
        VariantExplorer(final VariantDataset variants) {
            super("Variants");
            setSize(970, 600);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            add("Center", new VariantView(variants));
        }
    }
}
