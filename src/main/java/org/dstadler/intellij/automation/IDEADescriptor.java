package org.dstadler.intellij.automation;

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
import com.intellij.openapi.project.Project;
import org.dstadler.intellij.automation.settings.RESTConfigurationService;
import org.dstadler.intellij.automation.settings.RESTSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.logging.Level;


public class IDEADescriptor {
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("restautomation.eventlog");
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("restautomation.systemlog");

    public static IDEADescriptor getInstance() {
        return ServiceManager.getService(IDEADescriptor.class);
    }

    @NotNull
    public String getVersion() {
        return ApplicationInfo.getInstance().getFullVersion();
    }

    @NotNull
    public String getPluginVersion() {
        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("org.dstadler.intellij.automation"));
        if(plugin == null) {
            return "Unknown";
        }
        return plugin.getVersion();
    }

    public void log(@NotNull Level level, @NotNull String title, @NotNull String content, boolean notification) {
        ApplicationManager.getApplication().invokeLater(() -> {
            NotificationType type = NotificationType.INFORMATION;
            if (level == Level.SEVERE) {
                type = NotificationType.ERROR;
            } else if (level == Level.WARNING) {
                type = NotificationType.WARNING;
            }

            NotificationListener listener = new NotificationListener.Adapter() {
                @Override
                protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                    showSettings();
                }
            };

            final Notification notif;
            if (notification) {
                notif = IMPORTANT_NOTIFICATION_GROUP.createNotification(title, content, type, listener);
            } else {
                notif = INFO_NOTIFICATION_GROUP.createNotification(title, content, type, listener);
            }
            Notifications.Bus.notify(notif);
        });
    }

    public static void showSettings() {
        RESTSettingsConfigurable configurable = new RESTSettingsConfigurable();
        String dimensionKey = ShowSettingsUtilImpl.createDimensionKey(configurable);
        SingleConfigurableEditor singleConfigurableEditor = new SingleConfigurableEditor((Project)null, configurable, dimensionKey, false);
        singleConfigurableEditor.show();
    }
}
