package com.yata.core;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.local.LocalOozie;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hpatel on 8/31/2015.
 */
public class OozieServiceManager {

    private static OozieProperties oozieProperties;

    private final Properties conf;

    private static final String className = OozieServiceManager.class.getSimpleName();

    public OozieServiceManager(String ooziePropertiesFile, String jobPropertiesFile) throws OozieClientException {

        this.oozieProperties = OozieProperties.getInstance(ooziePropertiesFile);
        this.conf = this.loadWorkflowProperties(jobPropertiesFile);
    }

    public void submitOozieJob() throws OozieClientException {

        String OOZIE_URL = "http://" + this.oozieProperties.getProperty("OOZIE_HOST") + ":" + this.oozieProperties.getProperty("OOZIE_PORT") + "/oozie/";
        System.out.println("submitOozieJob@" + className + " : OOZIE_URL :-> " + OOZIE_URL);

        // get a OozieClient for local Oozie
        //http://host:port/oozie/;
        //OozieClient wc = new OozieClient(OOZIE_URL);
        OozieClient wc = LocalOozie.getClient();

        this.conf.setProperty(OozieClient.USER_NAME, this.oozieProperties.getProperty("OOZIE_USER"));
        System.out.println("submitOozieJob@" + className + " : Workflow Properties :-> " + this.conf);

        // submit and start the workflow job
        String jobId = null;

        // wait until the workflow job finishes printing the status every 10 seconds
        try {

            jobId = wc.run(this.conf);
            System.out.println("submitOozieJob@" + className + " : Oozie Workflow JOB Submitted : JobID :-> " + jobId);

            while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {

                System.out.println("submitOozieJob@" + className + " : Oozie Workflow JOB[ " + jobId + " ] Still Running...");

                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    // No need to handles, continue loop for sleep
                }
            }

        } catch (OozieClientException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_0212", "submitOozieJob@" + className + " : Oozie Job submission Failed - EXITING...");
        }
        if(wc.getJobInfo(jobId).getStatus() != WorkflowJob.Status.SUCCEEDED) {

            throw new OozieClientException("ERR_CODE_0212", "submitOozieJob@" + className + " : Oozie Job Failed or Suspended or Killed - EXITING...");
        }

        // print the final status o the workflow job
        System.out.println("submitOozieJob@" + className + " : Oozie Workflow JOB Completed...");
        System.out.println(wc.getJobInfo(jobId));
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
