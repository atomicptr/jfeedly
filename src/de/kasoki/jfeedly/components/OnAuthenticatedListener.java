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
package de.kasoki.jfeedly.components;

import de.kasoki.jfeedly.JFeedly;

/**
 * This listener will be called when the authentication process is done
 * @author Christopher Kaster
 */
public interface OnAuthenticatedListener {
    void onAuthenticated(JFeedly feedly);
}
