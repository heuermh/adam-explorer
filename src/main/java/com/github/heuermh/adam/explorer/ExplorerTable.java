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

import java.awt.Toolkit;

import java.awt.datatransfer.StringSelection;

import java.util.List;

import ca.odell.glazedlists.EventList;

import ca.odell.glazedlists.gui.TableFormat;

import org.dishevelled.eventlist.view.ElementsTable;

import org.dishevelled.iconbundle.tango.TangoProject;

/**
 * Explorer table.
 *
 * @author  Michael Heuer
 */
abstract class ExplorerTable<E> extends ElementsTable<E> {

    /**
     * Create a new explorer table.
     *
     * @param labelText label text
     * @param model model, must not be null
     * @param tableFormat table format, must not be null
     */
    ExplorerTable(final String labelText, final EventList<E> model, final TableFormat<E> tableFormat) {
        super(labelText, model, tableFormat);

        getPasteAction().setEnabled(false);
        getToolBar().displayIcons();
        getToolBar().setIconSize(TangoProject.EXTRA_SMALL);
    }


    /**
     * Return a transferable string representation of the specified element.
     *
     * @param element element
     * @return a transferable string representation of the specified element
     */
    protected abstract String transferableString(E element);
    
    @Override
    protected final void cut(final List<E> toCut) {
        copy(toCut);
        getModel().removeAll(toCut);
    }

    @Override
    protected final void copy(final List<E> toCopy) {
        StringBuilder sb = new StringBuilder(toCopy.size() * 1024);
        for (E e : toCopy) {
            sb.append(transferableString(e));
            sb.append("\n");
        }
        StringSelection selection = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }
}
