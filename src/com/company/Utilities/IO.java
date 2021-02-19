package com.company.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class IO {
    public static Double getDouble() {
        try {
            return new Scanner(System.in).nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Please Input a valid Number");
            return getDouble();
        }
    }

    public static Double getPositiveDouble(String pattern){
        if(pattern == null)
            pattern = "^[+]?([.]\\d+|\\d+[.]?\\d*)$";

        try {
            Double num = new Scanner(System.in).nextDouble();
            if (!Pattern.compile(pattern).matcher(String.format("%f", num)).matches())
                throw new InputMismatchException();
            return num;
        } catch (InputMismatchException e){
            System.out.println("Please make sure your input is correct");
            return getPositiveDouble(pattern);
        }
    }

//    public static Double getPositiveDoubleNonZero(String pattern){
//        if(pattern == null)
//            pattern = "/^\\d+([.,]\\d{0,2})?$/";
//
//        try {
//            Double num = new Scanner(System.in).nextDouble();
//            if (!Pattern.compile(pattern).matcher(String.format("%f", num)).matches())
//                throw new InputMismatchException();
//            return num;
//        } catch (InputMismatchException e){
//            System.out.println("Please make sure your input is correct");
//            return getPositiveDouble(pattern);
//        }
//    }

    public static Double getPositiveDouble(){
        return getPositiveDouble(null);
    }

    public static Integer getInteger(String pattern){
        if(pattern == null)
            pattern = "[\\d]*";

        try {
            Integer num = new Scanner(System.in).nextInt();
            if (!Pattern.compile(pattern).matcher(String.format("%d", num)).matches())
                throw new InputMismatchException();
            return num;
        } catch (InputMismatchException e){
            System.out.println("Please make sure your input is correct");
            return getInteger(pattern);
        }
    }

    public static void exitApplication(){
        System.out.println("Exiting out of Application...");
        System.exit(0);
    }

    public static String getText(String pattern){
        if(pattern == null)
            pattern = "..*";

        try {
            String txt = new Scanner(System.in).nextLine();
            if (!Pattern.compile(pattern).matcher(txt).matches())
                throw new InputMismatchException();
            return txt;
        } catch (InputMismatchException e){
            System.out.println("Please make sure your input is correct");
            return getText(pattern);
        }

    }

    /**
     * Non parameterized wrapper version of getText
     * @return
     */
    public static String getText() {
        return getText(null);
    }

    public static char getChar(String pattern){
        if(pattern == null)
            pattern = "^[A-Za-z]";

        try {
            String Text = new Scanner(System.in).next();
            if(Text.length() > 1)
                throw new InputMismatchException();

            char txt = Text.charAt(0);

            if (!Pattern.compile(pattern).matcher(Text.substring(0,1)).matches())
                throw new InputMismatchException();
            return txt;
        } catch (InputMismatchException e){
            System.out.println("Please make sure your input is correct");
            return getChar(pattern);
        }
    }

    /**
     * Reads from a text file, line by line
     * @param pathToFile - has to be in the format of "src/com/company/readme.txt"
     * @return - returns an null if nothing is found
     */
    public static String readToString(String pathToFile){
        try {
            FileReader fr = new FileReader(pathToFile);
            BufferedReader br = new BufferedReader(fr);
            String str;
            String longStr = "";

            while((str = br.readLine()) != null){
                longStr = longStr.concat(str + "\n");
            }
            br.close();
            return longStr;

        } catch (IOException e) {
            out.println("File not found");
            return null;
        }
    }

    /**
     * Reads from a text file, line by line
     * @param pathToFile - has to be in the format of "src/com/company/readme.txt"
     * @return an arraylist of strings on every line / returns null if nothing is found
     */
    public static ArrayList<String> readToList(String pathToFile) throws IOException {
        try {
            ArrayList<String> wordList = new ArrayList<>();
            Scanner scan = new Scanner(new File(pathToFile));
            scan.useDelimiter("[,\n]");

            while (scan.hasNext()) {
                wordList.add(scan.next());
            }
            scan.close();
            return wordList;

        } catch (IOException e) {
            out.println("File not found");
            return null;
        }
    }

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(dtf).toString();
        return date;
    }

    public static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(dtf).toString();
        return date;
    }


}
