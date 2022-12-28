package com.sgota.plugin.idea.javadoc2.ui;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.EditableModel;
import com.intellij.util.ui.UIUtil;
import com.sgota.plugin.idea.javadoc2.ui.model.TemplateVo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;

/**
 * The type Templates table.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class TemplateTable extends JBTable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private List<TemplateVo> modelList = new ArrayList<>(0);

    public TemplateTable() {
        setStriped(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setModel(new TableModel());
        Enumeration<TableColumn> columns = getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            columns.nextElement().setCellRenderer(new TemplateCellRenderer());
        }
    }

    /**
     * Gets modelList
     *
     * @return the modelList
     */
    public List<TemplateVo> getModelList() {
        return modelList;
    }

    /**
     * Sets modelList
     *
     * @param modelList the modelList
     */
    public void setModelList(List<TemplateVo> modelList) {
        this.modelList = modelList;
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (e == null) {
            return false;
        }
        if (e instanceof MouseEvent) {
            MouseEvent event = (MouseEvent) e;
            if (event.getClickCount() == 1) {
                return false;
            }
        }
        if (modelList.size() > row) {
            TemplateDialog templateDialog = new TemplateDialog(modelList.get(row));
            templateDialog.show();
            if (templateDialog.isOK()) {
                modelList.set(row, templateDialog.getModel());
            }
        }
        return false;
    }

    private class TableModel extends AbstractTableModel implements EditableModel {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;
        private final List<String> columnNames;

        public TableModel() {
            columnNames = new ArrayList<>();
            columnNames.add("正则表达式");
            columnNames.add("模板内容");
        }

        @Override
        public String getColumnName(int column) {
            return columnNames.get(column);
        }

        @Override
        public void addRow() {
            TemplateDialog templateDialog = new TemplateDialog(new TemplateVo());
            templateDialog.show();
            if (templateDialog.isOK()) {
                modelList.add(templateDialog.getModel());
            }
        }

        @Override
        public void removeRow(int index) {
            modelList.remove(index);
        }

        @Override
        public void exchangeRows(int oldIndex, int newIndex) {
            TemplateVo oldItem = modelList.get(oldIndex);
            modelList.set(oldIndex, modelList.get(newIndex));
            modelList.set(newIndex, oldItem);
        }

        @Override
        public boolean canExchangeRows(int oldIndex, int newIndex) {
            return true;
        }

        @Override
        public int getRowCount() {
            return modelList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 0 ? modelList.get(rowIndex).getName() : modelList.get(rowIndex).getContent();
        }
    }

    private static class TemplateCellRenderer extends JLabel implements TableCellRenderer {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected && hasFocus ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected && hasFocus ? table.getSelectionForeground() : table.getForeground());
            setBorder(hasFocus ? UIUtil.getTableFocusCellHighlightBorder() : BorderFactory.createEmptyBorder(1, 1, 1, 1));
            setText(value == null ? "" : value.toString());
            return this;
        }
    }
}
