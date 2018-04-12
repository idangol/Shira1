import javax.swing.JOptionPane;
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

			for (int i = 1 ; i < dataSize ; i++)
			{

				int daysBetweenTransplantAndFirstTest = (int) ChronoUnit.DAYS.between(
						patient.getTransplantDate().get(i), patient.getTestResultsDate().get(i));
				
				// All tests occured under <requiredNumOfDaysfromTransplant> except 1st test are discarded
				if (patient.getTestResults().get(i) != -1 ||
						daysBetweenTransplantAndFirstTest > requiredNumOfDaysfromTransplant	)
				{
					// Add record to cleanPatient:
					cleanPatient.addTestsDataFromOtherPatient(patient, i);
				}
			}
			
			// The 1st test is always inserted, patient will be processed only if there is at least 1 more valid test
			if (cleanPatient.getTestResults().size() > 1)
			{
				cleanDB.addPateint(cleanPatient);
			}
		}
	}
}
