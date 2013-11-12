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

public class Categories implements Iterable<Category> {
    private ArrayList<Category> categories;

    private Categories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public Iterator<Category> iterator() {
        return categories.iterator();
    }

    public int getNumberOfCategories() {
        return categories.size();
    }

    public Category get(int index) {
        return categories.get(index);
    }

    public Category getById(String id) {
        for(Category c : this.categories) {
            if(c.getCategoryId().equals(id)) {
                return c;
            }
        }

        return null;
    }

    public static Categories fromJSONArray(JSONArray array) {
        ArrayList<Category> categories = new ArrayList<Category>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            categories.add(Category.fromJSONObject(object));
        }

        return new Categories(categories);
    }

}
