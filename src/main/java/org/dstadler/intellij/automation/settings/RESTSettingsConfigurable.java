package org.dstadler.intellij.automation.settings;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.dstadler.intellij.automation.IDEADescriptor;
import org.dstadler.intellij.automation.Messages;
import org.dstadler.intellij.automation.RESTService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.logging.Level;

public class RESTSettingsConfigurable implements Configurable.NoScroll, Configurable {
    private final RESTConfigurationService provider;
    private RESTSettingsPanel panel;

    public RESTSettingsConfigurable() {
        this.provider = RESTConfigurationService.getInstance();
    }

    private static int checkPort(String strPort, String service) throws ConfigurationException {
        try {
            int port = Integer.parseInt(strPort);
            if (port < 0 || port > 0xFFFF) {
                throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", service));
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(Messages.getMessage("plugin.settings.ui.validation.illegalPort", service));
        }
    }

    @Nls
    @Override
    public String getDisplayName() {
        return Messages.getMessage("plugin.settings.ui.displayName");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (this.panel != null) {
            return this.panel.wholePanel;
        }
        this.panel = new RESTSettingsPanel();

        //add helptext url listener
        /*this.panel.helpText.addHyperlinkListener(hle -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(hle.getURL().toURI());
                } catch (Exception ex) {
                    IDEADescriptor.getInstance().log(Level.WARNING, "Error occured while opening hyperlink", "", ex.getMessage(), false);
                }
            }
        });*/
        return this.panel.wholePanel;
    }

    @Override
    public boolean isModified() {
        RESTConfigurationService.State state = this.provider.getState();

        //agent panel
        try {
            //server panel
            if (state.getServer().getPort() != Integer.parseInt(this.panel.restPort.getText())) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true; //will be validated in apply();
        }
        return false;
    }

    private void applyUIToServerSettings(RESTServerSettings settings) throws ConfigurationException {
        //server panel
        settings.setPort(checkPort(this.panel.restPort.getText(), "Server"));
    }

    @Override
    public void apply() throws ConfigurationException {
        RESTConfigurationService.State state = this.provider.getState();

        this.applyUIToServerSettings(state.getServer());

        final RESTService component = ApplicationManager.getApplication().getComponent(RESTService.class);
        if(component == null) {
            IDEADescriptor.getInstance().log(Level.WARNING, "Settings error", "Could not find component for RESTService", true);
            return;
        }
        component.restart();
    }

    @Override
    //reset does a rollback to the previous configuration
    public void reset() {
        RESTConfigurationService.State state = this.provider.getState();

        //server
        this.panel.restPort.setText(String.valueOf(state.getServer().getPort()));

        this.panel.helpText.setContentType("text/html");
        this.panel.helpText.setEditable(false);
        this.panel.helpText.setOpaque(false);
        this.panel.helpText.setText(Messages.getMessage("plugin.settings.ui.help", this.panel.helpText.getFont().getFamily()));
        //ApplicationManager.getApplication().invokeLater(this::createComponent);
    }

    @Override
    public void disposeUIResources() {
        this.panel = null;
    }

    public static class RESTSettingsPanel {
        private JTextField restPort;

        private JPanel wholePanel;
        private JEditorPane helpText;

        private void createUIComponents() {}
    }
}
