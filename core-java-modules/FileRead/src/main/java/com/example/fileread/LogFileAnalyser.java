package com.example.fileread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

//TODO : read zip files, access logs on remote server, traverse directoriy structure
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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        SpringApplication.run(LogFileAnalyser.class, args);
        try {

            /* List log files in the given directory */
            ArrayList<String> logFiles = new ArrayList<String>();
            String pathToDir = "C:\\Users\\deonb\\Downloads\\ProdLogs\\KE_ANdreLogs";
            //String pathToDir = "C:\\Users\\deonb\\Downloads\\ProdLogs\\ZW-PROD-LOGS-engine-dc02-20211028";
            //String pathToDir = "C:\\Users\\deonb\\Downloads\\ProdLogs\\ZW-PROD-LOGS-poe-dc02-20211028";

            /* Build a list of log  files from the given directory */
            File file = new File(pathToDir);
            File[] listOfFiles = file.listFiles();

            // Loop through the files in the directory and extract the archived files
            //TODO : extraction cannot extract an archive of archived files
            /*for (File fileInDirectory : listOfFiles) {
                System.out.println("The files in the directory : " + fileInDirectory);

                boolean isZipFile = isArchive(fileInDirectory);
                System.out.println("Is it an archived file ? : " + file.getName() + " === " + isZipFile);

                // Extract the archive to the current directory
                if(isZipFile) {
                    String archivedFileName = fileInDirectory.toString();
                    System.out.println("The archived file path is " + archivedFileName);
                }

            }*/

            System.out.println("The file list " + file.listFiles());
            /* Loop through every file in the list extracting certain records */
            for (String logFile : Objects.requireNonNull(file.list())) {

                BufferedReader in = new BufferedReader(new FileReader(pathToDir + "\\" + logFile));

                System.out.println("Records : " + in.readLine());

                String str;
                while ((str = in.readLine()) != null) {

                    boolean layOutDiffer = false;
                    /* Find the log line record */
                    if (str.contains("NoOfRows[") && (str.contains("time["))) {

                        System.out.println("I have found a row " + str);
                        //System.out.println("Index of NoOfRows " + str.indexOf("NoOfRows"));
                        System.out.println("Extraction -- " + str.substring(str.indexOf("NoOfRows")));
                        if (str.contains("ComponentQueryService")) {
                            layOutDiffer = true; //Zim logs has this type of query call, resulting in a different layout
                        }

                        try {
                            String[] data = str.split(" ");

                            String date, time, ref, nioChannelRef;
                            Integer rowCount, timeElapsed;

                            //TODO build dynamic date extract for Kenia and Zim
                            date = data[0].substring(4); // -- Kenia logs !""
                            System.out.println("Date extracted : " + date);
                            //date = data[0]; // -- Zim logs !

                            time = data[1].substring(0, 12);
                            System.out.println("Time extracted " + time);
                            ref = data[4];
                            System.out.println("Reference extracted :" + ref);
                            nioChannelRef = data[7];
                            System.out.println("Extracted Nio " + nioChannelRef);
                            System.out.println("13 " + data[13]);
                            System.out.println("15 " + data[15]);
                            System.out.println("14 " + data[14]);
                            System.out.println("16 " + data[16]);

                            if(layOutDiffer) {
                                rowCount = Integer.parseInt(data[13].replaceAll("[a-zA-Z\\[\\]]", ""));
                                timeElapsed = Integer.parseInt(data[15].replaceAll("[a-zA-Z\\[\\]\\.]", ""));
                            } else {
                                rowCount = Integer.parseInt(data[14].replaceAll("[a-zA-Z\\[\\]]", ""));
                                timeElapsed = Integer.parseInt(data[16].replaceAll("[a-zA-Z\\[\\]\\.]", ""));
                            }

                            if (timeElapsed >= 0) {

                                System.out.println(str);
                                System.out.println("Date : " + date + " : time : " + time + " : reference : " + ref + " :Channel reference : " + nioChannelRef + " : Nr Rows : " + rowCount + " : Elapsed Time : " + timeElapsed / 1000 + " : seconds");

                                try {
                                    FileWriter myWriter = new FileWriter("C:\\Users\\deonb\\Downloads\\ProdLogs\\KE_ANdreLogs\\AndreLogs.txt", true);
                                    myWriter.write(date.trim() + "\t"+ time.trim() + "\t" + ref.trim() + "\t" + nioChannelRef.trim() + "\t" + rowCount + "\t" + timeElapsed / 1000 + "\n");
                                    myWriter.close();
                                    System.out.println(ANSI_BLUE + "Successfully wrote to the file." + ANSI_RESET);
                                } catch (IOException e) {
                                    System.out.println("An error occurred writing to the file.");
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Exception splitting line" + ANSI_RED + ex + ANSI_RESET);
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /*
    * Checks for archived files (ZIP, GZ and RAR file signatures only)
    * */
    //TODO : get file signatures for all other types of archive files
    private static boolean isArchive(File f) {
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            fileSignature = raf.readInt();

            // Determine the file's signature
            //System.out.println("The file signature is : " + fileSignature);

        } catch (IOException e) {
            //TODO  Handle exception
            System.out.println("Archive exception occured");
        }
        //                          .GZ                          .RAR                            .ZIP
        return (fileSignature == 529205248 || fileSignature == 1382117921 || fileSignature == 1347093252) ? true : false;
    }

}
