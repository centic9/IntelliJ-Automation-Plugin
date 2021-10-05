package org.dstadler.intellij.automation;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.containers.HashMap;
import org.dstadler.commons.http.NanoHTTPD;
import org.dstadler.commons.logging.jdk.LoggerFactory;
import org.dstadler.intellij.automation.settings.RESTConfigurationService;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RESTService implements ApplicationComponent {
    private final static Logger logger = LoggerFactory.make();

    private NanoHTTPD nanoHTTPD;

    @Override
    public void initComponent() {
        final int port = RESTConfigurationService.getInstance().getState().getServer().getPort();
        logger.info("Starting REST server on port " + port + " to serve actions via REST");

        Map<String,ActionListener> actionMap = new HashMap<>();

        // map actionIds to REST paths
        actionMap.put("Recompile", new AnActionActionListener("CompileProject"));
        actionMap.put("Compile", new AnActionActionListener("CompileDirty"));
        actionMap.put("VcsRefresh", new AnActionActionListener("Vcs.RefreshStatuses"));

        try {
            nanoHTTPD = RESTServer.create(port, actionMap);
        } catch (IOException e) {
            IDEADescriptor.getInstance().log(Level.SEVERE, "Could not start the REST server", "Error: " + e + "<br>\n<a href=\"#\">Fix Configuration</a>", true);
        }
    }

    @Override
    public void disposeComponent() {
        // shut down the REST interface
        if(nanoHTTPD != null) {
            logger.info("Shutting down REST Server");
            nanoHTTPD.stop();
            nanoHTTPD = null;
        }
    }

    public void restart() {
        disposeComponent();
        initComponent();
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "RESTService";
    }

    private static class AnActionActionListener implements ActionListener {
        private final String actionId;

        public AnActionActionListener(String actionId) {
            this.actionId = actionId;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // send a refresh-action for each Project
            for(Project project : ProjectManager.getInstance().getOpenProjects()) {
                AnActionEvent event = AnActionEvent.createFromDataContext(actionId, null, dataId -> {
                    if (dataId.equals(CommonDataKeys.PROJECT.getName())) {
                        return project;
                    }
                    return null;
                });

				final Application application = ApplicationManager.getApplication();
				application.invokeLater(() -> {
					final ActionManager actionManager = ActionManager.getInstance();
					if (actionManager == null) {
						logger.warning("Could not execute action " + actionId + ", did not get ActionManager");
						return;
					}

					final AnAction action = actionManager.getAction(actionId);
					if (action == null) {
						logger.warning("Could not execute action " + actionId + ", did not find a corresponding action");
						return;
					}
					action.actionPerformed(event);
				});
            }
        }
    }
}
