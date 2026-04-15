package edu.ssmprn.gliders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LocalizedSkins {

    public static Skin getSkinForSelectedBox() {
        Skin skinStandart = new Skin(Gdx.files.internal("data/uiskin.json"));
        Skin selectedBox = new Skin();
        BitmapFont font = getRussianFont();
        SelectBox.SelectBoxStyle selectStyle = skinStandart.get("default", SelectBox.SelectBoxStyle.class);
        selectStyle.font = font;
        selectStyle.listStyle.font = font;
        selectedBox.add("default", selectStyle, SelectBox.SelectBoxStyle.class);

        return selectedBox;
    }

    public static BitmapFont getRussianFont() {
        return new BitmapFont(Gdx.files.internal("fonts/imperial.fnt"), false);
    }
}
