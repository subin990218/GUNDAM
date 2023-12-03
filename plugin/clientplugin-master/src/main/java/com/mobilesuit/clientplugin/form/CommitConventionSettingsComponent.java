package com.mobilesuit.clientplugin.form;

import com.intellij.openapi.project.Project;
import com.mobilesuit.clientplugin.setting.*;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;


@Slf4j
public class CommitConventionSettingsComponent {
    private JPanel mainPanel;
    private JButton addPropertyButton;
    private JButton removePropertyButton;
    private JList<String> propertiesList;
    private JTable keywordsTable;
    private JScrollPane keywordsTableScrollPane;
    private JScrollPane propertiesListScrollPane;
    private JCheckBox allowEmptyBodyCheckBox;
    private JCheckBox allowEmptyFooterCheckBox;
    private JFormattedTextField headerConventionTextField;
    private JFormattedTextField bodyConventionTextField;
    private JFormattedTextField footerConventionTextField;
    private JScrollPane conventionPanelScrollPane;
    private JTextField propertyNameField;
    private JButton addKeywordButton;
    private JButton removeKeywordButton;
    private JButton resetPropertiesButton;
    private JCheckBox allowOnlyKeywordsOnListCheckBox;

    @Getter @Setter
    private List<PropertyOption> propertyOptions;

    public String getHeaderConvention() {
        return headerConventionTextField.getText();
    }

    public void setHeaderConvention(String text) {
        headerConventionTextField.setText(text);
    }

    public String getBodyConvention() {
        return bodyConventionTextField.getText();
    }

    public void setBodyConvention(String text) {
        bodyConventionTextField.setText(text);
    }

    public String getFooterConvention() {
        return footerConventionTextField.getText();
    }

    public void setFooterConvention(String text) {
        footerConventionTextField.setText(text);
    }

    public boolean getAllowEmptyBody() {
        return allowEmptyBodyCheckBox.isSelected();
    }

    public void setAllowEmptyBody(boolean newStatus) {
        allowEmptyBodyCheckBox.setSelected(newStatus);
    }

    public boolean getAllowEmptyFooter() {
        return allowEmptyFooterCheckBox.isSelected();
    }

    public void setAllowEmptyFooter(boolean newStatus) {
        allowEmptyFooterCheckBox.setSelected(newStatus);
    }

    public List<String> getProperties() {
        return Collections.list(
                ((DefaultListModel<String>) propertiesList.getModel())
                        .elements());
    }

    public void setProperties(List<String> list) {
        DefaultListModel<String> listModel = (DefaultListModel<String>) propertiesList.getModel();
        if (list != null) {
            listModel.clear();
            list.forEach(listModel::addElement);
        }
    }

    public CommitConventionSettingsComponent() {
        initList();
        initStyle();
        addPropertyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> listModel = (DefaultListModel<String>) propertiesList.getModel();

                propertyOptions.add(new PropertyOption());

                propertiesList.setSelectedIndex(listModel.size() - 1);
            }
        });
        removePropertyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = propertiesList.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                DefaultListModel<String> listModel = (DefaultListModel<String>) propertiesList.getModel();
                listModel.removeElementAt(index);

                propertyOptions.remove(index);

                propertiesList.setSelectedIndex(listModel.size() - 1);
            }
        });
        propertyNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changePropertyName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changePropertyName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changePropertyName();
            }

            public void changePropertyName() {
                int index = propertiesList.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                DefaultListModel<String> listModel = (DefaultListModel<String>) propertiesList.getModel();
                listModel.setElementAt(propertyNameField.getText(), index);
            }
        });
        propertiesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int index = propertiesList.getSelectedIndex();
                if (index < 0 || index > propertyOptions.size()) {
                    return;
                }
                String currentName = (propertiesList.getModel()).getElementAt(index);
                PropertyOption currentOption = propertyOptions.get(index);
                propertyNameField.setText(currentName);
                allowOnlyKeywordsOnListCheckBox.setSelected(currentOption.isAllowOnlyKeywordsOnList());

                keywordsTable.setModel(new KeywordTableModel(currentOption.getKeywordRows()));
                initTableSetting();
            }
        });
        allowOnlyKeywordsOnListCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int index = propertiesList.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                PropertyOption currentOption = propertyOptions.get(index);
                boolean match = (e.getStateChange() == ItemEvent.SELECTED);
                currentOption.setAllowOnlyKeywordsOnList(match);
            }
        });
        addKeywordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeywordTableModel tableModel = (KeywordTableModel) keywordsTable.getModel();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tableModel.addRow(KeywordRow.builder()
                                .keyword("")
                                .description("")
                                .build());
                    }
                });
            }
        });
        removeKeywordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = keywordsTable.getSelectedRow();
                if (index < 0) {
                    return;
                }
                KeywordTableModel tableModel = (KeywordTableModel) keywordsTable.getModel();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tableModel.removeRow(index);
                    }
                });
            }
        });
        resetPropertiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project project = DataContainer.getInstance().getProject();
                CommitConventionSettingsState conventionSettingsState = CommitConventionSettingsState.getInstance(project);
                conventionSettingsState.resetToDefaultSettings();
            }
        });
    }
    public JPanel getContentPane() {
        return mainPanel;
    }

    private void initList() {
        ListModel<String> listModel = new DefaultListModel<>();
        propertiesList.setModel(listModel);

        propertiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initStyle() {
        propertiesListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        keywordsTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    private void initTableSetting() {
        keywordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keywordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        TableColumnModel columnModel = keywordsTable.getColumnModel();
        TableColumn firstColumn = columnModel.getColumn(0);
        firstColumn.setPreferredWidth(150);
        firstColumn.setMaxWidth(200);
        firstColumn.setMinWidth(100);
        TableColumn secondColumn = columnModel.getColumn(1);
        secondColumn.setPreferredWidth(400);
        for (int i = 0; i < keywordsTable.getColumnCount(); i++) {
            keywordsTable.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
        }
    }
}
