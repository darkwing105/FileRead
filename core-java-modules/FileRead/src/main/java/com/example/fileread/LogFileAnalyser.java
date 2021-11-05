package com.example.fileread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;



// Scratchpad
//C:\Users\deonb\Downloads\ProdLogs\ZW-PROD-LOGS-poe-dc02-20211028
//
/*public class UnzipFile {
    public static void main(String[] args) throws IOException {
        String fileZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File("src/main/resources/unzipTest");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            // ...
        }
        zis.closeEntry();
        zis.close();
    }*/



@SpringBootApplication
public class LogFileAnalyser {

    public static void main(String[] args) {
        SpringApplication.run(LogFileAnalyser.class, args);
        try {

            int nrOfRecordsFound = 0;

            /* List log files in the given directory */
            ArrayList<String> logFiles = new ArrayList<String>();
            //String pathToDir = "C:\\Users\\deonb\\Downloads\\ProdLogs\\KE_LOGS_ENGINE_20211028";
            String pathToDir = "C:\\Users\\deonb\\Downloads\\ProdLogs\\ZW-PROD-LOGS-poe-dc02-20211028";

            /* Build a list of log  files from the given directory */
            File file = new File(pathToDir);

            File[] listOfFiles = file.listFiles();

            for (File fileInDirectory : listOfFiles) {
                System.out.println("The files in the directory : " + fileInDirectory);

                boolean isZipFile = isArchive(fileInDirectory);
                System.out.println("Is it a zip/rar file ? : " + file.getName() + " === " + isZipFile);
            }

            System.out.println("The file list " + file.listFiles());
            /* Loop through every file in the list extracting certain records */
            for (String logFile : Objects.requireNonNull(file.list())) {

                BufferedReader in = new BufferedReader(new FileReader(pathToDir + "\\" + logFile));

                System.out.println("Records : " + in.readLine());

                String str;
                while ((str = in.readLine()) != null) {
                    /* Find the log line record */
                    if (str.contains("NoOfRows[") && (str.contains("time["))) {

                        System.out.println("I have found a row " + str);
                        nrOfRecordsFound++;

                        String[] data = str.split(" ");
                        String date, time, ref, nioChannelRef;
                        Integer rowCount, timeElapsed;
                        date = data[0];
                        time = data[1];
                        ref = data[4];
                        nioChannelRef = data[7];
                        rowCount = Integer.parseInt(data[14].replaceAll("[a-zA-Z\\[\\]]", ""));
                        timeElapsed = Integer.parseInt(data[16].replaceAll("[a-zA-Z\\[\\]\\.]", ""));

                        if (timeElapsed >= 5000) {

                            //System.out.println(str);
                            System.out.println("Date : " + date + " time : " + time + " reference : " + ref + " Channel reference : " + nioChannelRef + " Nr Rows : " + rowCount + " Elapsed Time : " + timeElapsed / 1000 + " seconds");
                            System.out.println("number of records analyzed : " + nrOfRecordsFound);
                            try {
                                FileWriter myWriter = new FileWriter("C:\\Users\\deonb\\Downloads\\ProdLogs\\KE_LOGS_ENGINE\\keniaLog.txt", true);
                                myWriter.write("Date : " + date + " time : " + time + " reference : " + ref + " Channel reference : " + nioChannelRef + " Nr Rows : " + rowCount + " Elapsed Time : " + timeElapsed / 1000 + " seconds" + "\n");
                                myWriter.close();
                                //System.out.println("Successfully wrote to the file.");
                            } catch (IOException e) {
                                //System.out.println("An error occurred writing to the file.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private static boolean isArchive(File f) {
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            fileSignature = raf.readInt();
            System.out.println("The file signature is : " + fileSignature);
        } catch (IOException e) {
            // handle if you like
        }

        return (fileSignature == 529205248) ? true : false;
    }

}
