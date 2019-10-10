package dmtr.example.android.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("name")
    private String mName;

    @SerializedName("nativeName")
    private String mNativeName;

    public Language(String name, String nativeName) {
        mName = name;
        mNativeName = nativeName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNativeName() {
        return mNativeName;
    }

    public void setNativeName(String nativeName) {
        mNativeName = nativeName;
    }
}
