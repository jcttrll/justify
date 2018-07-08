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

import javax.json.stream.JsonParser;

import org.leadpony.justify.core.Evaluator;

/**
 * Logical evaluator for "allOf" boolean logic.
 *  
 * @author leadpony
 */
class ConjunctionEvaluator extends AbstractLogicalEvaluator {
    
    private int falseEvaluations;
    
    public static LogicalEvaluator.Builder builder() {
        return new ConjunctionEvaluator();
    }
    
    @Override
    protected boolean accumulateResult(Evaluator evaluator, Result result) {
        if (result == Result.FALSE) {
            this.falseEvaluations++;
        }
        return true;
    }
    
    @Override
    protected Result conclude(JsonParser parser, Reporter reporter) {
        return (this.falseEvaluations == 0) ? Result.TRUE : Result.FALSE;
    }
}
