// Copyright 2013 Christopher "Kasoki" Kaster <http://kasoki.de>
//
// This project is hosted at Github <https://github.com/Kasoki/jfeedly>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// <http://www.apache.org/licenses/LICENSE-2.0>
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
