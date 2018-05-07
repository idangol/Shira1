import java.io.IOException;

import javax.naming.PartialResultException;
import javax.swing.JOptionPane;

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

			
		HSSFWorkbook InputWorkBook = HSSFReadWrite.readFile(inputfileName);
		PatientsDB db = new PatientsDB();
		Logger log = new Logger (loggerFileName);
		
		PatientsDataParser parser = new PatientsDataParser(InputWorkBook, db, log);
		parser.parseExcel();
		
		DBFixer dbFixer = new DBFixer(db, log);
		dbFixer.fixDB();
		
		TWAcalculator twAcalculator = new TWAcalculator(dbFixer.getCleanDB(), log);
		
		XSSFWorkbook resultWorkbook = new XSSFWorkbook();
		twAcalculator.calcTWA(resultWorkbook);
		twAcalculator.writeToFile(resultWorkbook, outputFileName);
		
		log.flush_n_close();
		
		
	}	
		
	

}
