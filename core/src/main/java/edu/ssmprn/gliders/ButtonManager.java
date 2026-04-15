package edu.ssmprn.gliders;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

import java.util.ArrayList;
import java.util.List;

public class ButtonManager {

    private final ButtonContainer container;
    private final List<SelectBox<String>> buttonPool = new ArrayList<>();
    private int currentVisibleCount = 0;

    public ButtonManager(ButtonContainer container) {
        this.container = container;
    }

    public void setButtons(List<List<String>> labels, List<Integer> selectedPath) {
        int newCount = labels.size();

        if (buttonPool.size() < newCount) {
            for (int i = buttonPool.size(); i < newCount; i++) {
                buttonPool.add(container.createButton(i));
            }
        }

        for (int i = 0; i < newCount; i++) {
            SelectBox<String> button = buttonPool.get(i);
            button.setItems(labels.get(i).toArray(new String[0]));
            button.setSelectedIndex(selectedPath.get(i));
        }

        if (newCount > currentVisibleCount) {
            for (int i = currentVisibleCount; i < newCount; i++) {
                container.addButton(buttonPool.get(i), i);
            }
        } else if (newCount < currentVisibleCount) {
            for (int i = newCount; i < currentVisibleCount; i++) {
                container.removeButton(buttonPool.get(i));
            }
        }

        currentVisibleCount = newCount;
    }

    public interface ButtonContainer {
        SelectBox<String> createButton(int index);

        void addButton(SelectBox<String> button, int index);

        void removeButton(SelectBox<String> button);
    }
}
