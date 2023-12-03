package com.mobilesuit.clientplugin.setting;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JComponent jc) {
            // Set the tooltip text to be the string representation of the cell value
            jc.setToolTipText(value != null ? value.toString() : null);
        }
        return c;
    }
}
