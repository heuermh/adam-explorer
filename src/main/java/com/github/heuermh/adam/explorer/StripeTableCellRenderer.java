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

import java.awt.Component;
import java.awt.Color;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Stripe table cell renderer.
 *
 * @author  Michael Heuer
 */
final class StripeTableCellRenderer extends DefaultTableCellRenderer {
    static final Color EVEN_COLOR = new Color(42, 87, 3, 12); // 2a5703, 5% alpha

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            label.setForeground(UIManager.getColor("Table.selectionForeground"));
            label.setBackground(UIManager.getColor("Table.selectionBackground"));
        }
        else {
            if (hasFocus) {
                label.setForeground(UIManager.getColor("Table.focusCellForeground"));
            }
            else {
                label.setForeground(UIManager.getColor("Table.foreground"));
            }
            if (row % 2 == 0) {
                label.setBackground(EVEN_COLOR);
            }
            else {
                if (hasFocus) {
                    label.setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
                else {
                    label.setBackground(UIManager.getColor("Table.background"));
                }
            }
        }
        return label;
    }

    /**
     * Install a stripe table cell renderer for the specified table.
     *
     * @param table table, must not be null
     */
    static void install(final JTable table) {
        StripeTableCellRenderer renderer = new StripeTableCellRenderer();
        table.setDefaultRenderer(Boolean.class, renderer);
        table.setDefaultRenderer(Double.class, renderer);
        table.setDefaultRenderer(Float.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
        table.setDefaultRenderer(List.class, renderer);
        table.setDefaultRenderer(Long.class, renderer);
        table.setDefaultRenderer(String.class, renderer);
    }
}
