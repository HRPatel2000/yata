package com.yata.core;

import org.apache.oozie.local.LocalOozie;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Created by hpatel on 9/20/15.
 */
public class OozieSetup {

    @BeforeClass
    public void setUp() throws Exception {

        LocalOozie.start();
    }

    @AfterClass
    public void tearDown() throws Exception {

        LocalOozie.stop();
    }
}