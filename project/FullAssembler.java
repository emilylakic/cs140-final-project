package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FullAssembler implements Assembler{
	private boolean readingCode = true;

	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		int retVal = 0;
		ArrayList<String> file = new ArrayList<>();
		if(error == null) throw new IllegalArgumentException("Coding error: the error buffer is null");
		
		try {
			BufferedReader test = new BufferedReader(new FileReader(inputFileName));
			String line;
			while((line = test.readLine()) != null) file.add(line);
			test.close();
		} catch(Exception e){
			error.append("Unable to open the source file\n");
			retVal = -1;
		}
		
		boolean dataSeen = false;
		int offset = file.size() - 1;
		
		for(int i = 0; i< file.size(); i++) {
			if(!dataSeen && file.get(i).trim().toUpperCase().equals("DATA")) {
				offset = i + 1;
				dataSeen = true;
			}
		}

		ArrayList<String> codeFile = new ArrayList<>();
		for(int i = 0; i < offset; i++) codeFile.add(file.get(i));

		int lineNum = -1;
		boolean isFirstBlank = false;
		
		for(int i = 0; i< codeFile.size(); i++) {
			String line = codeFile.get(i);
			if(line.trim().length() == 0 && isFirstBlank == false) {
				lineNum = i + 1;
				isFirstBlank = true;
			}
			
			String[] parts = line.trim().split("\\s+");
			if(lineNum != -1 && line.trim().length() != 0) {
				error.append("Error on line "+lineNum+": illegal blank line in the source file\n");
				retVal = lineNum;
				lineNum = -1;
			}

			char[] lineChar = line.toCharArray();
			if(line.trim().length() != 0) {
				if(lineChar[0] == ' '  || lineChar[0] == '\t') {
					error.append("Error on line " + (i+ 1) + ": line starts with illegal white space\n");
					retVal = i+1;
				} else if(line.trim().toUpperCase().equals("DATA") && readingCode == true) {
					if(!line.trim().equals("DATA")) {
						error.append("Error on line "+(i+1)+": line does not have DATA in upper case\n");
						retVal = i+1;
					}
				} else if(Instruction.opcodes.keySet().contains(parts[0].toUpperCase())) {
					if(!Instruction.opcodes.keySet().contains(parts[0])){
						error.append("Error on line " + (i+1) + ": mnemonic must be upper case\n");
						retVal = i+1;
					} else if(Assembler.noArgument.contains(parts[0])) {
						if(parts.length != 1) {
							error.append("Error on line " + (i+1) + ": this mnemonic cannot take arguments\n");
							retVal = i+1;
						}
					} else if(parts.length > 2) {
						error.append("Error on line " + (i+1) + ": this mnemonic has too many arguments\n");
						retVal = i+1;
					} else if(parts.length < 2) {
						error.append("Error on line " + (i+1) + ": this mnemonic is missing an argument\n");
						retVal = i+1;
					} else {
						try{
							int flags = 0;
							if(parts[1].charAt(0) == '#') {
								flags = 2;
								parts[1] = parts[1].substring(1);
							} else if(parts[1].charAt(0) == '@') {
								flags = 4;
								parts[1] = parts[1].substring(1);
							} else if(parts[1].charAt(0) == '&') {
								flags = 6;
								parts[1] = parts[1].substring(1);	
							}
							
							Integer.parseInt(parts[1],16);
							int opPart = 8*Instruction.opcodes.get(parts[0]) + flags;
							opPart += Instruction.numOnes(opPart) % 2;
						} catch(NumberFormatException e) {
							error.append("Error on line " + (i+1) + ": argument is not a hex number\n");
							retVal = i + 1;
						}
					}
				} else {
					error.append("Error on line " + (i+1) + ": illegal mnemonic\n");
					retVal = i+1;
				}
			}
		}

		ArrayList<String> dataFile = new ArrayList<>();
		for(int i = offset; i < file.size(); i++) dataFile.add(file.get(i));

		for(int i = 0; i < dataFile.size(); i++) {
			String line = dataFile.get(i);
			char[] lineChar = line.toCharArray();
			String[] parts = line.trim().split("\\s+");
			
			if(line.trim().length() == 0 && isFirstBlank == false) {
				lineNum = offset + i + 1;
				isFirstBlank = true;
			}
			
			if(lineNum != -1 && line.trim().length() != 0) {
				error.append("Error on line "+lineNum+": illegal blank line in the source file\n");
				retVal = lineNum;
				lineNum = -1;
			}
			
			if(line.trim().length() != 0) {
				if(lineChar[0] == ' '  || lineChar[0] == '\t') {
					error.append("Error on line "+lineNum+": line starts with illegal white space\n");
					retVal = offset + i+1;
				} else if(line.trim().toUpperCase().equals("DATA") && readingCode == true) {
					if(!line.trim().equals("DATA")) {
						error.append("Error on line "+lineNum+": line does not have DATA in upper case\n");
						retVal = i+1;
					}
					error.append("Error on line " + (offset + i + 1) + ": DATA should not occur twice\n");
				} else if(parts.length > 2) {
					error.append("Error on line " + (offset + i + 1) + ": the data is too long\n");
					retVal = offset + i + 1;
				} else if(parts.length < 2) {
					error.append("Error on line " + (offset + i + 1) + ": the data is too short\n");
					retVal = offset + i + 1;
				} else {
					try{
						Integer.parseInt(parts[0],16);
					} catch(NumberFormatException e) {
						error.append("Error on line " + (offset + i + 1) + ": data has non-numeric memory address\n");
						retVal =  i + 1;				
					}
					try {
						Integer.parseInt(parts[1],16);
					} catch(NumberFormatException e) {
						error.append("Error on line " + (offset + i + 1) + ": data has non-numeric memory value\n");
						retVal =  i + 1;				
					}
				}
			}
		}
		
		if(retVal == 0) new SimpleAssembler().assemble(inputFileName, outputFileName, error);

		return retVal;

	}

}
