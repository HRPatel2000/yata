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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.oozie.client.OozieClientException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFSManager {

    private static final String className = HDFSManager.class.getSimpleName();

    private final OozieProperties oozieProperties;

    public HDFSManager(String ooziePropertiesFile) throws OozieClientException {

        this.oozieProperties = OozieProperties.getInstance(ooziePropertiesFile);
    }

    private FileSystem getHdfsFileSytem() throws IOException {


        URI hdfsURI = null; //hdfs://hdtmaster1.lrd.cat.com:8020

        try {

            hdfsURI = new URI("hdfs://" + oozieProperties.getProperty("HDFS_HOST") + ":" + this.oozieProperties.getProperty("HDFS_PORT"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("getHdfsFileSytem@" + className + " : URI Syntax Invalid...");
        }

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsURI.getPath());
        conf.set("user.name", this.oozieProperties.getProperty("OOZIE_USER"));
        conf.set("basedir", "/user/" + this.oozieProperties.getProperty("OOZIE_USER"));
        conf.set("hadoop.job.ugi", this.oozieProperties.getProperty("OOZIE_USER"));

        FileSystem hdfs = null;

        for(int i = 0; i < 3; i++) {

            try {

                Thread.sleep(300);
                hdfs = FileSystem.get(hdfsURI, conf, this.oozieProperties.getProperty("OOZIE_USER"));
                break;
            } catch (InterruptedException e) {
                System.out.println("getHdfsFileSytem@" + className + " : InterruptedException while getting HDFS...");
                e.printStackTrace();
            } catch (ConnectException e) {
                System.out.println("getHdfsFileSytem@" + className + " : ConnectException while getting HDFS...");
                e.printStackTrace();
            }
            System.out.println("getHdfsFileSytem@" + className + " : RETRY");
        }

        return hdfs;
    }

    /**
     *
     * @param hdfsTestDataSourceFile
     * @param hdfsTestDataTargetFile
     * @throws IOException
     *
     * hadoop fs -cp /projects/ddsw/dev/data/backup/dealer_hierarchy/<<DOMAIN_NAME>>/<<FILE_NAME>> /projects/ddsw/dev/data/raw/nas/<<DOMAIN_NAME>>
     */
    public void copyHDFSData(String hdfsTestDataSourceFile, String hdfsTestDataTargetFile) throws OozieClientException {

        System.out.println("copyHDFSData@" + className + " : Loading Test Data From :-> " + hdfsTestDataSourceFile + " : Into :-> " + hdfsTestDataTargetFile);

        FileSystem hdfs = null;
        Path hdfsTestDataSource = null;
        Path hdfsTestDataTarget = null;

        try {

            hdfs = getHdfsFileSytem();

            System.out.println("copyHDFSData@" + className + " : HDFS :-> " + hdfs);

            System.out.println("copyHDFSData@" + className + " : HDFSHomeDirectory :-> " + hdfs.getHomeDirectory());
            System.out.println("copyHDFSData@" + className + " : HDFS-URI :-> " + hdfs.getUri());
            System.out.println("copyHDFSData@" + className + " : HDFSWorkingDirectory :-> " + hdfs.getWorkingDirectory());
            System.out.println("copyHDFSData@" + className + " : HDFS : " + hdfs + " : Exists :-> " + hdfs.exists(hdfs.getHomeDirectory()));

            hdfsTestDataSource = new Path(hdfs.getUri().getPath() + hdfsTestDataSourceFile);
            hdfsTestDataTarget = new Path(hdfs.getUri().getPath() + hdfsTestDataTargetFile);

            System.out.println("copyHDFSData@" + className + " : HDFS TEST DATA : " + hdfsTestDataSource + " : Exists :-> " + hdfs.exists(hdfsTestDataSource));
            System.out.println("copyHDFSData@" + className + " : HDFS DOMAIN DATA : " + hdfsTestDataTarget + " : Exists :-> " + hdfs.exists(hdfsTestDataTarget));

        } catch (IOException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_1218", "copyHDFSData@" + className + " : IOException while getting HDFS FileSystem - EXITING...");
        }

        FileUtil hdfsUtil = new FileUtil();

        try {

            hdfsUtil.copy(
                    hdfs,
                    hdfsTestDataSource,
                    hdfs,
                    hdfsTestDataTarget,
                    false,
                    true,
                    hdfs.getConf()
            );

            System.out.println("copyHDFSData@" + className + " : NOW : HDFS TEST DATA : " + hdfsTestDataSource + " : Exists :-> " + hdfs.exists(hdfsTestDataSource));
            System.out.println("copyHDFSData@" + className + " : HDFS DOMAIN DATA : " + hdfsTestDataTarget + " : Exists :-> " + hdfs.exists(hdfsTestDataTarget));

        } catch (IOException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_1218", "copyHDFSData@" + className + " : IOException while Copying HDFS Data - EXITING...");
        }

        /**
         * IMPORTANT
         * If the Source Data file on HDFS is not owned by the Hive/Hadoop User, then use the command below to
         * change the permission for Hive/Hadoop User to move/delete the file once processed...
         */
        try {

            hdfs.setPermission(hdfsTestDataTarget, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.READ_EXECUTE));
        } catch (IOException e) {

            e.printStackTrace();
            throw new OozieClientException("ERR_CODE_1218", "copyHDFSData@" + className + " : IOException while Changing HDFS File Permissions - EXITING...");
        }

    }
}

