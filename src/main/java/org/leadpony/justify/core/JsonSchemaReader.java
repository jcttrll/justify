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

package org.leadpony.justify.core;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.json.JsonException;

import org.leadpony.justify.core.spi.JsonValidationServiceProvider;

/**
 * Reads a JSON schema from an input source. 
 * 
 * <p>Each instance of this type is NOT safe for use by multiple concurrent threads.</p>
 * 
 * @author leadpony
 */
public interface JsonSchemaReader extends Closeable {
    
    /**
     * Creates a JSON schema reader from a byte stream. 
     * The character encoding of the stream is determined as described in RFC 7159.
     * 
     * @param in the byte stream from which a JSON schema is to be read.
     * @return newly created instance of JSON schema reader.
     * @throws NullPointerException if the specified {@code in} was {@code null}.
     */
    static JsonSchemaReader from(InputStream in) {
        return JsonValidationServiceProvider.provider().createSchemaReader(in);
    }
    
    /**
     * Creates a JSON schema reader from a byte stream. 
     * The bytes of the stream are decoded to characters using the specified charset.
     * 
     * @param in the byte stream from which a JSON schema is to be read.
     * @param charset the character set.
     * @return newly created instance of JSON schema reader.
     * @throws NullPointerException if the specified {@code in} or {@code charset} was {@code null}.
     */
    static JsonSchemaReader from(InputStream in, Charset charset) {
        return JsonValidationServiceProvider.provider().createSchemaReader(in, charset);
    }

    /**
     * Creates a JSON schema reader from a reader. 
     * 
     * @param reader the reader from which a JSON schema is to be read.
     * @return newly created instance of JSON schema reader.
     * @throws NullPointerException if the specified {@code reader} was {@code null}.
     */
    static JsonSchemaReader from(Reader reader) {
        return JsonValidationServiceProvider.provider().createSchemaReader(reader);
    }

    /**
     * Reads a JSON schema reader from a byte stream. 
     * The character encoding of the stream is determined as described in RFC 7159.
     * 
     * @param in the byte stream from which a JSON schema is to be read.
     * @return the JSON schema.
     * @throws NullPointerException if the specified {@code in} was {@code null}.
     * @throws JsonException if an I/O error occurs while reading.
     * @throws JsonValidatingException if the reader found problems during validation of the schema.
     */
    static JsonSchema readFrom(InputStream in) {
        try (JsonSchemaReader schemaReader = JsonSchemaReader.from(in)) {
            return schemaReader.read();
        }
    }

    /**
     * Reads a JSON schema reader from a byte stream. 
     * The bytes of the stream are decoded to characters using the specified charset.
     * 
     * @param in the byte stream from which a JSON schema is to be read.
     * @param charset the character set.
     * @return the JSON schema.
     * @throws NullPointerException if the specified {@code in} or {@code charset} was {@code null}.
     * @throws JsonException if an I/O error occurs while reading.
     * @throws JsonValidatingException if the reader found problems during validation of the schema.
     */
    static JsonSchema readFrom(InputStream in, Charset charset) {
        try (JsonSchemaReader schemaReader = JsonSchemaReader.from(in, charset)) {
            return schemaReader.read();
        }
    }

    /**
     * Reads a JSON schema reader from a reader. 
     * 
     * @param reader the reader from which a JSON schema is to be read.
     * @return the JSON schema.
     * @throws NullPointerException if the specified {@code reader} was {@code null}.
     * @throws JsonException if an I/O error occurs while reading.
     * @throws JsonValidatingException if the reader found problems during validation of the schema.
     */
    static JsonSchema readFrom(Reader reader) {
        try (JsonSchemaReader schemaReader = JsonSchemaReader.from(reader)) {
            return schemaReader.read();
        }
    }

    /**
     * Returns a JSON schema that is represented in the input source. 
     * This method needs to be called only once for a reader instance.
     * 
     * @return the JSON schema.
     * @throws JsonException if an I/O error occurs while reading.
     * @throws JsonValidatingException if the reader found problems during validation of the schema.
     * @throws IllegalStateException if read or close method is already called.
     */
    JsonSchema read();
    
    /**
     * Closes this reader and frees any resources associated with the reader. 
     * This method closes the underlying input source.
     * 
     * @throws JsonException if an I/O error occurs while closing this reader.
     */
    @Override
    void close();
    
    /**
     * Assigns a resolver of external JSON schemas to this reader.
     * 
     * @param resolver the resolver of external JSON schemas.
     * @return this reader.
     * @throws NullPointerException if the specified {@code resolver} was {@code null}.
     */
    JsonSchemaReader withSchemaResolver(JsonSchemaResolver resolver);
}
