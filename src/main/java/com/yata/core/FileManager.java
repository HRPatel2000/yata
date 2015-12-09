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

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    private static final String className = FileManager.class.getSimpleName();

    public static boolean contentEquals(String actualDataFileName, String expectedDataFileName, String encoding) throws IOException{

        File actualDataFile = new File(actualDataFileName);
        File expectedDataFile = new File(expectedDataFileName);

        if (!actualDataFile.exists()) {

            System.out.println("contentEquals@" + className + " : ActualDataFile :-> " + actualDataFileName + " : doesn't Exist...");
            throw new IOException("contentEquals@" + className + " : ActualDataFile :-> " + actualDataFileName + " : doesn't Exist...");

        } else if (!expectedDataFile.exists()) {

            System.out.println("contentEquals@" + className + " : ExpectedDataFile :-> " + expectedDataFileName + " : doesn't Exist...");
            throw new IOException("contentEquals@" + className + " : ExpectedDataFile :-> " + expectedDataFileName + " : doesn't Exist...");
        }

        boolean contentMatched;
        try {
            contentMatched = FileUtils.contentEqualsIgnoreEOL(actualDataFile, expectedDataFile, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return contentMatched;
    }

    public static String getFileContent(String fileName, String encoding) throws IOException {

        File dataFile = new File(fileName);

        if (!dataFile.exists()) {

            System.out.println("contentEquals@" + className + " : File :-> " + fileName + " : doesn't Exist...");
            throw new IOException("contentEquals@" + className + " : File :-> " + fileName + " : doesn't Exist...");
        }

        String fileContentString;
        try {
            fileContentString = FileUtils.readFileToString(dataFile, encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return fileContentString;
    }


    public static BufferedWriter getFileWriter(String fileName) throws IOException {

        System.out.println("getFileWriter@" + className + " : File to write in :-> " + fileName);

        File file = new File(fileName);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return bufferedWriter;
    }

    public static void write(BufferedWriter bufferedWriter, boolean newline, String dataline) throws IOException {

        bufferedWriter.write(dataline);

        if(newline) {

            bufferedWriter.newLine();
        }
    }

    public static void close(BufferedWriter bufferedWriter) throws IOException {

        bufferedWriter.close();
    }


    public static void deleteDirectoryContent(String filePath) {

        System.out.println("deleteFile@" + className + " : File to Delete :-> " + filePath);

        File file = new File(filePath);

        for(File childFile : file.listFiles()) {

            FileUtils.deleteQuietly(childFile);
        }
    }
}

