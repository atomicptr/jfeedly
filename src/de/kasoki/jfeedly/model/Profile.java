package de.kasoki.jfeedly.model;

import org.json.JSONObject;

public class Profile {
    private String id;
    private String email;
    private String givenName;
    private String familyName;
    private String locale;
    private String wave;

    public Profile(String id, String email, String givenName, String familyName,
                   String locale, String wave) {
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.locale = locale;
        this.wave = wave;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getLocale() {
        return locale;
    }

    public String getWave() {
        return wave;
    }

    public static Profile fromJSONObject(JSONObject object) {
        return new Profile(object.getString("id"), object.getString("email"), object.getString("givenName"),
                object.getString("familyName"), object.getString("locale"),
                object.getString("wave"));
    }
}
