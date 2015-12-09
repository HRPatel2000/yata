package com.yata.core;

import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.local.LocalOozie;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by hpatel on 9/20/15.
 */
public class OozieRunnerTest {

    public final static String OOZIE_HOST = "OOZIE_HOST";
    public final static String OOZIE_PORT = "OOZIE_PORT";
    public final static String HDFS_HOST = "HDFS_HOST";
    public final static String HDFS_PORT = "HDFS_PORT";

    public final static String OOZIE_PROPERTIES_FILE = "oozie.properties";

    public static OozieProperties props;

    /**
     *
     * ####IMPORTANT####
     * #Relative Path to the DOMAIN Specific Job Properties is required as below
     */
    private static final String jobPropertiesFile = "yata/job.properties";

    /**
     *
     * ####IMPORTANT####
     * #Relative Path to the Expected and Actual Data Path should be Drequired as below
     *  1. Expected - Expected Test Results in a File must be provided against "Each Query" under the Test Case (One Record Per Line)
     *  2. Actual - OozieRunner Framework will execute the Query under the Test Case and save result data into the file under this path (One Record Per Line)
     *
     * ####IMPORTANT####
     * #New Line Characters under the Expected file will break the Content Comparision.
     * # So make sure the Expected Result files are free of New Line Character at the end of the "Last Record"
     *
     */
    private static final String actualDataPath = "src/test/resources/yata/actual";
    private static final String expectedDataPath = "src/test/resources/yata/expected";

    private static OozieRunner oozieRunner = null;

    /**
     *
     * ####IMPORTANT####
     * #Naming convention must be maintained for DOMAIN Specific Properties below
     * #Provide Test Data Source and Target Files for each Domain with Keys as "<DOMAIN_NAME>.test_data_source_file" AND "<DOMAIN_NAME>.test_data_target_file"
     * #Also, assumption is that the Test Data Target File is monitored by individual Domain Workflows for processing
     *
     */
    private static final String sourceDataFile = "yata/input/yata_data_table_content.csv";
    private static final String targetDataFile = "yata/output/yata_data.csv";

    /**
     * Encoding utf-8 is required for File Content comparison to work in Windows.
     * OozieRunner.executeQuery, FileManager.getFileContent and FileManager.contentEquals methods requires this information.
     */
    String encoding = "utf-8";

    /**
     *
     * ****IMPORTANT****
     * Every Test case that needs to submit Oozie workflow Job needs this @BeforeClass annotation with,
     *  1. Create Instance of OozieRunner with supplying Domain specific Job Properties file
     *  2. Call CopyHDFSData with Source and Target Data Files on HDFS (If Test data is expected in a specific HDFS location prior to running the Workflow Job
     *  3. Submit the actual Workflow Job
     */
//    @BeforeClass
//    public static void initialize() throws Exception {
//
//        System.out.println("Yata@BeforeClass...");
//
////        LocalOozie.start();
//
//        try {
//            oozieRunner = new OozieRunner(jobPropertiesFile);
//
////            oozieRunner.copyHDFSData(sourceDataFile, targetDataFile);
//
//            oozieRunner.submitOozieJob();
//
//        } catch (OozieClientException e) {
//
//            e.printStackTrace();
////        } catch (IOException e) {
////
////            e.printStackTrace();
//        }
//    }

    @BeforeClass
    public static void setUp() throws Exception {

        System.out.println("Yata@BeforeClass...");

        LocalOozie.start();
        System.out.println("Yata@Local Oozie Initiallized...");

        props = OozieProperties.getInstance(System.getProperty(OOZIE_PROPERTIES_FILE));

        try {
            oozieRunner = new OozieRunner(jobPropertiesFile);

            oozieRunner.copyHDFSData(sourceDataFile, targetDataFile);

            oozieRunner.submitOozieJob();

        } catch (OozieClientException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {

        LocalOozie.stop();

        System.out.println("Yata@Local Oozie Teared Down...");
    }

//    @Test
//    public void testSubmitOozieJob() throws Exception {
//
////        // get a OozieClient for local Oozie
//        OozieClient wc = LocalOozie.getClient();
//
//        // create a workflow job configuration and set the workflow application path
//        Properties conf = wc.createConfiguration();
//
//        // setting workflow parameters
//        Path appPath = new Path(System.getProperty(Services.OOZIE_HOME_DIR), "workflow");
//        Path input = new Path(appPath, "input");
//        Path output = new Path(appPath, "output");
//
//        conf.setProperty(OozieClient.APP_PATH, appPath.toString());
//        conf.setProperty("jobTracker", props.getProperty(OOZIE_HOST) + ":" + props.getProperty(OOZIE_PORT));
//        conf.setProperty("nameNode", "hdfs://" + props.getProperty(HDFS_HOST) + ":" + props.getProperty(HDFS_PORT));
//        conf.setProperty("input", input.toString());
//        conf.setProperty("output", output.toString());
//        conf.setProperty("delPath", output.toString());
//        conf.setProperty("subWfApp", appPath.toString() + "/subwf/workflow.xml");
//        //conf.setProperty("user.name", getTestUser());
//
//        // submit and start the workflow job
//        String jobId = wc.run(conf);
//        System.out.println("Workflow job submitted");
//
//        // wait until the workflow job finishes printing the status every 10 seconds
//        while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
//            System.out.println("Workflow job running ...");
//            Thread.sleep(10 * 1000);
//        }
//
//        // print the final status o the workflow job
//        System.out.println("Workflow job completed ...");
//        System.out.println(wc.getJobInfo(jobId));
//    }

    @Test
    public void shouldExecuteOozieWorkflow() {

        System.out.println("Yata@shouldExecuteOozieWorkflow...");

        try {
            oozieRunner = new OozieRunner(jobPropertiesFile);

            oozieRunner.submitOozieJob();

        } catch (OozieClientException e) {

            e.printStackTrace();
        }
    }

    @Test
    public void testCopyHDFSData() throws Exception {

    }

    @Test
    public void testExecuteHiveQuery() throws Exception {

    }

    @Test
    public void testLoadDataFromHDFSPath() throws Exception {

    }

    @Test
    public void testLoadDataFromLocalPath() throws Exception {

    }

    @Test
    public void testDropPartition() throws Exception {

    }

    @Test
    public void testDeleteDirectoryContent() throws Exception {

    }

    @Test
    public void testGetOozieServiceManager() throws Exception {

    }

    @Test
    public void testGetHiveServiceManager() throws Exception {

    }
}