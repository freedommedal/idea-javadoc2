package com.sgota.plugin.idea.javadocs2.settings;

import com.github.setial.intellijjavadocs.configuration.JavaDocConfiguration;
import com.github.setial.intellijjavadocs.ui.settings.ConfigPanel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import javax.swing.*;

/**
 * App configurable
 *
 * @author tiankuo
 */
public class AppConfigurable implements Configurable {

    /**
     * Config panel
     */
    private ConfigPanel configPanel;

    @Override
    public String getDisplayName() {
        return "Idea JavaDoc2";
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        if (configPanel == null) {
            JavaDocConfiguration service = ServiceManager.getService(JavaDocConfiguration.class);
            configPanel = new ConfigPanel(service.getSettings());
        }
        reset();
        return configPanel;
    }

    @Override
    public boolean isModified() {
        return configPanel.isModified();
    }

    @Override
    public void apply() {
        JavaDocConfiguration service = ServiceManager.getService(JavaDocConfiguration.class);
        configPanel.apply();
        service.setupTemplates();
    }

    @Override
    public void reset() {
        configPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        configPanel.disposeUIResources();
        configPanel = null;
    }
}
