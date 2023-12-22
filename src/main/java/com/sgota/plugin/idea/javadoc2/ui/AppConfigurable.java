package com.sgota.plugin.idea.javadoc2.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.sgota.plugin.idea.javadoc2.exception.GenerateException;
import com.sgota.plugin.idea.javadoc2.model.setting.JavaDocSetting;
import com.sgota.plugin.idea.javadoc2.service.JavaDocSettingService;
import com.sgota.plugin.idea.javadoc2.service.JavaDocTemplateService;
import com.sgota.plugin.idea.javadoc2.ui.model.TemplateVo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * App configurable
 *
 * @author tiankuo
 */
public class AppConfigurable implements Configurable {

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    private TemplateTable classTemplateTable;
    private TemplateTable fieldTemplateTable;
    private TemplateTable constructorTemplateTable;
    private TemplateTable methodTemplateTable;

    private final JavaDocSetting javaDocSetting;

    public AppConfigurable() {
        JavaDocSettingService javaDocSettingService = ApplicationManager.getApplication().getService(JavaDocSettingService.class);
        this.javaDocSetting = javaDocSettingService.getState();
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        createTemplatesPanel();
        reset();
        return mainPanel;
    }

    private void createTemplatesPanel() {
        JPanel classSettingPanel = new JPanel(new BorderLayout());
        classSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("类和接口", false, JBUI.insetsBottom(10)));
        classTemplateTable = new TemplateTable();
        JPanel classTemplateTablePanel = ToolbarDecorator.createDecorator(classTemplateTable).createPanel();
        classSettingPanel.add(classTemplateTablePanel, BorderLayout.CENTER);

        JPanel fieldSettingPanel = new JPanel(new BorderLayout());
        fieldSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("成员变量", false, JBUI.insetsBottom(10)));
        fieldTemplateTable = new TemplateTable();
        JPanel fieldTemplateTablePanel = ToolbarDecorator.createDecorator(fieldTemplateTable).createPanel();
        fieldSettingPanel.add(fieldTemplateTablePanel, BorderLayout.CENTER);

        JPanel constructorSettingPanel = new JPanel(new BorderLayout());
        constructorSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("构造方法", false, JBUI.insetsBottom(10)));
        constructorTemplateTable = new TemplateTable();
        JPanel constructorTemplateTablePanel = ToolbarDecorator.createDecorator(constructorTemplateTable).createPanel();
        constructorSettingPanel.add(constructorTemplateTablePanel, BorderLayout.CENTER);

        JPanel methodSettingPanel = new JPanel(new BorderLayout());
        methodSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("普通方法", false, JBUI.insetsBottom(10)));
        methodTemplateTable = new TemplateTable();
        JPanel methodTemplateTablePanel = ToolbarDecorator.createDecorator(methodTemplateTable).createPanel();
        methodSettingPanel.add(methodTemplateTablePanel, BorderLayout.CENTER);

        JPanel templateSettingPanel = new JPanel();
        templateSettingPanel.setLayout(new GridLayoutManager(4, 1, JBUI.insets(10), -1, -1));

        templateSettingPanel.add(classSettingPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        templateSettingPanel.add(fieldSettingPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        templateSettingPanel.add(constructorSettingPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        templateSettingPanel.add(methodSettingPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        tabbedPane.addTab("注释模板设置", templateSettingPanel);
        tabbedPane.addTab("使用说明", this.createGuideComponent());
    }

    /**
     * createGuideComponent
     *
     * @return JComponent
     */
    protected JComponent createGuideComponent() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html"); // 设置内容类型为 HTML
        // HTML 代码
        String htmlContent = this.readGuideContent();
        // 设置 HTML 内容
        editorPane.setText(htmlContent);
        // 设置为只读
        editorPane.setEditable(false);
        // 放入 JScrollPane，以便支持滚动
        return new JBScrollPane(editorPane);
    }

    /**
     * 读取文件内容
     *
     * @return String
     */
    private String readGuideContent() {
        String filePath = "guide.html";
        try (InputStream inputStream = AppConfigurable.class.getResourceAsStream("/".concat(filePath))) {
            if (inputStream != null) {
                byte[] bytes = FileUtil.loadBytes(inputStream);
                return new String(bytes, StandardCharsets.UTF_8);
            } else {
                return "file not found: " + filePath;
            }
        } catch (IOException e) {
            return "read file exception: " + filePath;
        }
    }

    @Override
    public boolean isModified() {
        boolean result;
        Map<String, String> classTemplates = classTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> constructorTemplates = constructorTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> methodTemplates = methodTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> fieldTemplates = fieldTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));

        result = !javaDocSetting.getClassTemplates().equals(classTemplates);
        result = result || !javaDocSetting.getConstructorTemplates().equals(constructorTemplates);
        result = result || !javaDocSetting.getMethodTemplates().equals(methodTemplates);
        result = result || !javaDocSetting.getFieldTemplates().equals(fieldTemplates);
        return result;
    }

    @Override
    public void apply() {
        Map<String, String> classTemplates = classTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> constructorTemplates = constructorTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> methodTemplates = methodTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));
        Map<String, String> fieldTemplates = fieldTemplateTable.getModelList().stream().collect(Collectors.toMap(TemplateVo::getName, TemplateVo::getContent, (a, b) -> b, LinkedHashMap::new));

        javaDocSetting.setClassTemplates(classTemplates);
        javaDocSetting.setConstructorTemplates(constructorTemplates);
        javaDocSetting.setMethodTemplates(methodTemplates);
        javaDocSetting.setFieldTemplates(fieldTemplates);
        JavaDocTemplateService docTemplateManagerService = ApplicationManager.getApplication().getService(JavaDocTemplateService.class);
        try {
            docTemplateManagerService.reloadTemplateCache(javaDocSetting);
        } catch (GenerateException e) {
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
        }
    }

    @Override
    public void reset() {
        List<TemplateVo> classTemplateUiModelList = convertTemplateUiModelList(javaDocSetting.getClassTemplates());
        List<TemplateVo> constructorTemplateUiModelList = convertTemplateUiModelList(javaDocSetting.getConstructorTemplates());
        List<TemplateVo> methodTemplateUiModelList = convertTemplateUiModelList(javaDocSetting.getMethodTemplates());
        List<TemplateVo> fieldTemplateUiModelList = convertTemplateUiModelList(javaDocSetting.getFieldTemplates());

        classTemplateTable.setModelList(classTemplateUiModelList);
        constructorTemplateTable.setModelList(constructorTemplateUiModelList);
        methodTemplateTable.setModelList(methodTemplateUiModelList);
        fieldTemplateTable.setModelList(fieldTemplateUiModelList);
    }

    private List<TemplateVo> convertTemplateUiModelList(Map<String, String> templateMap) {
        List<TemplateVo> modelList = new ArrayList<>();
        templateMap.forEach((key, value) -> {
            TemplateVo templateUiModel = new TemplateVo();
            templateUiModel.setName(key);
            templateUiModel.setContent(value);
            modelList.add(templateUiModel);
        });
        return modelList;
    }
}
