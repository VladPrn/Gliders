package edu.ssmprn.gliders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class GliderLoader {

    private static final String FILE_MODELS = "models.json";

    private static final String PARSER_NAME = "name";
    private static final String PARSER_PATH = "path";
    private static final String PARSER_ANIM = "anim";
    private static final String PARSER_INDEX = "index";
    private static final String PARSER_MODELS = "models";


    private ModelItem model;
    private final List<Integer> selectedPath = new ArrayList<>();

    public void init() {
        model = loadModel();
        selectPath(0, 0);
    }

    public List<List<String>> getMenu() {
        List<List<String>> result = new ArrayList<>();
        ModelItem currentItem = model;
        for (Integer index : selectedPath) {
            List<ModelItem> models = null;
            if (currentItem != null) {
                models = currentItem.getModels();
            }

            List<String> names = new ArrayList<>();
            if (models != null) {
                for (ModelItem m : models) {
                    String name = m.getName();
                    names.add(name != null ? name : "");
                }
            }
            result.add(names);

            if (models != null && index < models.size()) {
                currentItem = models.get(index);
            } else {
                currentItem = null;
            }
        }

        return result;
    }

    public List<Integer> getSelectedPath() {
        return new ArrayList<>(selectedPath);
    }

    public ModelItem getSelectedGlider() {
        ModelItem currentItem = model;
        for (Integer index : selectedPath) {
            List<ModelItem> models = null;
            if (currentItem != null) {
                models = currentItem.getModels();
            }

            if (models != null && index < models.size()) {
                currentItem = models.get(index);
            } else {
                currentItem = null;
            }
        }

        return currentItem;
    }

    public void selectPath(int index, int item) {
        List<Integer> newPath;
        if (index < selectedPath.size()) {
            newPath = new ArrayList<>(selectedPath.subList(0, index));
        } else {
            newPath = new ArrayList<>();
        }
        newPath.add(index, item);

        ModelItem currentItem = model;
        for (Integer idx : newPath) {
            List<ModelItem> models = null;
            if (currentItem != null) {
                models = currentItem.getModels();
            }

            if (models != null && idx < models.size()) {
                currentItem = models.get(idx);
            }
        }

        while (currentItem != null && currentItem.getModels() != null && !currentItem.getModels().isEmpty()) {
            currentItem = currentItem.getModels().get(0);
            newPath.add(0);
        }

        if (currentItem != null) {
            selectedPath.clear();
            selectedPath.addAll(newPath);
        }
    }

    private ModelItem loadModel() {
        FileHandle fileHandle = Gdx.files.internal(FILE_MODELS);
        JsonReader jsonReader = new JsonReader();
        JsonValue json = jsonReader.parse(fileHandle);
        return parseItem(json);
    }

    private ModelItem parseItem(JsonValue json) {
        ModelItem res = new ModelItem();
        if (json.has(PARSER_NAME)) {
            res.setName(json.getString(PARSER_NAME));
        }
        if (json.has(PARSER_PATH)) {
            res.setPath(json.getString(PARSER_PATH));
        }
        if (json.has(PARSER_ANIM)) {
            res.setAnim(json.getString(PARSER_ANIM));
        }
        if (json.has(PARSER_INDEX)) {
            res.setIndex(json.getInt(PARSER_INDEX));
        }
        if (json.has(PARSER_MODELS)) {
            ArrayList<ModelItem> models = new ArrayList<>();
            JsonValue modelsJson = json.get(PARSER_MODELS);
            for (JsonValue model : modelsJson) {
                models.add(parseItem(model));
            }
            res.setModels(models);
        }
        return res;
    }
}
