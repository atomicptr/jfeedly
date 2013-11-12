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

public class Subscriptions implements Iterable<Subscription> {
    private ArrayList<Subscription> subscriptions;

    private Subscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Iterator<Subscription> iterator() {
        return subscriptions.iterator();
    }

    public int getNumberOfSubscriptions() {
        return subscriptions.size();
    }

    public Subscription get(int index) {
        return subscriptions.get(index);
    }

    public Subscription getById(String id) {
        for(Subscription s : this.subscriptions) {
            if(s.getId().equals(id)) {
                return s;
            }
        }

        return null;
    }

    public static Subscriptions fromJSONArray(JSONArray array) {
        ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            subscriptions.add(Subscription.fromJSONObject(object));
        }

        return new Subscriptions(subscriptions);
    }

}
