package ctgraphdep.utils;

import ctgraphdep.constants.WorkCode;
import ctgraphdep.models.MonthlyWorkSummary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

public class ExportExcelUtil {

    public static boolean exportMonthlyWorkSummaryToExcel(List<MonthlyWorkSummary> data, Integer year, Integer month, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Monthly_Work_Summary_" + year + "_" + month + ".xlsx");
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return false; // User cancelled the save dialog
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Monthly Work Summary");

            // Create cell style for headers
            CellStyle headerStyle = workbook.createCellStyle();
            byte[] rgb = new byte[]{(byte)220, (byte)230, (byte)241}; // Lighter blue RGB values
            headerStyle.setFillForegroundColor(new XSSFColor(rgb, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            createHeaderCell(headerRow, 0, "Name", headerStyle);
            createHeaderCell(headerRow, 1, "Employee ID", headerStyle);


            Integer daysInMonth = YearMonth.of(year, month).lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                createHeaderCell(headerRow, day + 1, String.valueOf(day), headerStyle);
            }

            createHeaderCell(headerRow, daysInMonth + 2, "Total Worked Hours", headerStyle);
            createHeaderCell(headerRow, daysInMonth + 3, "Overtime Hours", headerStyle);
            createHeaderCell(headerRow, daysInMonth + 4, "Days Worked", headerStyle);
            createHeaderCell(headerRow, daysInMonth + 5, "Time Off Summary", headerStyle);

            // Create cell style for data rows
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Integer rowNum = 1;
            for (MonthlyWorkSummary summary : data) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, summary.getName(), dataStyle);
                createCell(row, 1, summary.getEmployeeId(), dataStyle);

                double monthlyTotalHours = 0;
                double monthlyOvertimeHours = 0;
                Integer actualWorkDays = 0;
                for (int day = 1; day <= daysInMonth; day++) {
                    Double hours = summary.getDay(day);
                    String timeOffType = summary.getTimeOffType(day);
                    if (hours != null && hours > 0) {
                        double roundedHours = roundDailyHours(hours);
                        double dailyOvertime = Math.max(0, roundedHours - WorkCode.FULL_WORKDAY_HOURS);
                        createCell(row, day + 1, formatHours(roundedHours), dataStyle);
                        monthlyTotalHours += Math.min(roundedHours, WorkCode.FULL_WORKDAY_HOURS);
                        monthlyOvertimeHours += dailyOvertime;
                        if (!isTimeOff(timeOffType)) {
                            actualWorkDays++;
                        }
                    } else if (timeOffType != null && !timeOffType.isEmpty()) {
                        createCell(row, day + 1, timeOffType, dataStyle);
                    } else {
                        createCell(row, day + 1, "", dataStyle);
                    }
                }

                Integer displayTotalHours = (int) Math.floor(monthlyTotalHours);
                Integer displayOvertimeHours = (int) Math.floor(monthlyOvertimeHours);

                createCell(row, daysInMonth + 2, displayTotalHours, dataStyle);
                createCell(row, daysInMonth + 3, displayOvertimeHours, dataStyle);
                createCell(row, daysInMonth + 4, actualWorkDays, dataStyle);
                createCell(row, daysInMonth + 5, summary.getTimeOffSummary(), dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to the selected file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            return true;
        } catch (IOException e) {
            LoggerUtil.error(ExportExcelUtil.class,"ExportUtil excel error: " + e.getMessage());
            return false;
        }
    }

    private static void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static double roundDailyHours(double hours) {
        if (hours <= WorkCode.FULL_WORKDAY_WITH_OVERTIME) {
            return Math.min(hours, WorkCode.FULL_WORKDAY_HOURS);
        } else {
            return Math.ceil(hours - WorkCode.FULL_WORKDAY_WITH_OVERTIME) + WorkCode.FULL_WORKDAY_WITH_OVERTIME;
        }
    }

    private static boolean isTimeOff(String timeOffType) {
        return timeOffType != null && (timeOffType.equals("CO") || timeOffType.equals("CM") || timeOffType.equals("SN"));
    }

    private static String formatHours(double hours) {
        return String.format("%d", (int) Math.floor(hours));
    }

    private static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        cell.setCellStyle(style);
    }
}
