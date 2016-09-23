package org.dstadler.intellij.automation;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;


public class IDEADescriptor {
    public static final NotificationGroup IMPORTANT_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("restautomation.eventlog");
    public static final NotificationGroup INFO_NOTIFICATION_GROUP = NotificationGroup.logOnlyGroup("restautomation.systemlog");
    private static final String NOTIFICATION_FORMAT = "<b>%s</b><br><i>%s</i><br>%s";
    private static final String LOG_FORMAT = "[%s](%s) - %s";

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

    public void log(@NotNull Level level, @NotNull String title, @Nullable String subtitle, @NotNull String content, boolean notification) {
        ApplicationManager.getApplication().invokeLater(() -> {
            NotificationType type = NotificationType.INFORMATION;
            if (level == Level.SEVERE) {
                type = NotificationType.ERROR;
            } else if (level == Level.WARNING) {
                type = NotificationType.WARNING;
            }
            Notification notif;
            if (notification) {
                notif = IMPORTANT_NOTIFICATION_GROUP.createNotification(String.format(NOTIFICATION_FORMAT, title, subtitle, content), type);
            } else {
                notif = INFO_NOTIFICATION_GROUP.createNotification(String.format(LOG_FORMAT, title, subtitle, content), type);
            }
            Notifications.Bus.notify(notif);
        });
    }
}
