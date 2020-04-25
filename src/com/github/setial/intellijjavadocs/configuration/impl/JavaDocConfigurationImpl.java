package com.github.setial.intellijjavadocs.configuration.impl;

import com.github.setial.intellijjavadocs.configuration.JavaDocConfiguration;
import com.github.setial.intellijjavadocs.exception.SetupTemplateException;
import com.github.setial.intellijjavadocs.model.settings.JavaDocSettings;
import com.github.setial.intellijjavadocs.model.settings.Level;
import com.github.setial.intellijjavadocs.model.settings.Mode;
import com.github.setial.intellijjavadocs.model.settings.Visibility;
import com.github.setial.intellijjavadocs.template.DocTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * The type Java doc configuration impl.
 *
 * @author Sergey Timofiychuk
 */
@State(
    name = JavaDocConfiguration.COMPONENT_NAME,
    storages = @Storage("intellij-javadocs2.xml")
)
public class JavaDocConfigurationImpl implements JavaDocConfiguration,
    PersistentStateComponent<Element> {

    private static final Logger LOGGER = Logger.getInstance(JavaDocConfigurationImpl.class);

    private JavaDocSettings settings;
    private final DocTemplateManager templateManager;
    private boolean loadedStoredConfig = false;

    /**
     * Instantiates a new Java doc configuration object.
     */
    public JavaDocConfigurationImpl() {
        this.templateManager = ApplicationManager.getApplication().getComponent(DocTemplateManager.class);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public JavaDocSettings getConfiguration() {
        JavaDocSettings result;
        try {
            result = (JavaDocSettings) this.getSettings().clone();
        } catch (Exception e) {
            // return null if cannot clone object
            result = null;
        }
        return result;
    }

    @Nullable
    @Override
    public Element getState() {
        Element root = new Element("JAVA_DOC_SETTINGS_PLUGIN");
        if (this.settings != null) {
            this.settings.addToDom(root);
            this.loadedStoredConfig = true;
        }
        return root;
    }

    @Override
    public void loadState(@NotNull Element javaDocSettings) {
        this.settings = new JavaDocSettings(javaDocSettings);
        this.setupTemplates();
        this.loadedStoredConfig = true;
    }

    @Override
    public JavaDocSettings getSettings() {
        if (!this.loadedStoredConfig) {
            // setup default values
            this.settings = new JavaDocSettings();
            Set<Level> levels = new HashSet<>();
            levels.add(Level.TYPE);
            levels.add(Level.METHOD);
            levels.add(Level.FIELD);

            Set<Visibility> visibilities = new HashSet<>();
            visibilities.add(Visibility.PUBLIC);
            visibilities.add(Visibility.PROTECTED);
            visibilities.add(Visibility.DEFAULT);

            this.settings.getGeneralSettings().setOverriddenMethods(false);
            this.settings.getGeneralSettings().setSplittedClassName(true);
            this.settings.getGeneralSettings().setMode(Mode.UPDATE);
            this.settings.getGeneralSettings().setLevels(levels);
            this.settings.getGeneralSettings().setVisibilities(visibilities);

            this.settings.getTemplateSettings().setClassTemplates(this.templateManager.getClassTemplates());
            this.settings.getTemplateSettings().setConstructorTemplates(this.templateManager.getConstructorTemplates());
            this.settings.getTemplateSettings().setMethodTemplates(this.templateManager.getMethodTemplates());
            this.settings.getTemplateSettings().setFieldTemplates(this.templateManager.getFieldTemplates());
        }
        return this.settings;
    }

    @Override
    public void setupTemplates() {
        try {
            this.templateManager.setClassTemplates(this.settings.getTemplateSettings().getClassTemplates());
            this.templateManager.setConstructorTemplates(this.settings.getTemplateSettings().getConstructorTemplates());
            this.templateManager.setMethodTemplates(this.settings.getTemplateSettings().getMethodTemplates());
            this.templateManager.setFieldTemplates(this.settings.getTemplateSettings().getFieldTemplates());
        } catch (SetupTemplateException e) {
            LOGGER.error(e);
            Messages.showErrorDialog("Javadocs plugin is not available, cause: " + e.getMessage(), "Javadocs plugin");
        }
    }

}
