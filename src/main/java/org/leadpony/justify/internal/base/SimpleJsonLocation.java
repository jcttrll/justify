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

package org.leadpony.justify.internal.base;

import javax.json.stream.JsonLocation;

/**
 * Simple implementation of {@link JsonLocation}. 
 * 
 * @author leadpony
 */
public class SimpleJsonLocation implements JsonLocation {
    
    private final long lineNumber;
    private final long columnNumber;
    private final long streamOffset;
    
    public SimpleJsonLocation(long lineNumber, long columnNumber, long streamOffset) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.streamOffset = streamOffset;
    }
    
    public static JsonLocation before(JsonLocation other) {
        long lineNumber = other.getLineNumber();
        long columnNumber = other.getColumnNumber() - 1;
        long streamOffset = other.getStreamOffset() - 1;
        return new SimpleJsonLocation(lineNumber, columnNumber, streamOffset);
    }

    @Override
    public long getLineNumber() {
        return lineNumber;
    }

    @Override
    public long getColumnNumber() {
        return columnNumber;
    }

    @Override
    public long getStreamOffset() {
        return streamOffset;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(line no=").append(lineNumber)
               .append(", column no=").append(columnNumber)
               .append(", offset=").append(streamOffset)
               .append(")");
        return builder.toString();
    }
}