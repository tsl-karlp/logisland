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
package com.hurence.logisland.service.hbase;


import com.hurence.logisland.annotation.documentation.CapabilityDescription;
import com.hurence.logisland.annotation.documentation.Tags;
import com.hurence.logisland.component.PropertyDescriptor;
import com.hurence.logisland.controller.ControllerService;
import com.hurence.logisland.service.hbase.put.PutColumn;
import com.hurence.logisland.service.hbase.put.PutRecord;
import com.hurence.logisland.service.hbase.scan.Column;
import com.hurence.logisland.service.hbase.scan.ResultHandler;
import com.hurence.logisland.service.hbase.validate.ConfigFilesValidator;
import com.hurence.logisland.validator.StandardValidators;

import java.io.IOException;
import java.util.Collection;

@Tags({"hbase", "client"})
@CapabilityDescription("A controller service for accessing an HBase client.")
public interface HBaseClientService extends ControllerService {

    PropertyDescriptor HADOOP_CONF_FILES = new PropertyDescriptor.Builder()
            .name("hadoop.configuration.files")
            .description("Comma-separated list of Hadoop Configuration files," +
              " such as hbase-site.xml and core-site.xml for kerberos, " +
              "including full paths to the files.")
            .addValidator(new ConfigFilesValidator())
            .build();

    PropertyDescriptor ZOOKEEPER_QUORUM = new PropertyDescriptor.Builder()
            .name("zookeeper.quorum")
            .description("Comma-separated list of ZooKeeper hosts for HBase. Required if Hadoop Configuration Files are not provided.")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    PropertyDescriptor ZOOKEEPER_CLIENT_PORT = new PropertyDescriptor.Builder()
            .name("zookeeper.client.port")
            .description("The port on which ZooKeeper is accepting client connections. Required if Hadoop Configuration Files are not provided.")
            .addValidator(StandardValidators.PORT_VALIDATOR)
            .build();

    PropertyDescriptor ZOOKEEPER_ZNODE_PARENT = new PropertyDescriptor.Builder()
            .name("zookeeper.znode.parent")
            .description("The ZooKeeper ZNode Parent value for HBase (example: /hbase). Required if Hadoop Configuration Files are not provided.")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    PropertyDescriptor HBASE_CLIENT_RETRIES = new PropertyDescriptor.Builder()
            .name("hbase.client.retries")
            .description("The number of times the HBase client will retry connecting. Required if Hadoop Configuration Files are not provided.")
            .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
            .defaultValue("3")
            .build();

    PropertyDescriptor PHOENIX_CLIENT_JAR_LOCATION = new PropertyDescriptor.Builder()
            .name("phoenix.client.jar.location")
            .description("The full path to the Phoenix client JAR. Required if Phoenix is installed on top of HBase.")
            .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
            .expressionLanguageSupported(true)
           // .dynamicallyModifiesClasspath(true)
            .build();

    /**
     * Puts a batch of mutations to the given table.
     *
     * @param tableName the name of an HBase table
     * @param puts a list of put mutations for the given table
     * @throws IOException thrown when there are communication errors with HBase
     */
    void put(String tableName, Collection<PutRecord> puts) throws IOException;

    /**
     * Puts the given row to HBase with the provided columns.
     *
     * @param tableName the name of an HBase table
     * @param rowId the id of the row to put
     * @param columns the columns of the row to put
     * @throws IOException thrown when there are communication errors with HBase
     */
    void put(String tableName, byte[] rowId, Collection<PutColumn> columns) throws IOException;

    /**
     * Scans the given table using the optional filter criteria and passing each result to the provided handler.
     *
     * @param tableName the name of an HBase table to scan
     * @param columns optional columns to return, if not specified all columns are returned
     * @param filterExpression optional filter expression, if not specified no filtering is performed
     * @param minTime the minimum timestamp of cells to return, passed to the HBase scanner timeRange
     * @param handler a handler to process rows of the result set
     * @throws IOException thrown when there are communication errors with HBase
     */
    void scan(String tableName, Collection<Column> columns, String filterExpression, long minTime, ResultHandler handler) throws IOException;

    /**
     * Scans the given table for the given rowId and passes the result to the handler.
     *
     * @param tableName the name of an HBase table to scan
     * @param startRow the row identifier to start scanning at
     * @param endRow the row identifier to end scanning at
     * @param columns optional columns to return, if not specified all columns are returned
     * @param handler a handler to process rows of the result
     * @throws IOException thrown when there are communication errors with HBase
     */
    void scan(String tableName, byte[] startRow, byte[] endRow, Collection<Column> columns, ResultHandler handler) throws IOException;

    /**
     * Converts the given boolean to it's byte representation.
     *
     * @param b a boolean
     * @return the boolean represented as bytes
     */
    byte[] toBytes(boolean b);

    /**
     * Converts the given long to it's byte representation.
     *
     * @param l a long
     * @return the long represented as bytes
     */
    byte[] toBytes(long l);

    /**
     * Converts the given double to it's byte representation.
     *
     * @param d a double
     * @return the double represented as bytes
     */
    byte[] toBytes(double d);

    /**
     * Converts the given string to it's byte representation.
     *
     * @param s a string
     * @return the string represented as bytes
     */
    byte[] toBytes(String s);

    /**
     * Converts the given binary formatted string to a byte representation
     * @param s a binary encoded string
     * @return the string represented as bytes
     */
    byte[] toBytesBinary(String s);

}
