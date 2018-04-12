
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TWAcalculator {
	
	private PatientsDB cleanDB;
	
	public TWAcalculator(PatientsDB cleabDB) {
		this.cleanDB = cleabDB;
	}
	
	public void calcTWA(XSSFWorkbook outputWorkbook)
	{
		int outputFileRow = 0;
		XSSFSheet sheet1 = outputWorkbook.createSheet();
		XSSFRow header = sheet1.createRow(outputFileRow);
		XSSFCell cell1 = header.createCell(0);
		cell1.setCellValue("Patient ID");
		XSSFCell cell2 = header.createCell(1);
		cell2.setCellValue("TWA");
		
		for (Patient patient : cleanDB.getPatients().values())
		{
			
			double patientTWA = patient.calcTWA();
			if (patientTWA == Double.MIN_VALUE)
			{
				System.out.println("\nPatient ID: " + patient.getId() + " problem with TWA calculation");
			}
			//System.out.println("\nPatient ID: " + patient.getId() + "\n\t TWA: " + patientTWA );
			else
			{
				outputFileRow++;		
				XSSFRow row = sheet1.createRow(outputFileRow);
				row.createCell(0).setCellValue(patient.getId());
				row.createCell(1).setCellValue(patientTWA);
			}
		}
	}
	
	public void writeToFile(Workbook output, String outputDest)
	{
		 try (OutputStream fileOut = new FileOutputStream(outputDest)) {
			 output.write(fileOut);
		    } catch (IOException e) {
				e.printStackTrace();
				System.out.println("problem with writing output file");
		    }
	}
	

}
