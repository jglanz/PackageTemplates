package global.wrappers;

import base.ElementVisitor;
import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EditorTextField;
import core.actions.newPackageTemplate.dialogs.select.binaryFile.SelectBinaryFileDialog;
import core.actions.newPackageTemplate.dialogs.select.fileTemplate.SelectFileTemplateDialog;
import core.script.ScriptDialog;
import core.search.customPath.CustomPath;
import core.search.customPath.dialog.CustomPathDialog;
import core.writeRules.dialog.WriteRulesDialog;
import global.listeners.ClickListener;
import global.models.BaseElement;
import core.writeRules.WriteRules;
import global.models.BinaryFile;
import global.models.File;
import global.utils.Logger;
import global.utils.factories.GsonFactory;
import global.utils.factories.WrappersFactory;
import global.utils.i18n.Localizer;
import global.utils.templates.FileTemplateHelper;
import global.views.IconLabel;
import global.views.IconLabelCustom;
import icons.PluginIcons;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Created by CeH9 on 06.07.2016.
 */
public abstract class ElementWrapper extends BaseWrapper {

    private DirectoryWrapper parent;
    private PackageTemplateWrapper packageTemplateWrapper;


    //=================================================================
    //  Abstraction
    //=================================================================
    public abstract void accept(ElementVisitor visitor);

    public abstract void buildView(Project project, JPanel container);

    public abstract void removeMyself();

    public abstract void addElement(ElementWrapper element);

    public abstract BaseElement getElement();

    public abstract boolean isDirectory();

    public abstract ValidationInfo validateFields();

    public abstract void setEnabled(boolean isEnabled);


    //=================================================================
    //  UI
    //=================================================================
    public JLabel jlName;
    public EditorTextField etfDescription;
    public IconLabel jlCustomPath;
    public IconLabelCustom<? extends BaseElement> jlWriteRules;

    private void createPopupForEditMode(MouseEvent mouseEvent) {
        JPopupMenu popupMenu = new JBPopupMenu();

        JMenuItem itemAddFile = new JBMenuItem(Localizer.get("AddFile"), AllIcons.FileTypes.Text);
        JMenuItem itemAddDirectory = new JBMenuItem(Localizer.get("AddDirectory"), AllIcons.Nodes.Package);
        JMenuItem itemAddBinaryFile = new JBMenuItem(Localizer.get("action.AddBinaryFile"), AllIcons.FileTypes.Text);
        JMenuItem itemEditSourcePath = new JBMenuItem(Localizer.get("action.EditSourcePath"), AllIcons.FileTypes.Text);
        JMenuItem itemChangeFileTemplate = new JBMenuItem(Localizer.get("action.ChangeFileTemplate"), AllIcons.Actions.Edit);
        JMenuItem itemDelete = new JBMenuItem(Localizer.get("Delete"), AllIcons.General.Remove);

        itemAddFile.addActionListener(e -> AddFile());
        itemAddDirectory.addActionListener(e -> addDirectory());
        itemAddBinaryFile.addActionListener(e -> addBinaryFile());
        itemDelete.addActionListener(e -> deleteElement());

        popupMenu.add(itemAddFile);
        popupMenu.add(itemAddDirectory);
        popupMenu.add(itemAddBinaryFile);

        // if NOT root element
        if (getParent() != null) {
            popupMenu.add(itemDelete);
        }

        // Dir Specific
        if (isDirectory()) {
            //nothing
        } else {
            // File Specific
            if (this instanceof FileWrapper) {
                itemChangeFileTemplate.addActionListener(e -> changeFileTemplate());
                popupMenu.add(itemChangeFileTemplate);
            } else if (this instanceof BinaryFileWrapper){
                itemEditSourcePath.addActionListener(e -> changeFileTemplate());
                popupMenu.add(itemEditSourcePath);
            }
        }

        addScriptMenuItems(popupMenu);
        addCustomPathMenuItems(popupMenu);
        addWriteRulesMenuItems(popupMenu);

        popupMenu.show(jlName, mouseEvent.getX(), mouseEvent.getY());
    }

    public void reBuildEllements() {
        packageTemplateWrapper.reBuildElements();
    }


    //=================================================================
    //  Menu items
    //=================================================================
    private void addScriptMenuItems(JPopupMenu popupMenu) {
        // With Script
        if (getElement().getScript() != null && !getElement().getScript().isEmpty()) {
            JMenuItem itemEdit = new JBMenuItem(Localizer.get("EditScript"), PluginIcons.SCRIPT);
            JMenuItem itemDelete = new JBMenuItem(Localizer.get("DeleteScript"), AllIcons.General.Remove);

            itemEdit.addActionListener(e -> new ScriptDialog(
                    getPackageTemplateWrapper().getProject(), getElement().getScript()) {
                @Override
                public void onSuccess(String code) {
                    getElement().setScript(code);
                    updateComponentsState();
                }
            }.show());

            itemDelete.addActionListener(e -> {
                getElement().setScript("");
                updateComponentsState();
            });

            popupMenu.add(itemEdit);
            popupMenu.add(itemDelete);
        } else {
            // Without Script
            JMenuItem itemAddScript = new JBMenuItem(Localizer.get("AddScript"), PluginIcons.SCRIPT);

            itemAddScript.addActionListener(e -> new ScriptDialog(getPackageTemplateWrapper().getProject()) {
                @Override
                public void onSuccess(String code) {
                    getElement().setScript(code);
                    updateComponentsState();
                }
            }.show());

            popupMenu.add(itemAddScript);
        }
    }

    private void addCustomPathMenuItems(JPopupMenu popupMenu) {
        if (getElement().getCustomPath() != null) {
            JMenuItem itemEdit = new JBMenuItem(Localizer.get("EditCustomPath"), PluginIcons.CUSTOM_PATH);
            JMenuItem itemDelete = new JBMenuItem(Localizer.get("DeleteCustomPath"), AllIcons.General.Remove);

            CustomPath customPath = getElement().getCustomPath() == null ? null : GsonFactory.cloneObject(getElement().getCustomPath(), CustomPath.class);
            itemEdit.addActionListener(e -> new CustomPathDialog(getPackageTemplateWrapper().getProject(), customPath) {
                @Override
                public void onSuccess(CustomPath customPath) {
                    getElement().setCustomPath(customPath);
                    updateComponentsState();
                }
            }.show());

            itemDelete.addActionListener(e -> {
                getElement().setCustomPath(null);
                updateComponentsState();
            });

            popupMenu.add(itemEdit);
            popupMenu.add(itemDelete);
        } else {
            JMenuItem itemAdd = new JBMenuItem(Localizer.get("AddCustomPath"), PluginIcons.CUSTOM_PATH);

            itemAdd.addActionListener(e -> new CustomPathDialog(getPackageTemplateWrapper().getProject(), null) {
                @Override
                public void onSuccess(CustomPath customPath) {
                    getElement().setCustomPath(customPath);
                    updateComponentsState();
                }
            }.show());

            popupMenu.add(itemAdd);
        }
    }

    private void addWriteRulesMenuItems(JPopupMenu popupMenu) {
        WriteRules writeRules = getElement().getWriteRules();
        if (writeRules == null) {
            Logger.log("ElementWrapper getWriteRules NULL");
            return;
        }

        JMenuItem itemEdit = new JBMenuItem(Localizer.get("EditWriteRules"), writeRules.toIcon());

        itemEdit.addActionListener(e -> new WriteRulesDialog(getPackageTemplateWrapper().getProject(), writeRules, getParent() != null) {
            @Override
            public void onSuccess(WriteRules writeRules) {
                getElement().setWriteRules(writeRules);
                updateComponentsState();
            }
        }.show());

        popupMenu.add(itemEdit);
    }

    protected void updateOptionIcons() {
        if (getElement().getScript() != null && !getElement().getScript().isEmpty()) {
            jlScript.enableIcon();
        } else {
            jlScript.disableIcon();
        }

        if (getElement().getCustomPath() != null) {
            jlCustomPath.enableIcon();
        } else {
            jlCustomPath.disableIcon();
        }

        if (getElement().getWriteRules() != null) {
            jlWriteRules.updateIcon();
        }

    }


    //=================================================================
    //  Utils
    //=================================================================
    public void addMouseListener() {
        jlName.addMouseListener(new ClickListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    switch (getPackageTemplateWrapper().getMode()) {
                        case EDIT:
                        case CREATE:
                            createPopupForEditMode(mouseEvent);
                            break;
                        case USAGE:
                            break;
                    }
                }
            }
        });
    }

    public void addDirectory() {
        getPackageTemplateWrapper().collectDataFromFields();

        DirectoryWrapper dirParent;
        if (isDirectory()) {
            dirParent = ((DirectoryWrapper) this);
        } else {
            dirParent = getParent();
        }

        dirParent.addElement(WrappersFactory.createNewWrappedDirectory(dirParent));
        dirParent.reBuildEllements();
    }

    public void deleteElement() {
        removeMyself();

        getParent().getPackageTemplateWrapper().collectDataFromFields();
        getParent().reBuildEllements();
    }

    public void AddFile() {
        if (!FileTemplateHelper.isCurrentSchemeValid(packageTemplateWrapper.getProject(),
                packageTemplateWrapper.getPackageTemplate().getFileTemplateSource())) {
            return;
        }

        SelectFileTemplateDialog dialog = new SelectFileTemplateDialog(getPackageTemplateWrapper().getProject(), getPackageTemplateWrapper()) {
            @Override
            public void onSuccess(FileTemplate fileTemplate) {
                getPackageTemplateWrapper().collectDataFromFields();

                DirectoryWrapper parent;
                if (isDirectory()) {
                    parent = ((DirectoryWrapper) ElementWrapper.this);
                } else {
                    parent = getParent();
                }
                parent.addElement(WrappersFactory.createNewWrappedFile(parent, fileTemplate.getName(), fileTemplate.getExtension()));
                parent.reBuildEllements();
            }

            @Override
            public void onCancel() {
            }
        };
        dialog.show();
    }

    private void changeFileTemplate() {
        if (!FileTemplateHelper.isCurrentSchemeValid(packageTemplateWrapper.getProject(),
                packageTemplateWrapper.getPackageTemplate().getFileTemplateSource())) {
            return;
        }

        SelectFileTemplateDialog dialog = new SelectFileTemplateDialog(getPackageTemplateWrapper().getProject(), getPackageTemplateWrapper()) {
            @Override
            public void onSuccess(FileTemplate fileTemplate) {
                getPackageTemplateWrapper().collectDataFromFields();

                File file = (File) getElement();
                file.setTemplateName(fileTemplate.getName());
                file.setExtension(fileTemplate.getExtension());

                parent.reBuildEllements();
            }

            @Override
            public void onCancel() {
            }
        };
        dialog.show();
    }

    public void addBinaryFile() {
        SelectBinaryFileDialog dialog = new SelectBinaryFileDialog(getPackageTemplateWrapper().getProject(), null) {
            @Override
            public void onSuccess(BinaryFile binaryFile) {
                Logger.log("onSuccess");
                getPackageTemplateWrapper().collectDataFromFields();

                DirectoryWrapper parent;
                if (isDirectory()) {
                    parent = ((DirectoryWrapper) ElementWrapper.this);
                } else {
                    parent = getParent();
                }
                parent.addElement(WrappersFactory.wrapBinaryFile(parent, binaryFile));
                parent.reBuildEllements();
            }

            @Override
            public void onCancel() {

            }
        };
        dialog.show();
    }

    //=================================================================
    //  Getters | Setters
    //=================================================================
    public DirectoryWrapper getParent() {
        return parent;
    }

    public void setParent(DirectoryWrapper parent) {
        this.parent = parent;
    }

    public PackageTemplateWrapper getPackageTemplateWrapper() {
        return packageTemplateWrapper;
    }

    public void setPackageTemplateWrapper(PackageTemplateWrapper packageTemplateWrapper) {
        this.packageTemplateWrapper = packageTemplateWrapper;
    }


}
