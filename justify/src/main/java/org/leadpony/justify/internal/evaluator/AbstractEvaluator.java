/*
 * Copyright 2018-2019 the Justify authors.
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
package org.leadpony.justify.internal.evaluator;

import jakarta.json.stream.JsonParser;

import org.leadpony.justify.api.Evaluator;
import org.leadpony.justify.api.EvaluatorContext;

/**
 * The base implementation of {@link Evaluator} interfae.
 *
 * @author leadpony
 */
public abstract class AbstractEvaluator implements Evaluator {

    private final EvaluatorContext context;

    protected AbstractEvaluator(EvaluatorContext context) {
        this.context = context;
    }

    public final EvaluatorContext getContext() {
        return context;
    }

    public final JsonParser getParser() {
        return context.getParser();
    }
}
