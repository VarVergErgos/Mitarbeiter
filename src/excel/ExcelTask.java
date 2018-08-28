package excel;

import betreuer.Betreuer;
import betreuer.BetreuerDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import util.BigDezimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Mit dieser Klasse die von Task erbt, bewerkstelligen
 * wir die Seicherung als Excel-Datei
 *
 */
public class ExcelTask extends Task<List<String>>{
private List<String> copied;

private File files;
private BetreuerDAO dao;
private int anzahl = 0;
private int rowNum = 0;



    public ExcelTask(File file){
      this.files = file;
    }

    @Override
    protected List<String> call() throws Exception {
        dao = new BetreuerDAO();
        anzahl = dao.getAnzahlDatensaetze();
        //Spalten fuer die Exceldatei

        String[] columns = {"id", "persnr", "vorname", "nachname",
            "strasse", "wohnort", "funktion",
            "rd", "rdname", "Eintrittsdatum", "Geschlecht", "Gehalt"};

        @SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Betreuerdaten");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);

        //Spalten Ueberschriften
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
            headerCellStyle.setRotation((short) 90);
        }


        rowNum = 1;
        copied = new ArrayList<String>();

        //Jetzt die Zeilen der Daten die exportiert werden
        for (Betreuer liste : dao.getAllDaten()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(liste.getId());
            row.createCell(1).setCellValue(liste.getPersnr());
            row.createCell(2).setCellValue(liste.getVorname());
            row.createCell(3).setCellValue(liste.getNachname());
            row.createCell(4).setCellValue(liste.getStrasse());
            row.createCell(5).setCellValue(liste.getWohnort());
            row.createCell(6).setCellValue(liste.getFunktion());
            row.createCell(7).setCellValue(liste.getRd());
            row.createCell(8).setCellValue(liste.getRdname());


            //Eintrittsdatum
            Cell cellDateAp = row.createCell(9);
			cellDateAp.setCellValue(
			String.format("%td.%tm.%tY", liste.getEintrittsdatum(), liste.getEintrittsdatum(), liste.getEintrittsdatum()));
			CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
			cellDateAp.setCellStyle(cellStyle);

            row.createCell(10).setCellValue(liste.getGeschlecht());

            //Gehalt wir nutzen unser Util-Klasse BigDezimal um auf 2 Nackommastellen
            //zu runden wir Formatieren dann noch fuer Excel mit setDataFormat
            Cell cellAp24 = row.createCell(11);
			cellAp24.setCellValue(BigDezimal.doubleToBigDecimalTwoDigit(liste.getGehalt()));
			CellStyle styleAp24 = workbook.createCellStyle();
			DataFormat cf24 = workbook.createDataFormat();
			styleAp24.setDataFormat(cf24.getFormat("#,##0.00#"));
			cellAp24.setCellStyle(styleAp24);

            String strValue = liste.getId() + " " + liste.getPersnr()
                    + liste.getNachname() + "  " + liste.getVorname()
                    + liste.getGehalt();
            copied.add(strValue);
            this.updateProgress(rowNum, anzahl);
            this.updateMessage(strValue);

            /*
            System.out.println("von : " + rowNum + " bis : " + anzahl + " "
            + strValue);
            */
            Thread.sleep(20);
        }

            // Spaltengroesse anpassen
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ausgabe in Datei speichern
            FileOutputStream fileOut = new FileOutputStream(files);

            workbook.write(fileOut);
            fileOut.close();

        return copied;

    }



}



