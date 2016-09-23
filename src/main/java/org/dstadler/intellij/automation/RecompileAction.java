package org.dstadler.intellij.automation;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to start a full recompile of the current project
 */
public class RecompileAction extends AnAction {
    public RecompileAction() {
        super("Recompile all");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        /*Project project = e.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());*/

        AnActionEvent event = AnActionEvent.createFromDataContext("REcompileAction", null, new DataContext() {
            @Nullable
            @Override
            public Object getData(@NonNls String dataId) {
                /*if(dataId.equals(CommonDataKeys.PROJECT)) {
                    return e.getData();
                }*/
                return null;
            }

            @Nullable
            @Override
            public <T> T getData(@NotNull DataKey<T> key) {
                return e.getData(key);
            }
        });
        ActionManager.getInstance().getAction("CompileProject").actionPerformed(event);
    }


}
