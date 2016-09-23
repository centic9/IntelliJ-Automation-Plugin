package org.dstadler.intellij.automation;

import com.intellij.compiler.actions.CompileProjectAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.HashMap;
import org.dstadler.commons.http.NanoHTTPD;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

public class RESTService implements ProjectComponent {
    private final Project project;
    private NanoHTTPD nanoHTTPD;

    public RESTService(Project project) {
        System.out.println("Started with project " + project + ": " + project.getName());
        this.project = project;
    }

    @Override
    public void initComponent() {
        //e.getActionManager().tryToExecute(e.getActionManager().getAction("CompileProject"), new MenuKeyEvent(), null, null, true);
        // TODO: insert component initialization logic here

        System.out.println("Had action: " + ApplicationManager.getApplication().getComponent(CompileProjectAction.class));
        System.out.println("Had 2action: " + ActionManager.getInstance().getAction("CompileProject"));

        Map<String,ActionListener> actionMap = new HashMap<>();
        actionMap.put("Recompile", new RecompileActionListener());
        actionMap.put("Compile", new CompileActionListener());

        try {
            nanoHTTPD = RESTServer.create(actionMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disposeComponent() {
        // shut down the REST interface
        if(nanoHTTPD != null) {
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

    private class RecompileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AnActionEvent event = AnActionEvent.createFromDataContext("RecompileAction", null, dataId -> {
                if (dataId.equals(CommonDataKeys.PROJECT.getName())) {
                    return project;
                }
                return null;
            });

            ApplicationManager.getApplication().invokeLater(() -> ActionManager.getInstance().getAction("CompileProject").actionPerformed(event));
        }
    }

    private class CompileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AnActionEvent event = AnActionEvent.createFromDataContext("CompileAction", null, dataId -> {
                if (dataId.equals(CommonDataKeys.PROJECT.getName())) {
                    return project;
                }
                return null;
            });

            ApplicationManager.getApplication().invokeLater(() -> ActionManager.getInstance().getAction("CompileDirty").actionPerformed(event));
        }
    }
}
