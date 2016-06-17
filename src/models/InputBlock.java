package models;

import com.intellij.ide.fileTemplates.ui.CreateFromTemplatePanel;
import com.intellij.ui.EditorTextField;
import utils.UIMaker;

/**
 * Created by Arsen on 15.06.2016.
 */

public class InputBlock {

    private TemplateElement element;
    private EditorTextField tfName;
    private CreateFromTemplatePanel panelVariables;

    public InputBlock(TemplateElement element, int paddingScale, String[] unsetAttributes) {
        this.element = element;
        this.tfName = UIMaker.getEditorTextField(element.getName(), paddingScale);
        //optionally add TextFields for Attributes
        if (unsetAttributes != null && unsetAttributes.length > 0){
            panelVariables = new CreateFromTemplatePanel(unsetAttributes, false, null);
        }
    }

    public TemplateElement getElement() {
        return element;
    }

    public void setElement(TemplateElement element) {
        this.element = element;
    }

    public EditorTextField getTfName() {
        return tfName;
    }

    public void setTfName(EditorTextField tfName) {
        this.tfName = tfName;
    }

    public CreateFromTemplatePanel getPanelVariables() {
        return panelVariables;
    }
}
