package cz.muni.fi.pv256.movio2.uco_422476;

/**
 * Created by Matej on 7.1.2018.
 */

public class Category implements ListItem {
    private String mName;

    public Category(String name) {
        mName = name;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }
}
