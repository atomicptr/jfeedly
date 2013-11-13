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

/**
 * Representation model of a tag
 * @author Christopher Kaster
 */
public class Tag {

    private String id;
    private String label;

    private Tag(String id, String label) {
        this.id = id;
        this.label = label;
    }

    /** Returns the ID of this tag */
    public String getId() {
        return this.id;
    }

    /** Returns the label of this tag */
    public String getLabel() {
        return this.label;
    }

    /** Create a new tag from a given JSON object */
    public static Tag fromJSONObject(JSONObject object) {
        String id = object.getString("id");

        String label = null;

        if(object.has("label")) {
            label = object.getString("label");
        }

        return new Tag(id, label);
    }
}
