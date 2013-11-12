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
import java.util.Collections;
import java.util.Iterator;

public class Entries implements Iterable<Entry> {
    private ArrayList<Entry> entries;

    private Entries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public int getNumberOfEntries() {
        return entries.size();
    }

    public Entry get(int index) {
        return entries.get(index);
    }

    public Entry getById(String id) {
        for(Entry t : this.entries) {
            if(t.getId().equals(id)) {
                return t;
            }
        }

        return null;
    }

    public void sortByDateOldestFirst() {
        Collections.sort(entries);
    }

    public void sortByDateNewestFirst() {
        Collections.sort(entries, Collections.reverseOrder());
    }

    public static Entries fromJSONArray(JSONArray array) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            entries.add(Entry.fromJSONObject(object));
        }

        return new Entries(entries);
    }

}
