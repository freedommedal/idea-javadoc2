package com.github.setial.intellijjavadocs.configuration;

import com.github.setial.intellijjavadocs.model.settings.JavaDocSettings;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * The interface Java doc configuration.
 *
 * @author Sergey Timofiychuk
 */
public interface JavaDocConfiguration extends PersistentStateComponent<Element>, BaseComponent {
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
