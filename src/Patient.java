import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Patient {
	
	//private final int MAGIC_NUM_OF_DAYS_SINCE_TRANSPLANT = 90;
	
	private String firstName;
	private String lastName;
	private int id;
	
	// The containers for the patient's raw data:
	private ArrayList<LocalDate> transplantDate;
	private ArrayList<LocalDate> testResultsDate;
	private ArrayList<Integer> numOfTransplant;
	private ArrayList<String> typeOfTest;
	private ArrayList<Double> testResults;
	
	// The matrix for the patients intervals of data:
	private ArrayList<ArrayList<LocalDate>> transplantDateMatrix;
	private ArrayList<ArrayList<LocalDate>> testResultsDateMatrix;
	private ArrayList<ArrayList<Integer>> numOfTransplantMatrix;
	private ArrayList<ArrayList<String>> typeOfTestMatrix;
	private ArrayList<ArrayList<Double>> testResultsMatrix;
	
	
	
	// Constructor:
	//---------------------------------------------------------
	public Patient(String firstName, String lastName, int id) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		//this.firstTestDateAfterTheTransplant = null;
		//this.firstTestValueAfterTheTransplant = 0.0;
		this.transplantDate = new ArrayList<LocalDate>();
		this.testResultsDate = new ArrayList<LocalDate>();
		this.numOfTransplant = new  ArrayList<Integer>();
		this.typeOfTest = new ArrayList<String>();
		this.testResults = new ArrayList<Double>();
		
		this.transplantDateMatrix = new ArrayList<ArrayList<LocalDate>>();
		this.testResultsDateMatrix = new ArrayList<ArrayList<LocalDate>>();
		this.numOfTransplantMatrix = new ArrayList<ArrayList<Integer>>();
		this.typeOfTestMatrix = new ArrayList<ArrayList<String>>();
		this.testResultsMatrix = new ArrayList<ArrayList<Double>>();
	}
	
	// Getters:
	//---------------------------------------------------------
	public ArrayList<LocalDate> getTransplantDate() {
		return transplantDate;
	}

	public ArrayList<LocalDate> getTestResultsDate() {
		return testResultsDate;
	}

	public ArrayList<Integer> getNumOfTransplant() {
		return numOfTransplant;
	}

	public ArrayList<String> getTypeOfTest() {
		return typeOfTest;
	}

	public ArrayList<Double> getTestResults() {
		return testResults;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public ArrayList<ArrayList<Double>> getTestResultsMatrix() {
		return testResultsMatrix;
	}
	
	public ArrayList<ArrayList<LocalDate>> getTestResultsDateMatrix() {
		return testResultsDateMatrix;
	}
	
	public ArrayList<ArrayList<String>> getTypeOfTestMatrix() {
		return typeOfTestMatrix;
	}
	
	public ArrayList<ArrayList<LocalDate>> getTransplantDateMatrix() {
		return transplantDateMatrix;
	}
	
	
	
	// Setters:
	//---------------------------------------------------------

	public void setTransplantDateMatrix(ArrayList<ArrayList<LocalDate>> transplantDateMatrix) {
		this.transplantDateMatrix = transplantDateMatrix;
	}

	public void setTestResultsDateMatrix(ArrayList<ArrayList<LocalDate>> testResultsDateMatrix) {
		this.testResultsDateMatrix = testResultsDateMatrix;
	}

	public ArrayList<ArrayList<Integer>> getNumOfTransplantMatrix() {
		return numOfTransplantMatrix;
	}

	public void setNumOfTransplantMatrix(ArrayList<ArrayList<Integer>> numOfTransplantMatrix) {
		this.numOfTransplantMatrix = numOfTransplantMatrix;
	}

	public void setTypeOfTestMatrix(ArrayList<ArrayList<String>> typeOfTestMatrix) {
		this.typeOfTestMatrix = typeOfTestMatrix;
	}

	public void setTestResultsMatrix(ArrayList<ArrayList<Double>> testResultsMatrix) {
		this.testResultsMatrix = testResultsMatrix;
	}

	// Update the data lists:
	//-----------------------------------------------------------
	public void addTransplantDate(LocalDate date)
	{
		this.transplantDate.add(date);
	}
	
	public void addTestresultDate(LocalDate date)
	{
		this.testResultsDate.add(date);
	}
	
	public void addNumberOfTransplant(int number)
	{
		this.numOfTransplant.add(new Integer(number));
	}
	
	public void addTypeOftest(String testType)
	{
		this.typeOfTest.add(testType);
	}
	
	public void addTestResults(double result)
	{
		this.testResults.add(new Double(result));
	}
	
	public String toString()
	{
		String patientAsString ="\nLast name: " + this.lastName + 
								"\nFirst name: " + this.firstName + 
								"\nID number: " + this.id + 
								"\n\tTransplant date Array: " + this.transplantDate.toString() + 
								"\n\tTest results date Array:" + this.testResultsDate.toString() + 
								"\n\tNumber of transplant Array: " + this.numOfTransplant.toString() + 
								"\n\tType of test Array:" + this.typeOfTest.toString() + 
								"\n\tTest results Array: " + this.testResults.toString();
		
		return patientAsString;
		
	}

	public int getId() {
		return id;
	}

	public void addTestsDataFromOtherPatient(Patient other, int recordIndextoAdd, boolean append) {
		if(append)
		{
			this.numOfTransplant.add(other.getNumOfTransplant().get(recordIndextoAdd));
			this.testResults.add(other.getTestResults().get(recordIndextoAdd));
			this.testResultsDate.add(other.getTestResultsDate().get(recordIndextoAdd));
			this.transplantDate.add(other.getTransplantDate().get(recordIndextoAdd));
			this.typeOfTest.add(other.getTypeOfTest().get(recordIndextoAdd));
		}
		else
		{
			this.numOfTransplant.add(0, other.getNumOfTransplant().get(recordIndextoAdd));
			this.testResults.add(0,other.getTestResults().get(recordIndextoAdd));
			this.testResultsDate.add(0,other.getTestResultsDate().get(recordIndextoAdd));
			this.transplantDate.add(0,other.getTransplantDate().get(recordIndextoAdd));
			this.typeOfTest.add(0,other.getTypeOfTest().get(recordIndextoAdd));
			
		}
	}

	public double[] calcTWA() {
			
		int numOfcolumnsForThepatient =  this.getTestResultsMatrix().size();
		
		double[] results = new double[numOfcolumnsForThepatient];
		
		for (int i = 0 ; i < numOfcolumnsForThepatient ; i++)
		{
			double TWAi = calcTWAForInterval(this.getTestResultsDateMatrix().get(i), this.getTestResultsMatrix().get(i));
			results[i] = TWAi;		
		}
	    return results;
	}

	private double calcTWAForInterval(ArrayList<LocalDate> testResultsDate, ArrayList<Double>testResults) {
		
		// 1. Alert if test dates is unsorted:	
		double result = 0.0;
		for(int k = 0 ; k < testResultsDate.size() - 1 ; k ++ )
		{
			if (testResultsDate.get(k+1).isBefore(testResultsDate.get(k)))
			{
				System.out.println("Problem with patient with ID: " + id + " tests dates are out of order: see " + k + " and " + (k+1));
				return  -1.0;
			}
		}
		
		int days = 0;
		 
		for (int i = 0 ; i < testResultsDate.size() - 1 ; i ++)
		{
			double tempDays = ChronoUnit.DAYS.between(testResultsDate.get(i+1),testResultsDate.get(i));
			days += tempDays;
			
			// get Average results value:
			double tempAverageResult = (testResults.get(i+1) + testResults.get(i)) / 2;
			result += tempDays * tempAverageResult ;
		}
		
		return result/days;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void updateInerval(ArrayList<LocalDate> tempTransDateMat, ArrayList<LocalDate> tempTestResDateMat,
			ArrayList<Integer> tempNumOfTransMat, ArrayList<Double> tempTestResMat, ArrayList<String> tempTypeOfTestMat,
			int interval) {
		this.transplantDateMatrix.add(interval,tempTransDateMat);
		this.testResultsDateMatrix.add(interval,tempTestResDateMat);
		this.numOfTransplantMatrix.add(interval,tempNumOfTransMat);
		this.testResultsMatrix.add(interval,tempTestResMat);
		this.typeOfTestMatrix.add(interval,tempTypeOfTestMat);
	}
	
	
	
}
