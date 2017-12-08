package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullAssembler implements Assembler {
	private boolean readingCode = true;
	private int noArgCount = 0;

	private Instruction makeCode(String[] parts) {
		if(noArgument.contains(parts[0])) {
			noArgCount += 1;
			int opPart = 8 * Instruction.opcodes.get(parts[0]);
			opPart += Instruction.numOnes(opPart)%2;
			return new Instruction((byte)opPart, 0);
		}

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

		int arg = Integer.parseInt(parts[1], 16);
		int opPart = 8 * Instruction.opcodes.get(parts[0]) + flags;
		opPart += Instruction.numOnes(opPart)%2;
		return new Instruction((byte)opPart, arg);
	}

	private DataPair makeData(String[] parts) {
		return new DataPair(Integer.parseInt(parts[0], 16), Integer.parseInt(parts[1], 16));
	}

	@SuppressWarnings("resource")
	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		Map<Boolean, List<String>> lists = null;
		try (Stream<String> lines = Files.lines(Paths.get(inputFileName))) {
			lists = lines
					.filter(line -> line.trim().length() > 0)
					.map(line -> line.trim())
					.peek(line -> {if(line.toUpperCase().equals("DATA")) readingCode = false;})
					.map(line -> line.trim())
					.collect(Collectors.partitioningBy(line -> readingCode));
			//				System.out.println("true List " + lists.get(true)); // these lines can be uncommented 
			//				System.out.println("false List " + lists.get(false)); // for checking the code
		} catch (FileNotFoundException e) {
			error.append("\nUnable to open the source file");
			return -1;
		} catch (IOException e) {
			error.append("\nUnexplained IO Exception");
			return -1;
		} 

		try(Scanner sc = new Scanner((Readable) lists.values())) {
			ArrayList<String> inText = new ArrayList<>();
			while(sc.hasNextLine()) inText.add(sc.nextLine());

			boolean dataSeparator = false;
			for(int i = 0; i < inText.size(); i++) {
				String line = inText.get(i);
				if(line.trim().length() == 0 && i + 1 < inText.size() && inText.get(i+1).trim().length() > 0) {
					error.append("\nError: line "+(i+1)+" is a blank line");
				}
				if(!line.trim().isEmpty() && (line.charAt(0) == ' ' || line.charAt(0) == '\t')) {
					error.append("\nError: line "+(i+1)+" starts with white space");
				}
				if(line.trim().toUpperCase().equals("DATA")) {
					if(!line.trim().equals("DATA")) {
						error.append("\nError: line "+(i+1)+" has a badly formatted data separator");
					} else {
						if(dataSeparator) {
							error.append("\nError: line "+(i+1)+" has a duplicate data separator");
						}
					}
					dataSeparator = true;
					lists.get(false).remove("DATA");
					continue;
				}
				if(!dataSeparator) {
					String[] parts = line.trim().split("\\s+");
					if(Instruction.opcodes.keySet().contains(parts[0])) {
						int n = Instruction.opcodes.get(parts[0]);
						if(Instruction.mnemonics.get(n).toUpperCase().equals(parts[0])) {
							if(!Instruction.mnemonics.get(n).equals(parts[0])) {
								error.append("\nError: line "+(i+1)+" has a badly formatted instruction");
							} else {
								if(noArgument.contains(parts[0])) {
									if(!(parts.length == 1)) {
										error.append("\nError on line " + (i+1) + ": this mnemonic cannot take arguments");
									}
								} else {
									if(parts.length < 2) {
										error.append("\nError on line " + (i+1) + ": this mnemonic is missing an argument");
									} else if(parts.length > 2) {
										error.append("\nError on line " + (i+1) + ": this mnemonic has too many arguments");
									}
									try{
										int flags = 0;
										String s = parts[1];
										if(parts[1].charAt(0) == '#') {
											flags = 2;
											s = s.substring(1);
										} else if(parts[1].charAt(0) == '@') {
											flags = 4;
											s = s.substring(1);
										} else if(parts[1].charAt(0) == '&') {
											flags = 6;
											s = s.substring(1);
										}
										int arg = Integer.parseInt(s,16);
										int opPart = 8 * Instruction.opcodes.get(parts[0]) + flags;
										opPart += Instruction.numOnes(opPart)%2;
									} catch(NumberFormatException e) {
										error.append("\nError on line " + (i+1) + ": argument is not a hex number");
									}
								}
							}
						}
					}
				} else {
					String[] parts = line.trim().split("\\s+");
					if(parts.length == 2) {
						try {
							int address = Integer.parseInt(parts[0], 16);
						} catch(NumberFormatException e) {
							error.append("\nError on line " + (i+1) + ": data has non-numeric memory address");
						}
						int value = Integer.parseInt(parts[1], 16);
					} else if(parts.length < 2) {
						error.append("\nError on line " + (i+1) + ": this mnemonic is missing an argument");
					}
				}
			}
		}

		List<Instruction> outputCode = lists.get(true).stream()
				.map(line -> line.split("\\s+"))
				.map(this::makeCode) // note how we use an instance method
				.collect(Collectors.toList());

		List<DataPair> outputData = lists.get(false).stream()
				.map(line -> line.split("\\s+"))
				.map(this::makeData)
				.collect(Collectors.toList());

		int bytesNeeded = noArgCount + 5*(outputCode.size() - noArgCount) + 1 + 8*(outputData.size());
		ByteBuffer buff = ByteBuffer.allocate(bytesNeeded);

		outputCode.stream()
		.forEach(instr -> {
			buff.put(instr.opcode);
			if(!Instruction.noArgument(instr)) buff.putInt(instr.arg);
		});

		buff.put((byte) - 1);

		outputData.stream()
		.forEach(pair -> {
			buff.putInt(pair.address);
			buff.putInt(pair.value);
		});

		buff.rewind();

		boolean append = false;
		try(FileChannel wChannel = new FileOutputStream(new File(outputFileName), append).getChannel()) {
			wChannel.write(buff);
			wChannel.close();
		} catch (FileNotFoundException e) {
			error.append("\nError: Unable to write the assembled program to the output file");
			return -1;
		} catch (IOException e) {
			error.append("\nUnexplained IO Exception");
			return -1;
		}

		return 0;
	}

}