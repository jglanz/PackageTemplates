package global.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by CeH9 on 06.07.2016.
 */
public class Directory extends BaseElement {

    @Expose @SerializedName("listBaseElement") private ArrayList<BaseElement> listBaseElement;

    public ArrayList<BaseElement> getListBaseElement() {
        return listBaseElement;
    }

    public void setListBaseElement(ArrayList<BaseElement> listBaseElement) {
        this.listBaseElement = listBaseElement;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

}
