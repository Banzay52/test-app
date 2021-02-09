/*
 * Copyright 2017 insign gmbh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package inject;

import ch.insign.playauth.authz.AuthorizationHandler;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import scala.collection.Seq;
import util.DemoProjectAuthorizationHandler;

public class DemoProjectAuthorizationHandlerModule extends play.api.inject.Module {
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(bind(AuthorizationHandler.class).to(DemoProjectAuthorizationHandler.class));
    }
}
