package br.com.lisboa.ikigai;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AddedStrings {
    public String value;

    public AddedStrings(String value) {
        this.value = value;
    }

    public AddedStrings() {

    }

    public String getValue() {
        return value;
    }
}
