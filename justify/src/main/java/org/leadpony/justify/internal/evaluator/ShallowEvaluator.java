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

import jakarta.json.stream.JsonParser.Event;

import org.leadpony.justify.api.EvaluatorContext;
import org.leadpony.justify.api.ProblemDispatcher;

/**
 * An evaluator type which will observes only shallow events.
 *
 * @author leadpony
 */
public abstract class ShallowEvaluator extends AbstractEvaluator {

    protected ShallowEvaluator(EvaluatorContext context) {
        super(context);
    }

    @Override
    public final Result evaluate(Event event, int depth, ProblemDispatcher dispatcher) {
        if (depth > 1) {
            return Result.PENDING;
        }
        return evaluateShallow(event, depth, dispatcher);
    }

    public abstract Result evaluateShallow(Event event, int depth, ProblemDispatcher dispatcher);
}
