import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;

import jxl.write.Boolean;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 *
 */
public class Main {

    public static void main(String[] args) {

        Translator t = new Translator();

        try {
            List<Output> outputs = t.urlCall();
            ExcelCreator.saveToExcel(outputs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
