package com.sgota.plugin.idea.javadoc2.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.sgota.plugin.idea.javadoc2.model.setting.JavaDocSetting;
import org.apache.commons.collections.MapUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * JavaDocSettingService
 *
 * @author Sergey Timofiychuk,tiankuo
 */
@State(name = "Javadoc2", storages = @Storage("javadoc2.xml"))
public class JavaDocSettingService implements PersistentStateComponent<JavaDocSetting> {

    private final JavaDocSetting javaDocSetting = new JavaDocSetting();

    @Override
    public JavaDocSetting getState() {
        return javaDocSetting;
    }

    @Override
    public void loadState(JavaDocSetting javaDocSetting) {
        XmlSerializerUtil.copyBean(javaDocSetting, this.javaDocSetting);
        if (MapUtils.isEmpty(this.javaDocSetting.getClassTemplates())) {
            noStateLoaded();
        }
        JavaDocTemplateService templateManager = ApplicationManager.getApplication().getService(JavaDocTemplateService.class);
        templateManager.reloadTemplateCache(javaDocSetting);
    }

    @Override
    public void noStateLoaded() {
        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> templates = yaml.load(JavaDocSettingService.class.getResourceAsStream("/template.yml"));
        javaDocSetting.getClassTemplates().putAll(templates.get("class"));
        javaDocSetting.getFieldTemplates().putAll(templates.get("field"));
        javaDocSetting.getMethodTemplates().putAll(templates.get("method"));
        javaDocSetting.getConstructorTemplates().putAll(templates.get("constructor"));
    }
}
