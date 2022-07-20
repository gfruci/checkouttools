import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Boolean;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 */
public class ExcelCreator {

    public static void saveToExcel(List<Output> outputs) throws IOException, WriteException {

        //Create xls file
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xls";

        WritableWorkbook workbook = Workbook.createWorkbook(new File(fileLocation));
        WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

        //Create header
        WritableCellFormat headerFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD);
        headerFormat.setFont(font);
        headerFormat.setBackground(Colour.LIGHT_BLUE);
        headerFormat.setWrap(true);

        Label headerLabel = new Label(0, 0, "key name", headerFormat);
        sheet.setColumnView(0, 60);
        sheet.addCell(headerLabel);

        headerLabel = new Label(1, 0, "MISSING UNHOTELLING KEY", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(2, 0, "DIFFERENT UNHOTELLING TEXT", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(3, 0, "NO PROPERTY KEY", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(4, 0, "UNHOTELLING PROPERTY NOT TRANSLATED", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(5, 0, "original content en_GB", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(6, 0, ".unhoteling content en_GB", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(7, 0, ".unhoteling.property content en_GB", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);


        int column = 0;
        int row = 1;
        for (Output output : outputs) {

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setWrap(true);
            setCellColourCode(cellFormat, output);

            Label keyName = new Label(column, row, output.getKeyName(), cellFormat);
            sheet.addCell(keyName);
            column++;


            WritableCellFormat format = new WritableCellFormat();
            format.setWrap(true);

            format.setBackground(Colour.WHITE);
            if (output.isMissingUnhotellingKey() != null) {
                Boolean missingUnhotellingKey = new Boolean(column, row, output.isMissingUnhotellingKey(), format);
                sheet.addCell(missingUnhotellingKey);
            }

            column++;


            if (output.isDifferentUnhotellingText() != null) {
                Boolean differentUnhotellingText = new Boolean(column, row, output.isDifferentUnhotellingText(), format);
                sheet.addCell(differentUnhotellingText);
            }

            column++;

            if (output.isNoPropertyKey() != null) {
                Boolean noPropertyKey = new Boolean(column, row, output.isNoPropertyKey(), format);
                sheet.addCell(noPropertyKey);
            }

            column++;

            if (output.isUnhotellingPropertyNotTranslated() != null) {
                Boolean unhotellingNotTranslated = new Boolean(column, row, output.isUnhotellingPropertyNotTranslated(), format);
                sheet.addCell(unhotellingNotTranslated);
            }

            column++;

            Label originalContentEn_GB = new Label(column, row, output.getOriginalContentEN_GB(), format);
            sheet.addCell(originalContentEn_GB);

            column++;

            Label unhotelingContentEn_GB = new Label(column, row, output.getUnhotellingContentEN_GB(), format);
            sheet.addCell(unhotelingContentEn_GB);

            column++;

            Label unhotelingPropertyContentEn_GB = new Label(column, row, output.getUnhotellingPropertyContent(), format);
            sheet.addCell(unhotelingPropertyContentEn_GB);


            column = 0;
            row++;
        }

        workbook.write();
        workbook.close();
    }

    private static void setCellColourCode(WritableCellFormat cellFormat, Output output) throws WriteException {
        if (output.getColorCode() == ColorCodes.GREEN) {
            cellFormat.setBackground(Colour.GREEN);
        } else if (output.getColorCode() == ColorCodes.BROWN) {
            cellFormat.setBackground(Colour.BROWN);
        } else if (output.getColorCode() == ColorCodes.RED) {
            cellFormat.setBackground(Colour.RED);
        }
    }
}
