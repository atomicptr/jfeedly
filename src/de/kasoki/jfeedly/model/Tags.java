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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Tags implements Iterable<Tag> {
    private ArrayList<Tag> tags;

    private Tags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Iterator<Tag> iterator() {
        return tags.iterator();
    }

    public int getNumberOfTags() {
        return tags.size();
    }

    public Tag get(int index) {
        return tags.get(index);
    }

    public Tag getById(String id) {
        for(Tag t : this.tags) {
            if(t.getId().equals(id)) {
                return t;
            }
        }

        return null;
    }

    public static Tags fromJSONArray(JSONArray array) {
        ArrayList<Tag> tags = new ArrayList<Tag>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            tags.add(Tag.fromJSONObject(object));
        }

        return new Tags(tags);
    }

}
