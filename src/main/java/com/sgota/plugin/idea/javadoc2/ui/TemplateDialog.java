package com.sgota.plugin.idea.javadoc2.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.sgota.plugin.idea.javadoc2.ui.model.TemplateVo;

import javax.swing.*;
import java.awt.*;

/**
 * The type Template config dialog.
 *
 * @author Sergey Timofiychuk,tiankuo
 */
public class TemplateDialog extends DialogWrapper {

    private JTextField nameJTextField;
    private JTextArea contentJTextArea;

    private final TemplateVo model;

    public TemplateDialog(TemplateVo model) {
        super(true);
        this.model = model;
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridLayoutManager(2, 1, JBUI.insets(10), -1, -1));

        nameJTextField = new JTextField();
        if (model != null) {
            nameJTextField.setText(model.getName());
        }
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(IdeBorderFactory.createTitledBorder("正则表达式", false, JBUI.insetsBottom(10)));
        namePanel.add(nameJTextField, BorderLayout.CENTER);

        contentJTextArea = new JTextArea();
        if (model != null) {
            contentJTextArea.setText(model.getContent());
        }
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(IdeBorderFactory.createTitledBorder("模板内容", false, JBUI.emptyInsets()));
        contentPanel.add(contentJTextArea, BorderLayout.CENTER);

        mainPanel.add(namePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainPanel.add(contentPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        model.setName(nameJTextField.getText());
        model.setContent(contentJTextArea.getText());
        super.doOKAction();
    }

    /**
     * Gets model
     *
     * @return the model
     */
    public TemplateVo getModel() {
        return model;
    }
}
