import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class DBFixer {

	private PatientsDB rawDB;
	private PatientsDB cleanDB;
	private Logger log;


	public DBFixer(PatientsDB rawDB, Logger log) {
		super();
		this.rawDB = rawDB;
		this.cleanDB = new PatientsDB();
		this.log = log;
	}

	public PatientsDB getCleanDB() {
		return cleanDB;
	}

	public void fixDB(int requiredNumOfDaysfromTransplant, int requiredNumOfDaysToTheEndOfInterval ) {

		// Scan and fix the DB:
		// For each patient;
		//  Disassemble the raw data into intervals,
		// 	Correct using linear interpolation the 1st interval on both edges, other intervals only the last edge	
		for (Patient patient : rawDB.getPatients().values())
		{

			int dataSize = patient.getTestResults().size();
			
			//interval division helpers:
			int interval = 0;
			boolean newInterval = false;
			int intervalStartTest = 0;

			
			//create data for 1st interval in order to insert the 1st test:
			ArrayList<LocalDate> tempTransDateMat = new ArrayList<LocalDate>();
			ArrayList<LocalDate> tempTestResDateMat = new ArrayList<LocalDate>();
			ArrayList<Integer> tempNumOfTransMat = new ArrayList<Integer>();
			ArrayList<String> tempTypeOfTestMat = new ArrayList<String>();
			ArrayList<Double> tempTestResMat = new ArrayList<Double>();

			// Insert 1st test for each patient manually:
			tempTransDateMat.add(0,patient.getTransplantDate().get(0));
			tempTestResDateMat.add(0,patient.getTestResultsDate().get(0));
			tempNumOfTransMat.add(0,patient.getNumOfTransplant().get(0));
			tempTestResMat.add(0,patient.getTestResults().get(0));
			tempTypeOfTestMat.add(0, patient.getTypeOfTest().get(0));

			// For each patient, continue from the 2nd spot.
			int testNum = 1;
			while (testNum < dataSize)
			{
				if (newInterval)
				{
					// Create data for current interval:
					tempTransDateMat = new ArrayList<LocalDate>();
					tempTestResDateMat = new ArrayList<LocalDate>();
					tempNumOfTransMat = new ArrayList<Integer>();
					tempTypeOfTestMat = new ArrayList<String>();
					tempTestResMat = new ArrayList<Double>();
				}
				
				// Check for test date, time from transplant should be > requiredNumOfDaysfromTransplant
				int daysBetweenTransplantAndTest = (int) ChronoUnit.DAYS.between(
									patient.getTransplantDate().get(0), patient.getTestResultsDate().get(testNum));
				
				if (daysBetweenTransplantAndTest < requiredNumOfDaysfromTransplant)	 {testNum++; continue;}
				else
				{
					//it is the beginning of an interval
					
					//inner loop until the time period of the interval > requiredNumOfDaysToTheEndOfInterval and data is not finished
					int tempTestNum = testNum;
					int deltaFor1stIntervalMess = 0; // 1st interval includes the 1st test for interpolation, this night effect interval length, should be fixed.
	
					while ( (tempTestNum < dataSize) &&
							((int) ChronoUnit.DAYS.between(patient.getTestResultsDate().get(intervalStartTest),
							patient.getTestResultsDate().get(tempTestNum)) <= requiredNumOfDaysToTheEndOfInterval + deltaFor1stIntervalMess)) 
							
					{
						// Temporary test is within the interval
						tempTransDateMat.add(patient.getTransplantDate().get(tempTestNum));
						tempTestResDateMat.add(patient.getTestResultsDate().get(tempTestNum));
						tempNumOfTransMat.add(patient.getNumOfTransplant().get(tempTestNum));
						tempTestResMat.add(patient.getTestResults().get(tempTestNum));
						tempTypeOfTestMat.add(patient.getTypeOfTest().get(tempTestNum));					
						if (deltaFor1stIntervalMess == 0 && interval == 0)
						{
							// Extend 1st interval length
							deltaFor1stIntervalMess = (int) ChronoUnit.DAYS.between(patient.getTestResultsDate().get(intervalStartTest),
								patient.getTestResultsDate().get(tempTestNum));
						}
						tempTestNum++;
					}
					
					// out of the while loop = tempTestNum is out of interval scope:
					// If there is 1 more test - insert it for interpolation purpose.
					
					if (tempTestNum < dataSize)
					{
						tempTransDateMat.add(patient.getTransplantDate().get(tempTestNum));
						tempTestResDateMat.add(patient.getTestResultsDate().get(tempTestNum));
						tempNumOfTransMat.add(patient.getNumOfTransplant().get(tempTestNum));
						tempTestResMat.add(patient.getTestResults().get(tempTestNum));
						tempTypeOfTestMat.add(patient.getTypeOfTest().get(tempTestNum));	
					}
					// else, previous test was the last Test of this patient - duplicate the last test for same results after interpolation
					else
					{
						tempTransDateMat.add(patient.getTransplantDate().get(tempTestNum - 1));
						tempTestResDateMat.add(patient.getTestResultsDate().get(tempTestNum - 1));
						tempNumOfTransMat.add(patient.getNumOfTransplant().get(tempTestNum - 1));
						tempTestResMat.add(patient.getTestResults().get(tempTestNum - 1 ));
						tempTypeOfTestMat.add(patient.getTypeOfTest().get(tempTestNum - 1));
					}
					
					// update testNum, update newInterval, update intervalStartTest
					testNum = tempTestNum;
					newInterval = true;
					intervalStartTest = testNum;
				}
				
				//---------------------------------------------------------------------------------------------------------------------------------
				// Interpolation
				//---------------------------------------------------------------------------------------------------------------------------------
				
				// The 1st test is always inserted, interval will be processed only if there is at least 1 more valid test
				if (tempTestResMat.size() > 1)
				{
					// Alternate first and last data for the patient:
					int intervalDataSize = tempTestResMat.size();
					double[] A0_B0 = null;
					if (interval == 0)
					{
						A0_B0 = replacePatientDataEdges(requiredNumOfDaysfromTransplant,tempTestResMat,tempTransDateMat,tempTestResDateMat, 0, 1, 0);
					}

					double[] An_Bn = 
							replacePatientDataEdges(requiredNumOfDaysToTheEndOfInterval,tempTestResMat,tempTransDateMat,tempTestResDateMat,
									intervalDataSize - 2, intervalDataSize - 1, intervalDataSize - 1);
					
					
					// Update patients DB with the new interval
					patient.updateInerval(tempTransDateMat,
							tempTestResDateMat,
							tempNumOfTransMat,
							tempTestResMat,
							tempTypeOfTestMat,
							interval);
					interval++;
					
					
					// Log the data if it's the interesting patient
					if (patient.getId() == log.getPatientID())
					{
						log.logData("\nAfter scaning, tests dates list:\n\t",tempTestResDateMat.toString());
						log.logData("\nAfter scanning:, tests values lists:\n\t", tempTestResMat.toString());
						if (interval == 0)
						{
							log.logData("\nLinear equation factors, beginning of period:\n\t","Y = " + A0_B0[0] + "*X + " + A0_B0[1]);
						}
						log.logData("\nLinear equation factors, end of period:\n\t", "Y = " + An_Bn[0] + "*X + " + An_Bn[1]);
					}
				}
			}
		}
	}
	
	// Interpolation helper methods
	//---------------------------------------------------------------------------------------------------------------------------------------------------
	private double[] replacePatientDataEdges(int requiredNumOfDaysfromTransplant,
			ArrayList<Double> tempTestResMat,
			ArrayList<LocalDate> tempTransDateMat,
			ArrayList<LocalDate> tempTestResDateMat,
			int firstIndex, int lastIndex, int patientTestIndexToReplace) 
	{
		double y1 = tempTestResMat.get(firstIndex);
		double y2 = tempTestResMat.get(lastIndex);
		double x1 = (int) ChronoUnit.DAYS.between(tempTransDateMat.get(0),
				tempTestResDateMat.get(firstIndex));
		double x2 = (int) ChronoUnit.DAYS.between(tempTransDateMat.get(0),
				tempTestResDateMat.get(lastIndex));
		int requiredTimedValue = requiredNumOfDaysfromTransplant;

		LinearEquation le = 
				getCalcultedTestResult(y2, y1, x2, x1, requiredTimedValue);
		LocalDate newDateValue = tempTransDateMat.get(0).plusDays(requiredNumOfDaysfromTransplant);
		tempTestResMat.remove(patientTestIndexToReplace);
		tempTestResMat.add(patientTestIndexToReplace, le.Y);
		tempTestResDateMat.remove(patientTestIndexToReplace);
		tempTestResDateMat.add(patientTestIndexToReplace, newDateValue);
		double[] a_b = {le.a,le.b};
		return a_b;


	}

	private double getLinearSlop(double y2, double y1, double x2, double x1)
	{
		return (y2-y1)/(x2-x1);
	}

	private double getLinearFreeElement (double x1, double y1, double slope)
	{
		return y1-slope*x1; 
	}

	// For linear equation: y = ax + b
	private double calculateYvalueLinearEquasion (double slope_a, double freeElement_b, double x)
	{
		return slope_a * x + freeElement_b;
	}

	private LinearEquation getCalcultedTestResult (double y2, double y1, double x2, double x1, int requiredTimeValue)
	{

		double a = getLinearSlop(y2, y1, x2, x1);
		double b = getLinearFreeElement(x1, y1, a);
		double Y = calculateYvalueLinearEquasion(a, b, (double) requiredTimeValue);
		return new LinearEquation(a, b, Y);
	}

	public class LinearEquation{
		private double a;
		private double b;
		private double Y;

		public LinearEquation(double a, double b, double Y)
		{
			this.a = a;
			this.b = b;
			this.Y = Y;
		}
	}
}
