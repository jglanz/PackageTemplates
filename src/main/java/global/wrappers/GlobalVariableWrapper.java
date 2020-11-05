package global.wrappers;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.EditorTextField;
import core.textInjection.VelocityHelper;
import global.listeners.ClickListener;
import core.script.ScriptDialog;
import core.script.ScriptExecutor;
import global.utils.text.StringTools;
import global.utils.highligt.HighlightHelper;
import global.views.IconLabel;
import icons.PluginIcons;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import global.models.GlobalVariable;
import global.utils.i18n.Localizer;
import global.utils.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import static global.utils.UIHelper.PADDING_LABEL;
import static global.wrappers.PackageTemplateWrapper.ATTRIBUTE_BASE_NAME;
import static global.wrappers.PackageTemplateWrapper.PATTERN_BASE_NAME;

/**
 * Created by CeH9 on 06.07.2016.
 */
public class GlobalVariableWrapper extends BaseWrapper {

    private GlobalVariable globalVariable;

    //=================================================================
    //  UI
    //=================================================================
    private EditorTextField tfKey;
    private EditorTextField tfValue;
    private JLabel jlVariable;

    public void buildView(PackageTemplateWrapper ptWrapper, JPanel container) {
        jlVariable = new JLabel(AllIcons.Nodes.Variable, JLabel.LEFT);
        jlVariable.setDisabledIcon(jlVariable.getIcon());
        jlVariable.setText("variable");

        tfKey = new EditorTextField(globalVariable.getName());
        tfKey.setAlignmentX(Component.LEFT_ALIGNMENT);
//        UIHelper.setRightPadding(tfKey, PADDING_LABEL);

        tfValue = UIHelper.getEditorTextField(globalVariable.getValue(), ptWrapper.getProject());

        if (ptWrapper.getMode() == PackageTemplateWrapper.ViewMode.USAGE) {
            tfKey.setEnabled(false);
        } else {
            jlVariable.addMouseListener(new ClickListener() {
                @Override
                public void mouseClicked(MouseEvent eventOuter) {
                    if (SwingUtilities.isRightMouseButton(eventOuter)) {
                        JPopupMenu popupMenu = getPopupMenu(ptWrapper);
                        popupMenu.show(jlVariable, eventOuter.getX(), eventOuter.getY());
                    }
                }
            });
        }

        // Lock modifying BASE_NAME
        if (getGlobalVariable().getName().equals(ATTRIBUTE_BASE_NAME)) {
            tfKey.setEnabled(false);
        }

        container.add(createOptionsBlock(), new CC().spanX().split(3));
        container.add(tfKey, new CC().width("0").pushX().growX());
        container.add(tfValue, new CC().width("0").pushX().growX().wrap());
    }

    @NotNull
    private JPanel createOptionsBlock() {
        JPanel optionsPanel = new JPanel(new MigLayout(new LC()));

        // Script
        jlScript = new IconLabel(
                Localizer.get("tooltip.ColoredWhenItemHasScript"),
                PluginIcons.SCRIPT,
                PluginIcons.SCRIPT_DISABLED
        );

        updateComponentsState();

        optionsPanel.add(jlScript, new CC().pad(0, 0, 0, 6));
        optionsPanel.add(jlVariable, new CC());
        return optionsPanel;
    }

    @NotNull
    private JPopupMenu getPopupMenu(final PackageTemplateWrapper ptWrapper) {
        JPopupMenu popupMenu = new JBPopupMenu();

        JMenuItem itemAddVariable = new JBMenuItem(Localizer.get("AddVariable"), AllIcons.Nodes.Variable);
        JMenuItem itemDelete = new JBMenuItem(Localizer.get("Delete"), AllIcons.General.Remove);

        itemAddVariable.addActionListener(e -> addVariable(ptWrapper));
        itemDelete.addActionListener(e -> deleteVariable(ptWrapper));

        popupMenu.add(itemAddVariable);
        addScriptMenuItems(popupMenu, ptWrapper.getProject());
        if (!getGlobalVariable().getName().equals(ATTRIBUTE_BASE_NAME)) {
            popupMenu.add(itemDelete);
        }
        return popupMenu;
    }

    private void addScriptMenuItems(JPopupMenu popupMenu, Project project) {
        if (globalVariable.getScript() != null && !globalVariable.getScript().isEmpty()) {
            JMenuItem itemEditScript = new JBMenuItem(Localizer.get("EditScript"), PluginIcons.SCRIPT);
            itemEditScript.addActionListener(e -> new ScriptDialog(project, globalVariable.getScript()) {
                @Override
                public void onSuccess(String code) {
                    globalVariable.setScript(code);
                    updateComponentsState();
                }
            }.show());
            popupMenu.add(itemEditScript);

            JMenuItem itemDeleteScript = new JBMenuItem(Localizer.get("DeleteScript"), AllIcons.General.Remove);
            itemDeleteScript.addActionListener(e -> {
                globalVariable.setScript("");
                updateComponentsState();
            });
            popupMenu.add(itemDeleteScript);
        } else {
            JMenuItem itemAddScript = new JBMenuItem(Localizer.get("AddScript"), PluginIcons.SCRIPT);
            itemAddScript.addActionListener(e -> new ScriptDialog(project) {
                @Override
                public void onSuccess(String code) {
                    globalVariable.setScript(code);
                    updateComponentsState();
                }
            }.show());
            popupMenu.add(itemAddScript);
        }
    }


    //=================================================================
    //  Utils
    //=================================================================
    private void deleteVariable(PackageTemplateWrapper ptWrapper) {
        ptWrapper.removeGlobalVariable(this);

        ptWrapper.collectDataFromFields();
        ptWrapper.reBuildGlobals();
    }

    private void addVariable(PackageTemplateWrapper ptWrapper) {
        ptWrapper.collectDataFromFields();

        GlobalVariable gVariable = new GlobalVariable();
        gVariable.setName("UNNAMED_VARIABLE");
        gVariable.setValue("");
        gVariable.setEnabled(true);
        gVariable.setScript("");

        ptWrapper.addGlobalVariable(new GlobalVariableWrapper(gVariable));
        ptWrapper.reBuildGlobals();
    }

    public void collectDataFromFields() {
        globalVariable.setName(tfKey.getText());
        globalVariable.setValue(tfValue.getText());
    }

    public void runScript() {
        if (globalVariable.getScript() != null && !globalVariable.getScript().isEmpty()) {
            globalVariable.setValue(ScriptExecutor.runScript(globalVariable.getScript(), globalVariable.getValue()));
        }
    }

    @Override
    public void updateComponentsState() {
        if (globalVariable.getScript() != null && !globalVariable.getScript().isEmpty()) {
            jlScript.enableIcon();
        } else {
            jlScript.disableIcon();
        }
    }

    public void evaluteVelocity(HashMap<String, String> map) {
        globalVariable.setValue(VelocityHelper.fromTemplate(globalVariable.getValue(), map));
    }


    //=================================================================
    //  Getter | Setter
    //=================================================================
    public GlobalVariableWrapper(GlobalVariable globalVariable) {
        this.globalVariable = globalVariable;
    }

    public GlobalVariable getGlobalVariable() {
        return globalVariable;
    }

    public void setGlobalVariable(GlobalVariable globalVariable) {
        this.globalVariable = globalVariable;
    }

    public EditorTextField getTfKey() {
        return tfKey;
    }

    public EditorTextField getTfValue() {
        return tfValue;
    }

}
