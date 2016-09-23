package org.dstadler.intellij.automation.settings;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;


@State(name = "RESTConfigurationService", storages = @Storage(file = "restautomation.settings.xml"))
public class RESTConfigurationService implements PersistentStateComponent<RESTConfigurationService.State> {
    private State state;

    public static RESTConfigurationService getInstance() {
        return ServiceManager.getService(RESTConfigurationService.class);
    }

    @Override
    @NotNull
    public RESTConfigurationService.State getState() {
        if (this.state == null) {
            this.state = new RESTConfigurationService.State();
        }
        return this.state;
    }

    @Override
    public void loadState(RESTConfigurationService.State state) {
        if (state == null) {
            this.state = new RESTConfigurationService.State();
        }
        this.state = state;
    }

    public static class State {
        @NotNull
        @Property
        private RESTServerSettings server = new RESTServerSettings();

        @NotNull
        public RESTServerSettings getServer() {
            return this.server;
        }
    }
}
