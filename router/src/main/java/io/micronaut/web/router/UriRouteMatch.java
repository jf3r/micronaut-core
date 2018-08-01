/*
 * Copyright 2017-2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.web.router;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.uri.UriMatchInfo;

import java.util.*;
import java.util.function.Function;

/**
 * A {@link RouteMatch} that matches a URI and {@link HttpMethod}.
 *
 * @param <R> The route
 * @author Graeme Rocher
 * @since 1.0
 */
public interface UriRouteMatch<R> extends UriMatchInfo, MethodBasedRouteMatch<R> {

    /**
     * @return The backing {@link UriRoute}
     */
    UriRoute getRoute();

    /**
     * <p>Returns the required arguments for this RouteMatch.</p>
     * <p>
     * <p>Note that this is not the save as {@link #getArguments()} as it will include a subset of the arguments
     * excluding those that have been subtracted from the URI variables</p>
     *
     * @return The required arguments in order to invoke this route
     */
    default List<Argument> getRequiredArguments() {
        Map<String, Object> matchVariables = getVariables();

        Argument[] arguments = getArguments();
        List<Argument> actualArguments = new ArrayList<>(arguments.length);
        Argument<?> body = getBodyArgument().orElse(null);
        for (Argument argument : arguments) {
            if (!matchVariables.containsKey(argument.getName())) {
                if (body != null && body.getName().equals(argument.getName())) {
                    actualArguments.add(body);
                } else {
                    actualArguments.add(argument);
                }
            }
        }

        return actualArguments;
    }

    /**
     * @return The matched HTTP method
     */
    HttpMethod getHttpMethod();

    @Override
    UriRouteMatch<R> fulfill(Map<String, Object> argumentValues);

    @Override
    UriRouteMatch<R> decorate(Function<RouteMatch<R>, R> executor);
}
