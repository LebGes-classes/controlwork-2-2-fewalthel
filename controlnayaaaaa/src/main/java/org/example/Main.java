package org.example;

import org.apache.poi.xssf.usermodel.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Main {
    //создание списка всех программ
    private static List<Program> getProgramList(){
        List<Program> programList = new ArrayList<>();
        String currentChannel = "";
        String filePath = "controlnayaaaaa/data.txt";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    //в строке с названием канала, убираем # и сохраняем в переменную
                    currentChannel = line.substring(1).trim();
                } else {
                    //строка со временем и названием программы
                    String time = line.trim();
                    String name = bufferedReader.readLine().trim();
                    BroadcastsTime broadcastsTime = new BroadcastsTime(time);
                    Program program = new Program(currentChannel, broadcastsTime, name);
                    programList.add(program);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return programList;
    }

    //вывод всех программ, которые идут сейчас
    public static void printNowPrograms(String nowTime, List<Program> programList){
        BroadcastsTime currentTime = new BroadcastsTime(nowTime);
        for (int i = 0; i < programList.size(); i++) {
            Program currentProgram = programList.get(i);

            Program nextProgram = null;
            if (i+1 < programList.size()) { //если мы еще не дошли до конца списка, значит есть последующая программа
                nextProgram = programList.get(i + 1);
            }

            //если попадаем во временные рамки
            if (currentProgram.getTime().equals(currentTime) || (currentProgram.getTime().before(currentTime) && (nextProgram == null || nextProgram.getTime().after(currentTime)))) {
                currentProgram.print();
            }
        }
    }

    //поиск программы по названию
    public static void findProgramsByName(String nameToSearch, List<Program> programList) {
        for (Program program : programList) {
            if (program.getName().equalsIgnoreCase(nameToSearch)) {
                program.print();
            }
        }
    }

    //поиск программ по названию канала и нынешнему времени
    public static void findChannelNowPrograms(String channelName,String nowTime,List<Program> programList){
        List<Program> channelList = new ArrayList<>();
        for (Program program : programList) {
            if (program.getChannel().equalsIgnoreCase(channelName)) {
                channelList.add(program);
            }
        }
        printNowPrograms(nowTime, channelList);
    }

    //поиск программ во временном проммежутке
    public static void findChannelProgramsInTimeRange(String channelName, String startTime, String endTime, List<Program> programList) {
        BroadcastsTime start = new BroadcastsTime(startTime);
        BroadcastsTime end = new BroadcastsTime(endTime);

        programList.stream()
                .filter(program -> program.getChannel().equalsIgnoreCase(channelName))
                .filter(program -> (program.getTime().after(start) || program.getTime().equals(start)) && (program.getTime().before(end) || program.getTime().equals(end)))
                .forEach(program -> program.print());
    }

    //сохранение данных в таблицу
    public static void saveProgramListToXlsx(List<Program> programList, String outputPath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Program List");

        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Channel");
        header.createCell(1).setCellValue("Broadcast Time");
        header.createCell(2).setCellValue("Program Name");

        //заполняем данные
        int rowNum = 1;
        for (Program program : programList) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(program.getChannel());
            row.createCell(1).setCellValue(program.getTime().toString());
            row.createCell(2).setCellValue(program.getName());
        }

        // записываем созданную книгу в файл
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        List<Program> programList = getProgramList();

        //сортировка по каналу, затем по времени показа
        programList.sort(Comparator.comparing(Program::getChannel).thenComparing(Program::getTime));
        programList.forEach(program -> program.print());

        //вывод текущих программ
        printNowPrograms("16:10",programList);

        //поиск программы
        findProgramsByName("Последний мент. 12-я серия",programList);

        //поиск программ по названию канала и нынешнему времени
        findChannelNowPrograms("Первый","16:10",programList);

        //поиск программ по названию канала и временному промежутку
        findChannelProgramsInTimeRange("Первый", "15:00", "16:10", programList);

        //сохранение данных
        saveProgramListToXlsx(programList, "ProgramList.xlsx");
    }
}