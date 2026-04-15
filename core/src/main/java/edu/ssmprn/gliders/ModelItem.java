package edu.ssmprn.gliders;

import java.util.List;

public class ModelItem {
    private String name;
    private String path;
    private String anim;
    private int index;
    private List<ModelItem> models;

    public ModelItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAnim() {
        return anim;
    }

    public void setAnim(String anim) {
        this.anim = anim;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ModelItem> getModels() {
        return models;
    }

    public void setModels(List<ModelItem> models) {
        this.models = models;
    }
}
