
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TWAcalculator {
	
	private PatientsDB DB;
	private Logger log;
	
	public TWAcalculator(PatientsDB DB, Logger log) {
		this.DB = DB;
		this.log = log;
	}
	
	public void calcTWA(XSSFWorkbook outputWorkbook)
	{
		int outputFileRow = 0;
		XSSFSheet sheet1 = outputWorkbook.createSheet();
		XSSFRow header = sheet1.createRow(outputFileRow);
		XSSFCell cell1 = header.createCell(0);
		cell1.setCellValue("Patient ID");
		XSSFCell cell2 = header.createCell(1);
		
		
		// TODO: update column header s.t. times will be shown per patient
		cell2.setCellValue("TWA");
		
		//TODO: patients need to be listed according to their ID
		
		for (Patient patient : DB.getPatients().values())
		{
			
			double[] patientTWA = patient.calcTWA();
			if (patientTWA == null)
			{
				System.out.println("\nPatient ID: " + patient.getId() + " problem with TWA calculation");
			}
			
			else
			{
				outputFileRow++;		
				XSSFRow row = sheet1.createRow(outputFileRow);
				row.createCell(0).setCellValue(patient.getId());
				for (int j = 0 ; j < patientTWA.length ; j++)
				{
					row.createCell(j + 1).setCellValue(patientTWA[j]);
				}
			}
			if (patient.getId() == log.getPatientID())
			{
				log.logData("Final TWA: ", "" + patientTWA );
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
