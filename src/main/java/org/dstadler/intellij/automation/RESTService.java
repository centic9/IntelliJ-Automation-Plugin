package org.dstadler.intellij.automation;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashMap;
import org.dstadler.commons.http.NanoHTTPD;
import org.dstadler.commons.logging.jdk.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RESTService implements ProjectComponent {
    private final static Logger logger = LoggerFactory.make();
    
    private final Project project;
    private NanoHTTPD nanoHTTPD;

    public RESTService(Project project) {
        logger.info("Started with project " + project + ": " + project.getName());
        this.project = project;
    }

    @Override
    public void initComponent() {
        logger.info("Starting REST server to serve actions via REST");

        Map<String,ActionListener> actionMap = new HashMap<>();

        // map actionIds to REST paths
        actionMap.put("Recompile", new AnActionActionListener("CompileProject"));
        actionMap.put("Compile", new AnActionActionListener("CompileDirty"));

        try {
            nanoHTTPD = RESTServer.create(actionMap);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not start the REST server", e);
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

    @Override
    @NotNull
    public String getComponentName() {
        return "RESTService";
    }

    @Override
    public void projectOpened() {
        // called when project is opened
    }

    @Override
    public void projectClosed() {
        // called when project is being closed
    }

    private class AnActionActionListener implements ActionListener {
        private final String actionId;

        public AnActionActionListener(String actionId) {
            this.actionId = actionId;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            AnActionEvent event = AnActionEvent.createFromDataContext(actionId, null, dataId -> {
                if (dataId.equals(CommonDataKeys.PROJECT.getName())) {
                    return project;
                }
                return null;
            });

            ApplicationManager.getApplication().invokeLater(() -> ActionManager.getInstance().getAction(actionId).actionPerformed(event));
        }
    }
}
