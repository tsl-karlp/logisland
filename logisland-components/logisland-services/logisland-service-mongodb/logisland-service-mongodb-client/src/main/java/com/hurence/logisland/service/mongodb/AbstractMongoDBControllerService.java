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
package com.hurence.logisland.service.mongodb;

import com.hurence.logisland.annotation.lifecycle.OnStopped;
import com.hurence.logisland.component.PropertyDescriptor;
import com.hurence.logisland.controller.AbstractControllerService;
import com.hurence.logisland.controller.ConfigurationContext;
import com.hurence.logisland.controller.ControllerServiceInitializationContext;
import com.hurence.logisland.record.Record;
import com.hurence.logisland.validator.StandardValidators;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMongoDBControllerService extends AbstractControllerService {

    static final String WRITE_CONCERN_ACKNOWLEDGED = "ACKNOWLEDGED";
    static final String WRITE_CONCERN_UNACKNOWLEDGED = "UNACKNOWLEDGED";
    static final String WRITE_CONCERN_FSYNCED = "FSYNCED";
    static final String WRITE_CONCERN_JOURNALED = "JOURNALED";
    static final String WRITE_CONCERN_REPLICA_ACKNOWLEDGED = "REPLICA_ACKNOWLEDGED";
    static final String WRITE_CONCERN_MAJORITY = "MAJORITY";

    protected static final PropertyDescriptor URI = new PropertyDescriptor.Builder()
            .name("mongo.uri")
            .displayName("Mongo URI")
            .description("MongoURI, typically of the form: mongodb://host1[:port1][,host2[:port2],...]")
            .required(true)
            .expressionLanguageSupported(true)
            .addValidator(Validation.DOCUMENT_VALIDATOR)
            .build();

    protected static final PropertyDescriptor DATABASE_NAME = new PropertyDescriptor.Builder()
            .name("mongo.db.name")
            .displayName("Mongo Database Name")
            .description("The name of the database to use")
            .required(true)
            .expressionLanguageSupported(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    protected static final PropertyDescriptor COLLECTION_NAME = new PropertyDescriptor.Builder()
            .name("mongo.collection.name")
            .displayName("Mongo Collection Name")
            .description("The name of the collection to use")
            .required(true)
            .expressionLanguageSupported(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    /*
    public static final PropertyDescriptor SSL_CONTEXT_SERVICE = new PropertyDescriptor.Builder()
            .name("ssl-context-service")
            .displayName("SSL Context Service")
            .description("The SSL Context Service used to provide client certificate information for TLS/SSL "
                    + "connections.")
            .required(false)
            .identifiesControllerService(SSLContextService.class)
            .build();
    public static final PropertyDescriptor CLIENT_AUTH = new PropertyDescriptor.Builder()
            .name("ssl-client-auth")
            .displayName("Client Auth")
            .description("Client authentication policy when connecting to secure (TLS/SSL) cluster. "
                    + "Possible values are REQUIRED, WANT, NONE. This property is only used when an SSL Context "
                    + "has been defined and enabled.")
            .required(false)
            .allowableValues(SSLContextService.ClientAuth.values())
            .defaultValue("REQUIRED")
            .build();
*/
    public static final PropertyDescriptor WRITE_CONCERN = new PropertyDescriptor.Builder()
            .name("mongo.write.concern")
            .displayName("Write Concern")
            .description("The write concern to use")
            .required(true)
            .allowableValues(WRITE_CONCERN_ACKNOWLEDGED, WRITE_CONCERN_UNACKNOWLEDGED, WRITE_CONCERN_FSYNCED, WRITE_CONCERN_JOURNALED,
                    WRITE_CONCERN_REPLICA_ACKNOWLEDGED, WRITE_CONCERN_MAJORITY)
            .defaultValue(WRITE_CONCERN_ACKNOWLEDGED)
            .build();

    static List<PropertyDescriptor> descriptors = new ArrayList<>();

    static {
        descriptors.add(URI);
        descriptors.add(DATABASE_NAME);
        descriptors.add(COLLECTION_NAME);
       /* descriptors.add(SSL_CONTEXT_SERVICE);
        descriptors.add(CLIENT_AUTH);*/
    }

    protected MongoClient mongoClient;

    protected final void createClient(ControllerServiceInitializationContext context) throws IOException {
        if (mongoClient != null) {
            closeClient();
        }

        getLogger().info("Creating MongoClient");

        // Set up the client for secure (SSL/TLS communications) if configured to do so
        /*final SSLContextService sslService = context.getProperty(SSL_CONTEXT_SERVICE).asControllerService(SSLContextService.class);
        final String rawClientAuth = context.getProperty(CLIENT_AUTH).getValue();*/
        final SSLContext sslContext = null;

       /* if (sslService != null) {
            final SSLContextService.ClientAuth clientAuth;
            if (StringUtils.isBlank(rawClientAuth)) {
                clientAuth = SSLContextService.ClientAuth.REQUIRED;
            } else {
                try {
                    clientAuth = SSLContextService.ClientAuth.valueOf(rawClientAuth);
                } catch (final IllegalArgumentException iae) {
                    throw new ProviderCreationException(String.format("Unrecognized client auth '%s'. Possible values are [%s]",
                            rawClientAuth, StringUtils.join(SslContextFactory.ClientAuth.values(), ", ")));
                }
            }
            sslContext = sslService.createSSLContext(clientAuth);
        } else {
            sslContext = null;
        }*/

        try {
            if (sslContext == null) {
                mongoClient = new MongoClient(new MongoClientURI(getURI(context)));
            } else {
                mongoClient = new MongoClient(new MongoClientURI(getURI(context), getClientOptions(sslContext)));
            }
        } catch (Exception e) {
            getLogger().error("Failed to schedule {} due to {}", new Object[]{this.getClass().getName(), e}, e);
            throw e;
        }
    }

    protected Builder getClientOptions(final SSLContext sslContext) {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.sslEnabled(true);
        builder.socketFactory(sslContext.getSocketFactory());
        return builder;
    }

    @OnStopped
    public final void closeClient() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    protected MongoDatabase getDatabase(final ConfigurationContext context) {
        return getDatabase(context, null);
    }

    protected MongoDatabase getDatabase(final ConfigurationContext context, final Record record) {
        final String databaseName = context.getPropertyValue(DATABASE_NAME).evaluate(record).asString();
        return mongoClient.getDatabase(databaseName);
    }

    protected MongoCollection<Document> getCollection(final ConfigurationContext context) {
        return getCollection(context, null);
    }

    protected MongoCollection<Document> getCollection(final ConfigurationContext context, final Record record) {
        final String collectionName = context.getPropertyValue(COLLECTION_NAME).evaluate(record).asString();
        return getDatabase(context, record).getCollection(collectionName);
    }

    protected String getURI(final ControllerServiceInitializationContext context) {
        return context.getPropertyValue(URI).evaluate(null).asString();
    }

    protected WriteConcern getWriteConcern(final ConfigurationContext context) {
        final String writeConcernProperty = context.getPropertyValue(WRITE_CONCERN).asString();
        WriteConcern writeConcern = null;
        switch (writeConcernProperty) {
            case WRITE_CONCERN_ACKNOWLEDGED:
                writeConcern = WriteConcern.ACKNOWLEDGED;
                break;
            case WRITE_CONCERN_UNACKNOWLEDGED:
                writeConcern = WriteConcern.UNACKNOWLEDGED;
                break;
            case WRITE_CONCERN_FSYNCED:
                writeConcern = WriteConcern.FSYNCED;
                break;
            case WRITE_CONCERN_JOURNALED:
                writeConcern = WriteConcern.JOURNALED;
                break;
            case WRITE_CONCERN_REPLICA_ACKNOWLEDGED:
                writeConcern = WriteConcern.REPLICA_ACKNOWLEDGED;
                break;
            case WRITE_CONCERN_MAJORITY:
                writeConcern = WriteConcern.MAJORITY;
                break;
            default:
                writeConcern = WriteConcern.ACKNOWLEDGED;
        }
        return writeConcern;
    }

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }
}
