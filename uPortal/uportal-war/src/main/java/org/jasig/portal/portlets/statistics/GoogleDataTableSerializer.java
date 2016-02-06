/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.portlets.statistics;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.visualization.datasource.render.JsonRenderer;

/**
 * GoogleDataTableSerializer configures a Google DataTable to be serialized 
 * using Google's Json serialization code. 
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class GoogleDataTableSerializer extends JsonSerializer<JsonDataTable> {

    @Override
    public void serialize(JsonDataTable dataTable, JsonGenerator gen,
            SerializerProvider provider) throws IOException,
            JsonProcessingException {
        final CharSequence formatted = JsonRenderer.renderDataTable(dataTable, true, true, false);
        gen.writeRawValue(formatted.toString());
    }

}
