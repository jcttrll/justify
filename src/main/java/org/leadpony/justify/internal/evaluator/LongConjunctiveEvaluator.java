/*
 * Copyright 2018 the Justify authors.
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

import java.util.Iterator;
import java.util.stream.Stream;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.leadpony.justify.core.Evaluator;
import org.leadpony.justify.core.InstanceType;
import org.leadpony.justify.core.JsonSchema;
import org.leadpony.justify.core.ProblemDispatcher;

/**
 * @author leadpony
 */
class LongConjunctiveEvaluator extends ConjunctiveEvaluator {

    private final InstanceMonitor monitor;
    
    LongConjunctiveEvaluator(InstanceType type) {
        this.monitor = InstanceMonitor.of(type);
    }
    
    LongConjunctiveEvaluator(Stream<JsonSchema> children, InstanceType type, boolean affirmative) {
        super(children, type, affirmative);
        this.monitor = InstanceMonitor.of(type);
    }

    @Override
    public Result evaluate(Event event, JsonParser parser, int depth, ProblemDispatcher dispatcher) {
        Iterator<Evaluator> it = children.iterator();
        while (it.hasNext()) {
            Result result = invokeChildEvaluator(it.next(), event, parser, depth, dispatcher);
            if (result != Result.PENDING) {
                if (result == Result.FALSE) {
                    evaluationsAsInvalid++;
                }
                it.remove();
            }
        }
        if (monitor.isCompleted(event, depth)) {
            return (evaluationsAsInvalid == 0) ? Result.TRUE : Result.FALSE;
        }
        return Result.PENDING;
    }

    protected Result invokeChildEvaluator(Evaluator evaluator, Event event, JsonParser parser, int depth,
            ProblemDispatcher dispatcher) {
        return evaluator.evaluate(event, parser, depth, dispatcher);
    }
}
