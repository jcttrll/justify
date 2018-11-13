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

package org.leadpony.justify.internal.keyword.assertion;

import java.math.BigDecimal;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;

import org.leadpony.justify.core.Problem;
import org.leadpony.justify.core.ProblemDispatcher;

/**
 * Assertion specified with "multipleOf" validation keyword.
 * 
 * @author leadpony
 */
class MultipleOf extends AbstractNumericAssertion {
    
    protected final BigDecimal factor;
    
    MultipleOf(BigDecimal factor) {
        this.factor = factor;
    }

    @Override
    public String name() {
        return "multipleOf";
    }
    
    @Override
    public void addToJson(JsonObjectBuilder builder, JsonBuilderFactory builderFactory) {
        builder.add(name(), factor);
    }
    
    @Override
    protected Result evaluateAgainst(BigDecimal value, JsonParser parser, ProblemDispatcher dispatcher) {
        if (testValue(value)) {
            return Result.TRUE;
        } else {
            Problem p = createProblemBuilder(parser)
                    .withMessage("instance.problem.multipleOf")
                    .withParameter("actual", value)
                    .withParameter("factor", factor)
                    .build();
            dispatcher.dispatchProblem(p);
            return Result.FALSE;
        }
    }

    @Override
    protected Result evaluateNegatedAgainst(BigDecimal value, JsonParser parser, ProblemDispatcher dispatcher) {
        if (testValue(value)) {
            Problem p = createProblemBuilder(parser)
                    .withMessage("instance.problem.not.multipleOf")
                    .withParameter("actual", value)
                    .withParameter("factor", factor)
                    .build();
            dispatcher.dispatchProblem(p);
            return Result.FALSE;
        } else {
            return Result.TRUE;
        }
    }
    
    private boolean testValue(BigDecimal value) {
        BigDecimal remainder = value.remainder(factor);
        return remainder.compareTo(BigDecimal.ZERO) == 0;
    }
}
