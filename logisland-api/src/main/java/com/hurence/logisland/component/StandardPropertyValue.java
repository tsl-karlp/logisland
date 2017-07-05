/**
 * Copyright (C) 2016 Hurence (support@hurence.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hurence.logisland.component;

import com.hurence.logisland.controller.ControllerServiceLookup;
import com.hurence.logisland.registry.VariableRegistry;

public class StandardPropertyValue extends AbstractPropertyValue {

    public StandardPropertyValue(final String rawValue, final ControllerServiceLookup serviceLookup) {
        this(rawValue, serviceLookup, VariableRegistry.EMPTY_REGISTRY);
    }

    /**
     * Constructs a new StandardPropertyValue with the given value & service
     * lookup and indicates whether or not the rawValue contains any ExpressionLanguage
     * Expressions. If it is unknown whether or not the value contains any ExpressionLanguage
     * Expressions, the
     * {@link #StandardPropertyValue(String, ControllerServiceLookup, VariableRegistry)}
     * constructor should be used or <code>true</code> should be passed.
     *
     * @param rawValue value
     * @param serviceLookup lookup
     * @param variableRegistry variableRegistry
     */
    public StandardPropertyValue(final String rawValue, final ControllerServiceLookup serviceLookup,
                                 final VariableRegistry variableRegistry) {
        this.rawValue = rawValue;
        this.serviceLookup = serviceLookup;
        this.variableRegistry = variableRegistry;
    }

    /**
     * Constructs a new StandardPropertyValue with the given value.
     *
     * @param rawValue value
     */
    public StandardPropertyValue(final String rawValue) {
        this(rawValue, null, VariableRegistry.EMPTY_REGISTRY);
    }

}
