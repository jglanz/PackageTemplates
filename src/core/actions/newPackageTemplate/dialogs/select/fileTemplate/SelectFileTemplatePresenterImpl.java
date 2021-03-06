package core.actions.newPackageTemplate.dialogs.select.fileTemplate;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.util.ArrayUtil;
import global.models.TemplateForSearch;
import global.utils.i18n.Localizer;

import java.util.ArrayList;

/**
 * Created by Arsen on 16.09.2016.
 */
public class SelectFileTemplatePresenterImpl implements SelectFileTemplatePresenter {

    private SelectFileTemplateDialog view;

    public SelectFileTemplatePresenterImpl(SelectFileTemplateDialog view) {
        this.view = view;
        view.setTitle(Localizer.get("SelectFileTemplate"));
    }

    @Override
    public void onSuccess(FileTemplate template) {
        view.onSuccess(template);
    }

    @Override
    public void onCancel() {
        view.onCancel();
    }

    @Override
    public ArrayList<TemplateForSearch> getListTemplateForSearch(boolean addInternal, boolean addJ2EE) {
        FileTemplateManager ftm = FileTemplateManager.getDefaultInstance();
        FileTemplate[] fileTemplates = ftm.getAllTemplates();

        if (addInternal)
            fileTemplates = ArrayUtil.mergeArrays(fileTemplates, ftm.getInternalTemplates());
        if (addJ2EE)
            fileTemplates = ArrayUtil.mergeArrays(fileTemplates, ftm.getTemplates(FileTemplateManager.J2EE_TEMPLATES_CATEGORY));


        ArrayList<TemplateForSearch> listTemplateForSearch = new ArrayList(fileTemplates.length);
        for (FileTemplate template : fileTemplates) {
            listTemplateForSearch.add(new TemplateForSearch(template));
        }
        return listTemplateForSearch;
    }

}
