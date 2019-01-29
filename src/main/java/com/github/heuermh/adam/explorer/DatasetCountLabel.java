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

import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.bdgenomics.adam.rdd.GenomicDataset;

/**
 * Dataset count label.
 *
 * @author  Michael Heuer
 */
class DatasetCountLabel extends JLabel {

    /**
     * Create a new dataset count label for the specified dataset.
     *
     * @param dataset dataset, must not be null
     */
    DatasetCountLabel(final GenomicDataset dataset) {
        super("");
        new SwingWorker<Long, Void>() {
            @Override
            public Long doInBackground() {
                return dataset.jrdd().count();
            }

            @Override
            public void done() {
                try {
                    setText(get().toString());
                }
                catch (InterruptedException | ExecutionException e) {
                    // ignore
                }
            }
        }.execute();
    }
}
