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

package org.leadpony.justify.internal.schema;

import static org.leadpony.justify.internal.base.Arguments.requireNonNull;

import java.net.URI;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.json.JsonValue;
import org.leadpony.justify.api.ObjectJsonSchema;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.Keyword;
import org.leadpony.justify.internal.base.json.JsonPointerTokenizer;
import org.leadpony.justify.internal.keyword.SchemaKeyword;
import org.leadpony.justify.internal.keyword.annotation.Default;
import org.leadpony.justify.internal.keyword.core.Comment;
import org.leadpony.justify.internal.keyword.core.Schema;

/**
 * Skeletal implementation of {@link JsonSchema}.
 *
 * @author leadpony
 */
abstract class AbstractJsonSchema extends AbstractMap<String, Keyword> implements ObjectJsonSchema, Resolvable {

    private URI id;
    private final JsonValue json;

    private final Map<String, SchemaKeyword> keywordMap;

    protected AbstractJsonSchema(URI id, JsonValue json, Map<String, SchemaKeyword> keywords) {
        this.id = id;
        this.json = json;
        this.keywordMap = Collections.unmodifiableMap(keywords);
        this.keywordMap.forEach((k, v) -> v.setEnclosingSchema(this));
        if (hasAbsoluteId()) {
            resolveSubschemas(id());
        }
    }

    /* As a JsonSchema */

    @Override
    public boolean hasId() {
        return id != null;
    }

    @Override
    public URI id() {
        return id;
    }

    @Override
    public URI schema() {
        if (containsKeyword("$schema")) {
            Schema keyword = getKeyword("$schema");
            return keyword.value();
        } else {
            return null;
        }
    }

    @Override
    public String comment() {
        if (containsKeyword("$comment")) {
            Comment keyword = getKeyword("$comment");
            return keyword.value();
        } else {
            return null;
        }
    }

    @Override
    public JsonValue defaultValue() {
        if (containsKeyword("default")) {
            Default keyword = getKeyword("default");
            return keyword.value();
        } else {
            return null;
        }
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean containsKeyword(String keyword) {
        requireNonNull(keyword, "keyword");
        return keywordMap.containsKey(keyword);
    }

    @Override
    public JsonValue getKeywordValue(String keyword) {
        return getKeywordValue(keyword, null);
    }

    @Override
    public JsonValue getKeywordValue(String keyword, JsonValue defaultValue) {
        requireNonNull(keyword, "keyword");
        Keyword found = keywordMap.get(keyword);
        if (found == null) {
            return defaultValue;
        }
        return found.getValueAsJson();
    }

    @Override
    public Stream<JsonSchema> getSubschemas() {
        return keywordMap.values().stream()
                .filter(SchemaKeyword::hasSubschemas)
                .flatMap(SchemaKeyword::getSubschemas);
    }

    @Override
    public Stream<JsonSchema> getInPlaceSubschemas() {
        return keywordMap.values().stream()
                .filter(SchemaKeyword::hasSubschemas)
                .filter(SchemaKeyword::isInPlace)
                .flatMap(SchemaKeyword::getSubschemas);
    }

    @Override
    public JsonSchema getSubschemaAt(String jsonPointer) {
        requireNonNull(jsonPointer, "jsonPointer");
        if (jsonPointer.isEmpty()) {
            return this;
        }
        return searchKeywordsForSubschema(jsonPointer);
    }

    @Override
    public final JsonValue toJson() {
        return json;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    /* Resolvable interface */

    @Override
    public void resolve(URI baseUri) {
        if (hasAbsoluteId()) {
            return;
        } else if (hasId()) {
            this.id = baseUri.resolve(this.id);
            baseUri = this.id;
        }
        resolveSubschemas(baseUri);
    }

    public boolean hasAbsoluteId() {
        return hasId() && id().isAbsolute();
    }

    /* As a Map */

    @Override
    public int size() {
        return keywordMap.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return keywordMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return keywordMap.containsValue(value);
    }

    @Override
    public Keyword get(Object key) {
        return keywordMap.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<String, Keyword>> entrySet() {
        Set<?> entrySet = this.keywordMap.entrySet();
        return (Set<Entry<String, Keyword>>) entrySet;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Keyword> T getKeyword(String name) {
        return (T) keywordMap.get(name);
    }

    private JsonSchema searchKeywordsForSubschema(String jsonPointer) {
        JsonPointerTokenizer tokenizer = new JsonPointerTokenizer(jsonPointer);
        SchemaKeyword keyword = keywordMap.get(tokenizer.next());
        if (keyword != null) {
            JsonSchema candidate = keyword.getSubschema(tokenizer);
            if (candidate != null) {
                if (tokenizer.hasNext()) {
                    return candidate.getSubschemaAt(tokenizer.remaining());
                } else {
                    return candidate;
                }
            }
        }
        return null;
    }

    private void resolveSubschemas(URI baseUri) {
        getSubschemas()
                .filter(s -> !s.hasAbsoluteId())
                .filter(s -> s instanceof Resolvable)
                .forEach(s -> ((Resolvable) s).resolve(baseUri));
    }
}
