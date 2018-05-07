import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Logger {
	
	private String path;
	private BufferedWriter bw;
	private int patientID;
	
	
	// Constructor:
	//----------------------------
	public Logger(String path) {
		super();
		this.patientID = 0;
		this.path = path;
		try {
			bw = new BufferedWriter(new FileWriter(new File(path)));
			bw.write("\nLogger: created at " + LocalDate.now().toString());
		} catch (IOException e) {
			System.out.println("Failed to create logger");
		}
	}
	
	
	
	public int getPatientID() {
		return patientID;
	}

	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}



	public void logData(String header, String data)
	{
		try {
			bw.append("\n\t" + header +  data);
		} catch (IOException e) {
			System.out.println("Failed to write data: " + data);
		}
	}



	public void flush_n_close() throws IOException {
		if (bw != null)
			{bw.close();}
	}
	
}
