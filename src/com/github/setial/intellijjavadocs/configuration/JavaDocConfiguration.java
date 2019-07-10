package com.github.setial.intellijjavadocs.configuration;

import com.github.setial.intellijjavadocs.model.settings.JavaDocSettings;
import com.intellij.openapi.components.BaseComponent;
import org.jetbrains.annotations.Nullable;

/**
 * The interface Java doc configuration.
 *
 * @author Sergey Timofiychuk
 */
public interface JavaDocConfiguration extends BaseComponent {

    /**
     * The constant COMPONENT_NAME.
     */
    String COMPONENT_NAME = "IntellijJavadocs2_1.0.0";

    /**
     * Gets the configuration.
     *
     * @return the Configuration
     */
    @Nullable
    JavaDocSettings getConfiguration();

    JavaDocSettings getSettings();

    void setupTemplates();
}
