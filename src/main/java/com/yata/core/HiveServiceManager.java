/*
 * Copyright 2013 Klarna AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yata.core;

import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.OozieClientException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class HiveServiceManager {

    private static OozieProperties oozieProperties;

    private final HiveJdbcManager hiveJdbcManager;

    private static final String className = OozieServiceManager.class.getSimpleName();

    public HiveServiceManager(String ooziePropertiesFile) throws OozieClientException {

        this.oozieProperties = OozieProperties.getInstance(ooziePropertiesFile);
        this.hiveJdbcManager = new HiveJdbcManager(ooziePropertiesFile);
    }

    public String executeHiveQuery(String query, String fileName, String delimeter, String encoding) throws OozieClientException {

        BufferedWriter bufferedWriter = null;
        System.out.println("executeHiveQuery@" + className + " : Query :-> " + query);
        System.out.println("executeHiveQuery@" + className + " : Actual Data FileName :-> " + fileName);

        Connection con = null;
        Statement stmt = null;

        try {

            con = this.hiveJdbcManager.getHiveJdbcConnection();
            stmt = con.createStatement();

            bufferedWriter = FileManager.getFileWriter(fileName);

            ResultSet res = stmt.executeQuery(query);
            ResultSetMetaData resMetadata = res.getMetaData();

            while (res.next()) {

                String resultRow = this.getResultRow(res, resMetadata, delimeter);
                FileManager.write(bufferedWriter, true, resultRow);
                System.out.println("executeHiveQuery@" + className + " : RESULTS :-> " + resultRow);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_0111", "executeHiveQuery@" + className + " : SQLException while executing the Query - EXITING...");
        } catch (IOException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_0111", "executeHiveQuery@" + className + " : IOException while executing the Query - EXITING...");
        } finally {
            try {
                stmt.close();
                con.close();
                FileManager.close(bufferedWriter);
            } catch (IOException e) {

                e.printStackTrace();
                throw new OozieClientException("ERR_CODE_0111", "executeHiveQuery@" + className + " : IOException - Connection couldn't be closed after executing Query - EXITING...");
            } catch (SQLException e) {

                e.printStackTrace();
                throw new OozieClientException("ERR_CODE_0111", "executeHiveQuery@" + className + " : SQLException - Connection couldn't be closed after executing Query - EXITING...");
            }
        }

        return fileName;
    }

    /**
     *
     * @param query
     * @return boolean
     *
     */
    public boolean execute(String query) throws OozieClientException {

        boolean res = false;

        Connection con = null;
        Statement stmt = null;

        try {

            con = this.hiveJdbcManager.getHiveJdbcConnection();
            stmt = con.createStatement();
            System.out.println("executeHiveQuery@" + className + " : QUERY :-> " + query);
            res = stmt.execute(query);

        } catch (SQLException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_0111", "execute@" + className + " : SQLException while executing the Query - EXITING...");
        } finally {
            try {

                stmt.close();
                con.close();
            } catch (SQLException e) {

                e.printStackTrace();
                throw new OozieClientException("ERR_CODE_0111", "execute@" + className + " : Connection couldn't be closed after executing Query - EXITING...");
            }
        }

        return res;
    }

    public boolean loadDataFromHDFSPath(String fileName, String tableName) throws OozieClientException {

        Path hdfsTestDataSource = new Path(fileName);

        String query = "load data inpath '" + hdfsTestDataSource + "' into table " + tableName;

        return this.execute(query);
    }

    public boolean loadDataFromLocalPath(String fileName, String tableName) throws OozieClientException {

        String query = "load data local inpath '" + fileName + "' into table " + tableName;

        return this.execute(query);
    }

    public boolean dropPartition(String tableName, String partitionString) throws OozieClientException {

        if(tableName != null && tableName.contains(".")) {
            throw new IllegalArgumentException("Table name, " + tableName
                    + ", cannot contain a database qualifier for dropping a partition for Hive Versions < 0.14. "
                    + "Please use the convention of obtaining a Jdbc connection with the default database set and use unqualified table names."
                    + "See https://issues.apache.org/jira/browse/HIVE-4064.");
        }

        String query = "alter table " + tableName + " drop if exists partition ( " + partitionString + " )";

        return this.execute(query);
    }

    private String getResultRow(ResultSet res, ResultSetMetaData resMetadata, String delimeter) throws SQLException {

        int columns = resMetadata.getColumnCount();

        StringBuffer result = new StringBuffer();

        for(int i=1; i<=columns; i++) {
            int columnType = resMetadata.getColumnType(i);

            switch(columnType) {

                case Types.VARCHAR:
                    result.append(res.getString(i) + delimeter);
                    break;

                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.BIGINT:
                    result.append(res.getInt(i) + delimeter);
                    break;

                case Types.FLOAT:
                case Types.DECIMAL:
                case Types.DOUBLE:
                    result.append(res.getDouble(i) + delimeter);
                    break;

                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    result.append(res.getDate(i) + delimeter);
                    break;
            }
        }

        String resultString = result.toString();
        return resultString.substring(0, resultString.lastIndexOf(delimeter));
    }

    private Properties loadWorkflowProperties(String jobPropertiesFile) throws OozieClientException {

        Properties props = new Properties();

        InputStream is = ClassLoader.getSystemResourceAsStream(jobPropertiesFile);
        try {
            props.load(is);
        }
        catch (IOException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_1030", "loadWorkflowProperties@" + className + " : Workflow Job Properties couldn't be loaded - EXITING...");
        }

        return props;
    }
}
