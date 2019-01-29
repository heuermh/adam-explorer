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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * Stripe list cell renderer.
 *
 * @author  Michael Heuer
 */
final class StripeListCellRenderer extends DefaultListCellRenderer {
    static final Color EVEN_COLOR = new Color(42, 87, 3, 12); // 2a5703, 5% alpha

    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, boolean isSelected, boolean hasFocus)
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        if (isSelected) {
            label.setForeground(UIManager.getColor("List.selectionForeground"));
            label.setBackground(UIManager.getColor("List.selectionBackground"));
        }
        else {
            label.setForeground(UIManager.getColor("List.foreground"));

            if (index % 2 == 0) {
                label.setBackground(EVEN_COLOR);
            }
            else {
                label.setBackground(UIManager.getColor("List.background"));
            }
        }
        return label;
    }

    /**
     * Install a stripe list cell renderer for the specified list.
     *
     * @param list list, must not be null
     */
    static <T> void install(final JList<T> list) {
        StripeListCellRenderer renderer = new StripeListCellRenderer();
        list.setCellRenderer(renderer);
    }
}
