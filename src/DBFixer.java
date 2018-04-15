import javax.swing.JOptionPane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DBFixer {

	private PatientsDB rawDB;
	private PatientsDB cleanDB;

	public DBFixer(PatientsDB rawDB) {
		super();
		this.rawDB = rawDB;
		this.cleanDB = new PatientsDB();
	}

	public PatientsDB getCleanDB() {
		return cleanDB;
	}

	public void fixDB() {

		// Set the beginning of test data limit:
		int requiredNumOfDaysfromTransplant = 
				Integer.parseInt(JOptionPane.showInputDialog("How namy days from transplant?"));

		int requiredNumOfDaysToTheEndOfResearch =
				Integer.parseInt(JOptionPane.showInputDialog("How namy days from transplant to the end of the research?"));

		// Scan and fix the DB:
		//	1. If the test data is N/A: discard the test
		//	2. Set the 1st test data the nearest test after the transplant
		for (Patient patient : rawDB.getPatients().values())
		{
			// Create a new patient, only clean test should be recorded:
			Patient cleanPatient = new Patient(patient.getFirstName(),patient.getLastName() ,patient.getId());

			// Set the transplant date and the 1st test after the transplant date:

			cleanPatient.setFirstTestDateAfterTheTransplant(patient.getFirstTestDateAfterTheTransplant());
			cleanPatient.setFirstTestValueAfterTheTransplant(patient.getFirstTestValueAfterTheTransplant());

			int dataSize = patient.getTestResults().size();

			boolean firstPass = true;
			// Start from the 2nd spot. save room for the last test that is under <requiredNumOfDaysfromTransplant>
			for (int i = 1 ; i < dataSize; i++)
			{

				int daysBetweenTransplantAndTest = (int) ChronoUnit.DAYS.between(
						patient.getTransplantDate().get(i), patient.getTestResultsDate().get(i));

				// All tests occurred under <requiredNumOfDaysfromTransplant> except 1st test are discarded
				if (patient.getTestResults().get(i) != -1 &&
						daysBetweenTransplantAndTest > requiredNumOfDaysfromTransplant	)
				{
					// If the previous test was the last one to be entered - insert this one and continue to the next patient
					if (daysBetweenTransplantAndTest > requiredNumOfDaysToTheEndOfResearch )
					{
						cleanPatient.addTestsDataFromOtherPatient(patient, i, true);
						// Go to next patient
						break;

					}
					else 
					{
						// Add record to cleanPatient:
						cleanPatient.addTestsDataFromOtherPatient(patient, i, true);
						if (firstPass)
						{
							cleanPatient.addTestsDataFromOtherPatient(patient, i-1, false);
							firstPass = false;
						}
					}
				}
			}
			
				
			
			// The 1st test is always inserted, patient will be processed only if there is at least 1 more valid test
			if (cleanPatient.getTestResults().size() > 1)
			{
				// Alternate first and last data for the patient:
				int cleanDataSize = cleanPatient.getTestResults().size();
				try {
				replacePatientDataEdges(requiredNumOfDaysfromTransplant, cleanPatient, 0, 1, 0);
				}
				catch(Exception e)
				{
					System.out.println("problem with patient : \n"  +cleanPatient.toString());
				}
				replacePatientDataEdges(requiredNumOfDaysToTheEndOfResearch, cleanPatient,
										cleanDataSize - 2, cleanDataSize - 1, cleanDataSize - 1 );		
				cleanDB.addPateint(cleanPatient);
			}
		}
	}

	private void replacePatientDataEdges(int requiredNumOfDaysfromTransplant, Patient cleanPatient,
										 int firstIndex, int lastIndex, int patientTestIndexToReplace) 
	{
		double y1 = cleanPatient.getTestResults().get(firstIndex);
		double y2 = cleanPatient.getTestResults().get(lastIndex);
		double x1 = (int) ChronoUnit.DAYS.between(cleanPatient.getTransplantDate().get(0),
												  cleanPatient.getTestResultsDate().get(firstIndex));
		double x2 = (int) ChronoUnit.DAYS.between(cleanPatient.getTransplantDate().get(0),
												  cleanPatient.getTestResultsDate().get(lastIndex));
		int requiredTimedValue = requiredNumOfDaysfromTransplant;
		
		double newTestValue = getCalcultedTestResult(y2, y1, x2, x1, requiredTimedValue);
		LocalDate newDateValue = cleanPatient.getTransplantDate().get(0).plusDays(requiredNumOfDaysfromTransplant);
		cleanPatient.getTestResults().remove(patientTestIndexToReplace);
		cleanPatient.getTestResults().add(patientTestIndexToReplace, newTestValue);
		cleanPatient.getTestResultsDate().remove(patientTestIndexToReplace);
		cleanPatient.getTestResultsDate().add(patientTestIndexToReplace, newDateValue);
	}
	
	private double getLinearSlop(double y2, double y1, double x2, double x1)
	{
		return (y2-y1)/(x2-x1);
	}
	
	
	// Y-y1 = slope(X-x1)
	// Y = slope(X-x1)+y1
	// Y = slope(X) + y1-slope*x1
	// ==> b = y1-slope*x1
	private double getLinearFreeElement (double x1, double y1, double slope)
	{
		return y1-slope*x1; 
	}
	
	// For linear equation: y = ax + b
	private double calculateYvalueLinearEquasion (double slope_a, double freeElement_b, double x)
	{
		return slope_a * x + freeElement_b;
	}
	
	private double getCalcultedTestResult (double y2, double y1, double x2, double x1, int requiredTimeValue)
	{
		double a = getLinearSlop(y2, y1, x2, x1);
		double b = getLinearFreeElement(x1, y1, a);
		return calculateYvalueLinearEquasion(a, b, (double) requiredTimeValue);
	}
}
