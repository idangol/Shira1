
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TWAcalculator {
	
	private PatientsDB DB;
	private Logger log;
	private int requiredNumOfDaysfromTransplant;
	private int requiredNumOfDaysToTheEndOfInterval;
	
	public TWAcalculator(PatientsDB DB, Logger log, int requiredNumOfDaysfromTransplant, int requiredNumOfDaysToTheEndOfInterval) {
		this.DB = DB;
		this.log = log;
		this.requiredNumOfDaysfromTransplant = requiredNumOfDaysfromTransplant;
		this.requiredNumOfDaysToTheEndOfInterval = requiredNumOfDaysToTheEndOfInterval;
	}
	
	public int getRequiredNumOfDaysfromTransplant() {
		return requiredNumOfDaysfromTransplant;
	}

	public int getRequiredNumOfDaysToTheEndOfInterval() {
		return requiredNumOfDaysToTheEndOfInterval;
	}

	public void calcTWA(XSSFWorkbook outputWorkbook)
	{
		int outputFileRow = 0;
		XSSFSheet sheet1 = outputWorkbook.createSheet();
		XSSFRow header = sheet1.createRow(outputFileRow);
		XSSFCell cell1 = header.createCell(0);
		cell1.setCellValue("Patient ID");
		
		//TODO: patients need to be listed according to their ID
		int maxNumOfIntervals = 0;
		for (Patient patient : DB.getPatients().values())
		{
			 
			int  finalNumOfColumns = patient.getTestResultsMatrix().size();
			maxNumOfIntervals = Math.max(finalNumOfColumns,maxNumOfIntervals);
			
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
		
		for (int col = 1 ; col < maxNumOfIntervals ; col++)
		{
			XSSFCell tempCell = header.createCell(col);
			int periodStart = ((col - 1) * this.getRequiredNumOfDaysToTheEndOfInterval()) + this.getRequiredNumOfDaysfromTransplant();
			int periodEnd = periodStart + this.getRequiredNumOfDaysToTheEndOfInterval();
			tempCell.setCellValue("TWA: " + periodStart + " - " + periodEnd);
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
