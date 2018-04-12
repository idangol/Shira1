import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ShiraMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("At least two argument expected");
			return;
		}

		String inputfileName = args[0];
		String outputFileName = args[1];
		
		HSSFWorkbook InputWorkBook = HSSFReadWrite.readFile(inputfileName);
		PatientsDB db = new PatientsDB();
		PatientsDataParser parser = new PatientsDataParser(InputWorkBook, db);
		parser.parseExcel();
		
		DBFixer dbFixer = new DBFixer(db);
		dbFixer.fixDB();
		
		TWAcalculator twAcalculator = new TWAcalculator(dbFixer.getCleanDB());
		XSSFWorkbook resultWorkbook = new XSSFWorkbook();
		twAcalculator.calcTWA(resultWorkbook);
		twAcalculator.writeToFile(resultWorkbook, outputFileName);
		
		
	}	
		
	

}
