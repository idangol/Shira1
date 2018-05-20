import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ShiraMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.err.println("At least two argument expected");
			return;
		}

		String inputfileName = args[0];
		String outputFileName = args[1];
		String loggerFileName = args[2];
		int deltaDaysForTWAs =Integer.parseInt(args[3]);
		int daysFromTransplant =Integer.parseInt(args[4]);
		
		

			
		HSSFWorkbook InputWorkBook = HSSFReadWrite.readFile(inputfileName);
		PatientsDB db = new PatientsDB();
		Logger log = new Logger (loggerFileName);
		
		PatientsDataParser parser = new PatientsDataParser(InputWorkBook, db, log);
		parser.parseExcel();
		
		DBFixer dbFixer = new DBFixer(db, log);
		dbFixer.fixDB(daysFromTransplant, deltaDaysForTWAs);
		
		TWAcalculator twAcalculator = new TWAcalculator(db, log,daysFromTransplant, deltaDaysForTWAs);
		XSSFWorkbook resultWorkbook = new XSSFWorkbook();
		twAcalculator.calcTWA(resultWorkbook);
		twAcalculator.writeToFile(resultWorkbook, outputFileName);
		
		log.flush_n_close();
		
		
	}	
		
	

}
