import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Patient {
	
	//private final int MAGIC_NUM_OF_DAYS_SINCE_TRANSPLANT = 90;
	
	private String firstName;
	private String lastName;
	private int id;
	private LocalDate firstTestDateAfterTheTransplant;
	private double firstTestValueAfterTheTransplant;
	private ArrayList<LocalDate> transplantDate;
	private ArrayList<LocalDate> testResultsDate;
	private ArrayList<Integer> numOfTransplant;
	private ArrayList<String> typeOfTest;
	private ArrayList<Double> testResults;
	
	// Constructor:
	//---------------------------------------------------------
	public Patient(String firstName, String lastName, int id) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.firstTestDateAfterTheTransplant = null;
		this.firstTestValueAfterTheTransplant = 0.0;
		this.transplantDate = new ArrayList<LocalDate>();
		this.testResultsDate = new ArrayList<LocalDate>();
		this.numOfTransplant = new  ArrayList<Integer>();
		this.typeOfTest = new ArrayList<String>();
		this.testResults = new ArrayList<Double>();
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
	
	public LocalDate getFirstTestDateAfterTheTransplant() {
		return firstTestDateAfterTheTransplant;
	}

	public double getFirstTestValueAfterTheTransplant() {
		return firstTestValueAfterTheTransplant;
	}
	
	// Setters:
	//---------------------------------------------------------

	public void setFirstTestDateAfterTheTransplant(LocalDate firstTestDateAfterTheTransplant) {
		this.firstTestDateAfterTheTransplant = firstTestDateAfterTheTransplant;
	}

	public void setFirstTestValueAfterTheTransplant(double firstTestValueAfterTheTransplant) {
		this.firstTestValueAfterTheTransplant = firstTestValueAfterTheTransplant;
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

	public void addTestsDataFromOtherPatient(Patient other, int recordIndextoAdd) {
		this.numOfTransplant.add(other.getNumOfTransplant().get(recordIndextoAdd));
		this.testResults.add(other.getTestResults().get(recordIndextoAdd));
		this.testResultsDate.add(other.getTestResultsDate().get(recordIndextoAdd));
		this.transplantDate.add(other.getTransplantDate().get(recordIndextoAdd));
		this.typeOfTest.add(other.getTypeOfTest().get(recordIndextoAdd));
	}

	public double calcTWA() {
		
		double result = 0.0;
		int days = 0;
		// 1. Alert if test dates is unsorted:
		for(int i = 0 ; i < testResultsDate.size() - 1 ; i ++ )
		{
			if (testResultsDate.get(i+1).isBefore(testResultsDate.get(i)))
			{
				System.out.println("Problem with patient with ID: " + id + " tests dates are out of order: see " + i + " and " + (i+1));
				return  Double.MIN_VALUE;
			}
		}
		
//		// Calc first pair:
//		days += ChronoUnit.DAYS.between(testResultsDate.get(0),transplantDate.get(0).plusDays(MAGIC_NUM_OF_DAYS_SINCE_TRANSPLANT));
//		result  += days * testResults.get(0);
		
		// 
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
	
	
}
