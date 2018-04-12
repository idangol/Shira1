import java.util.HashMap;


/**
 * 
 * @author idan
 *	This Class represents the input excel file DB in form of a list of patients, can accessed by ID
 */

public class PatientsDB {
	
	private HashMap<Integer,Patient> patients;
	
	public PatientsDB() {
		super();
		this.patients = new HashMap<Integer,Patient>();
	}
	
	public HashMap<Integer, Patient> getPatients() {
		return patients;
	}

	public void addPateint(Patient patient)
	{
		if (patient != null)
		{
			this.patients.put(patient.getId(), patient);
		}
	}

	@Override
	public String toString() {
		
		StringBuffer s = new StringBuffer();
		for (Patient p : patients.values())
		{
			s.append("\n\n" + p.toString());
		}
		
		return new String(s);
	}
	
	

	
	
	

}
