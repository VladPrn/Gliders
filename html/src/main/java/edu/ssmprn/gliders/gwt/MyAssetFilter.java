package edu.ssmprn.gliders.gwt;

import com.badlogic.gdx.backends.gwt.preloader.DefaultAssetFilter;

public class MyAssetFilter extends DefaultAssetFilter {

    @Override
    public boolean preload(String file) {
        return !file.startsWith("mod/");
    }
}
