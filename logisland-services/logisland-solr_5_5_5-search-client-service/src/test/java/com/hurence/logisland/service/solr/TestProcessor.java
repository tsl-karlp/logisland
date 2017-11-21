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

package com.hurence.logisland.service.elasticsearch;

import com.hurence.logisland.component.PropertyDescriptor;
import com.hurence.logisland.processor.AbstractProcessor;
import com.hurence.logisland.processor.ProcessContext;
import com.hurence.logisland.record.Record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



public class TestProcessor extends AbstractProcessor {

    public static final PropertyDescriptor SOLR_CLIENT_SERVICE = new PropertyDescriptor.Builder()
            .name("solr.search-client.service")
            .description("The instance of the Controller Service to use for accessing Solr.")
            .required(true)
            .identifiesControllerService(ElasticsearchClientService.class)
            .build();


    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        List<PropertyDescriptor> propDescs = new ArrayList<>();
        propDescs.add(SOLR_CLIENT_SERVICE);
        return propDescs;
    }

    @Override
    public Collection<Record> process(ProcessContext context, Collection<Record> records) {
        return Collections.emptyList();
    }


}
