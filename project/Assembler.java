package project;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public interface Assembler {
	Set<String> noArgument = new TreeSet<>(Arrays.asList("HALT", "NOP", "NOT"));
	
	/**
	 * Method to assemble a file to its executable representation. 
	 * If the input has errors one or more of the errors will be reported 
	 * the StringBulder. The errors may not be the first error in 
	 * the code and will depend on the order in which instructions 
	 * are checked. There is no attempt to report all the errors.
	 * The line number of the last error that is reported 
	 * is returned as the value of the method. 
	 * A return value of 0 indicates that the code had no errors 
	 * and an output file was produced and saved. If the input or 
	 * output cannot be opened, the return value is -1.
	 * The unchecked exception IllegalArgumentException is thrown 
	 * if the error parameter is null, since it would not be 
	 * possible to provide error information about the source code.
	 * @param inputFileName the source assembly language file name
	 * @param outputFileName the file name of the executable version  
	 * of the program if the source program is correctly formatted
	 * @param error the StringBuilder to store the description 
	 * of the error or errors reported. It will be empty (length 
	 * zero) if no error is found.
	 * @return 0 if the source code is correct and the executable 
	 * is saved, -1 if the input or output files cannot be opened, 
	 * otherwise the line number of a reported error.
	 */
	int assemble(String inputFileName, String outputFileName, StringBuilder error);
	
	class DataPair {
		protected int address;
		protected int value;
		
		public DataPair(int address, int value) {
			this.address = address;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "DataPair (" + address + ", " + value + ")";
		}
	}
}
