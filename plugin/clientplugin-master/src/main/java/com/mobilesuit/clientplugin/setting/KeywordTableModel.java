package com.mobilesuit.clientplugin.setting;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class KeywordTableModel extends AbstractTableModel {
    private final List<KeywordRow> data;
    private final String[] columnNames = {"Keyword", "Description"}; // Add more column names as needed

    public KeywordTableModel(List<KeywordRow> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KeywordRow keywordRow = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> keywordRow.keyword;
            case 1 -> keywordRow.description;
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void addRow(KeywordRow keywordRow) {
        data.add(keywordRow);
        int rowIndex = data.size() - 1;
        fireTableRowsInserted(rowIndex, rowIndex);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= data.size()) {
            return;
        }
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        KeywordRow keywordRow = data.get(rowIndex);

        switch (columnIndex) {
            case 0 -> keywordRow.keyword = (String) newValue;
            case 1 -> keywordRow.description = (String) newValue;
            default -> {
                return;
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}

