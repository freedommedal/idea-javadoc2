package com.sgota.plugin.idea.javadocs2.settings;

import com.github.setial.intellijjavadocs.configuration.JavaDocConfiguration;
import com.github.setial.intellijjavadocs.ui.settings.ConfigPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;

import javax.swing.JComponent;

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
        if (this.configPanel == null) {
            JavaDocConfiguration service = ApplicationManager.getApplication().getComponent(JavaDocConfiguration.class);
            this.configPanel = new ConfigPanel(service.getSettings());
        }
        this.reset();
        return this.configPanel;
    }

    @Override
    public boolean isModified() {
        return this.configPanel.isModified();
    }

    @Override
    public void apply() {
        JavaDocConfiguration service = ApplicationManager.getApplication().getComponent(JavaDocConfiguration.class);
        this.configPanel.apply();
        service.setupTemplates();
    }

    @Override
    public void reset() {
        this.configPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        this.configPanel.disposeUIResources();
        this.configPanel = null;
    }
}
