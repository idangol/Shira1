import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;



public class PatientsDataParser {
	
	final static int LAST_NAME_INDEX = 0;
	final static int FIRST_NAME_INDEX = 1;
	final static int ID_INDEX = 2;
	final static int TRANSPLANT_DATE_INDEX = 3;
	final static int NUMBER_OF_TRANSPLANT_INDEX = 4;
	final static int TEST_DATE_INDEX = 8; // I had to manipulate the excel file...
	final static int TEST_NAME_INDEX = 6;
	final static int TEST_RESULT_INDEX = 7;
	
	private HSSFWorkbook InputWorkBook;
	private PatientsDB patientRawDB;
	private Logger log;

	public PatientsDataParser(HSSFWorkbook excelFile, PatientsDB db, Logger log) {
		super();
		this.InputWorkBook = excelFile;
		this.patientRawDB = db;
		this.log = log;
	}
	
	public void parseExcel()
	{
		for (int sheet = 0; sheet < InputWorkBook.getNumberOfSheets(); sheet++)
		{
			HSSFSheet sheet1 = InputWorkBook.getSheetAt(sheet);
			int rowsInFile = sheet1.getPhysicalNumberOfRows();
			/*System.out.println("Sheet " + sheet + " \"" + InputWorkBook.getSheetName(sheet) + "\" has " + rows
					+ " row(s).");*/
			
			for (int row = 1 ; row < rowsInFile ; row++)
			{	
				HSSFRow currentRow = sheet1.getRow(row);
				
				// check for a new patient:
				HSSFCell cell = currentRow.getCell(ID_INDEX);
				int currentID =Integer.parseInt(cell.getStringCellValue());
				
				// check for a new Patient: 
				// if the raw pateints DB doesn't contains the patients ID
				boolean isNewPatient = !(patientRawDB.getPatients().keySet().contains(new Integer(currentID)));
				Patient patient = null;
				if (isNewPatient)
				{
					HSSFCell tempCell = null;
					String lastName =(currentRow.getCell(LAST_NAME_INDEX)).getStringCellValue();
					String firstName =(currentRow.getCell(FIRST_NAME_INDEX)).getStringCellValue();
					int id = Integer.parseInt((currentRow.getCell(ID_INDEX)).getStringCellValue());
					patient = new Patient (firstName, lastName, id);
					patientRawDB.addPateint(patient);
				}
				else
				{
					patient = patientRawDB.getPatients().get((Integer) currentID);
				}

				// Update patient's data:
				 parseRowToPatient(currentRow, patient);
			}
			
			// After reading all rows in the file = prompt for required patient and start logging:
			int IDToLog = Integer.parseInt(JOptionPane.showInputDialog("Pls insert required patient ID"));
			log.logData("Raw data: ", patientRawDB.getPatients().get(IDToLog).toString());
		}
	}
	
	private void addDataToExistingPatient(HSSFRow currentRow, int currentID) {
		// TODO Auto-generated method stub
		
	}

	static private Patient parseRowToPatient(HSSFRow row, Patient patient)
	{
		
		//parse data from excel to patient object		
		parseTransplantDatetoPatient(row, patient);

		parseTestResultDateToPatient(patient, row);

		parseTransplantNumbertoPatient(row, patient);
		
		parseTypeOfTesttoPatient(row, patient);
		
		parsetestResultToPatient(row, patient);
		
		patient.setFirstTestDateAfterTheTransplant(patient.getTestResultsDate().get(0));
		patient.setFirstTestValueAfterTheTransplant(patient.getTestResults().get(0));
			
		return patient;
	}


	/**
	 * @param patient - to be update
	 * @param row - the data to update from the excel file
	 */
	private static void parseTestResultDateToPatient(Patient patient, HSSFRow row) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");		
		LocalDate testResultDate = LocalDate.parse(row.getCell(TEST_DATE_INDEX).getStringCellValue(),formatter);
		patient.addTestresultDate(testResultDate);
	}

	private static void parseTransplantDatetoPatient(HSSFRow row, Patient patient) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yy");		
		LocalDate transplantDate = LocalDate.parse(row.getCell(TRANSPLANT_DATE_INDEX).getStringCellValue(),formatter);
		patient.addTransplantDate(transplantDate);
	}
	private static void parsetestResultToPatient(HSSFRow row, Patient patient) {
		
		Double testRes = -1.0;
		
		try {
			testRes = Double.parseDouble((row.getCell(TEST_RESULT_INDEX).getStringCellValue()));
		}
		catch (NumberFormatException e)
		{
			System.out.println("problem with row: " + row.getRowNum() + "\nvalue of -1.0 is assigned to this exam");
			patient.addTestResults(-1.0);
			return;
		}
		patient.addTestResults(testRes);
	}

	private static void parseTypeOfTesttoPatient(HSSFRow row, Patient patient) {
		String typeOfTest = (row.getCell(TEST_NAME_INDEX).getStringCellValue()); 
		patient.addTypeOftest(typeOfTest);
	}

	private static void parseTransplantNumbertoPatient(HSSFRow row, Patient patient) {
		
		Integer numberOfTransplant = -1;
		
		try {
		numberOfTransplant = (int) (row.getCell(NUMBER_OF_TRANSPLANT_INDEX).getNumericCellValue());
		}
		catch(IllegalStateException e)
		{
			System.out.println("problem with row: " + row.getRowNum() + "\nvalue of -1.0 is assigned to this transplant number");
			patient.addNumberOfTransplant(-1);
			return;
		}
		patient.addNumberOfTransplant(numberOfTransplant);
	}
}
