package com.yata.core;

import org.apache.oozie.client.OozieClientException;

import java.io.IOException;

/**
 * Created by hpatel on 8/31/2015.
 *
 * IMPORTANT : Each Oozie Functional Test should extend OozieRunner
 */
public class OozieRunner {

    private static final String ooziePropertiesFile = System.getProperty("oozie.properties");
    private final String jobPropertiesFile;

    private static OozieServiceManager oozieServiceManager = null;

    private static HiveServiceManager hiveServiceManager = null;

    public OozieRunner(String jobPropertiesFile) throws OozieClientException {

        this.jobPropertiesFile = jobPropertiesFile;
        this.oozieServiceManager = new OozieServiceManager(ooziePropertiesFile, jobPropertiesFile);
        this.hiveServiceManager = new HiveServiceManager(ooziePropertiesFile);
    }

    /**
     *
     * @param hdfsTestDataSourceFile
     * @param hdfsTestDataTargetFile
     * @throws IOException
     *
     * Copy Data from HDFS Source Location into HDFS Target Location
     */
    public void copyHDFSData(String hdfsTestDataSourceFile, String hdfsTestDataTargetFile) throws IOException, OozieClientException {

        HDFSManager hdfsManager = new HDFSManager(ooziePropertiesFile);

        hdfsManager.copyHDFSData(hdfsTestDataSourceFile, hdfsTestDataTargetFile);
    }

    /**
     *
     * @throws OozieClientException
     *
     * Submit the Oozie Workflow.
     * Assumption : Required Data to be loaded or processed as part of this workflow is already available in appropriate
     * location as expected by the Workflow
     */
    public void submitOozieJob() throws OozieClientException {

        this.getOozieServiceManager().submitOozieJob();
    }

    public String executeHiveQuery(String query, String fileName, String delimeter, String encoding) throws OozieClientException {

        return this.getHiveServiceManager().executeHiveQuery(query, fileName, delimeter, encoding);
    }

    public boolean loadDataFromHDFSPath(String fileName, String tableName) throws OozieClientException {

        return this.getHiveServiceManager().loadDataFromHDFSPath(fileName, tableName);
    }

    public boolean loadDataFromLocalPath(String fileName, String tableName) throws OozieClientException {

        return this.getHiveServiceManager().loadDataFromLocalPath(fileName, tableName);
    }

    public boolean dropPartition(String tableName, String partitionString) throws OozieClientException {

        return this.getHiveServiceManager().dropPartition(tableName, partitionString);
    }

    public void deleteDirectoryContent(String filePath) {

        FileManager.deleteDirectoryContent(filePath);
    }

    public OozieServiceManager getOozieServiceManager() {

        return this.oozieServiceManager;
    }

    public HiveServiceManager getHiveServiceManager() {

        return this.hiveServiceManager;
    }

}
