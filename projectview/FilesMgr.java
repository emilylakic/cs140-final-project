package projectview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.Assembler;
import project.Loader;
import project.MachineModel;
import project.SimpleAssembler;

public class FilesMgr {
	private ViewMediator mediator;
	private MachineModel model;
	private String defaultDir; 
	private String sourceDir; 
	private String executableDir; 
	private Properties properties = null;
	private File currentlyExecutingFile;
	private Assembler assembler = new SimpleAssembler(); 

	public FilesMgr(ViewMediator view) {
		mediator = view;
		model = mediator.getModel();
	}

	public void initialize() {
		locateDefaultDirectory();
		loadPropertiesFile();
	}

	private void locateDefaultDirectory() {
		//CODE TO DISCOVER THE ECLIPSE DEFAULT DIRECTORY
		//WHICH WILL STORE THE propertyfile
		File temp = new File("propertyfile.txt");
		if(!temp.exists()) {
			PrintWriter out;
			try {
				out = new PrintWriter(temp);
				out.close();
				defaultDir = temp.getAbsolutePath();
				temp.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			defaultDir = temp.getAbsolutePath();
		}
		// change to forward slashes
		defaultDir = defaultDir.replace('\\','/');
		int lastSlash = defaultDir.lastIndexOf('/');
		defaultDir  = defaultDir.substring(0, lastSlash + 1);
	}

	void loadPropertiesFile() {
		try { // load properties file "propertyfile.txt", if it exists
			properties = new Properties();
			properties.load(new FileInputStream("propertyfile.txt"));
			sourceDir = properties.getProperty("SourceDirectory");
			executableDir = properties.getProperty("ExecutableDirectory");
			// CLEAN UP ANY ERRORS IN WHAT IS STORED:
			if (sourceDir == null || sourceDir.length() == 0 
					|| !new File(sourceDir).exists()) {
				sourceDir = defaultDir;
			}
			if (executableDir == null || executableDir.length() == 0 
					|| !new File(executableDir).exists()) {
				executableDir = defaultDir;
			}
		} catch (Exception e) {
			// PROPERTIES FILE DID NOT EXIST
			sourceDir = defaultDir;
			executableDir = defaultDir;
		}		
	}

	/**
	 * Translate method reads a source "pasm" file and saves the
	 * file with the extension "pexe" by collecting the input and output
	 * files and calling Assembler.assemble. If the source has errors 
	 * the error messages will be reported in a JOptionPane.  
	 */
	public void assembleFile() {
		File source = null;
		File outputExe = null;
		JFileChooser chooser = new JFileChooser(sourceDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Source Files", "pasm");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);
		if(openOK == JFileChooser.APPROVE_OPTION) {
			source = chooser.getSelectedFile();
		}
		if(source != null && source.exists()) {
			// CODE TO IDENTIFY WHICH DIRECTORY HAS THE pasm FILES
			sourceDir = source.getAbsolutePath();
			sourceDir = sourceDir.replace('\\','/');
			int lastDot = sourceDir.lastIndexOf('.');
			String outName = sourceDir.substring(0, lastDot + 1) + "pexe";			
			int lastSlash = sourceDir.lastIndexOf('/');
			sourceDir = sourceDir.substring(0, lastSlash + 1);
			outName = outName.substring(lastSlash+1); 
			filter = new FileNameExtensionFilter(
					"Pippin Executable Files", "pexe");
			if(executableDir.equals(defaultDir)) {
				chooser = new JFileChooser(sourceDir);
			} else {
				chooser = new JFileChooser(executableDir);
			}
			chooser.setFileFilter(filter);
			chooser.setSelectedFile(new File(outName));
			int saveOK = chooser.showSaveDialog(null);
			if(saveOK == JFileChooser.APPROVE_OPTION) {
				outputExe = chooser.getSelectedFile();
			}
			if(outputExe != null) {
				// CODE TO RESTORE WHICH DIRECTORIES HAVE THE pasm AND pexe FILES
				// WHICH WE WILL ALLOW TO BE DIFFERENT
				executableDir = outputExe.getAbsolutePath();
				executableDir = executableDir.replace('\\','/');
				lastSlash = executableDir.lastIndexOf('/');
				executableDir = executableDir.substring(0, lastSlash + 1);
				try { 
					// CODE TO SAVE WHICH DIRECTORIES HAVE THE pasm AND pexe FILES
					// WHICH WE WILL ALLOW TO BE DIFFERENT
					properties.setProperty("SourceDirectory", sourceDir);
					properties.setProperty("ExecutableDirectory", executableDir);
					properties.store(new FileOutputStream("propertyfile.txt"), 
							"File locations");
				} catch (Exception e) {
					System.out.println("Error writing properties file");
				}
				StringBuilder errors = new StringBuilder();
				int errorLine = assembler.assemble(source.getAbsolutePath(), outputExe.getAbsolutePath(), errors); 
				if (errorLine == 0){
					JOptionPane.showMessageDialog(
							mediator.getFrame(), 
							"The source was assembled to an executable",
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(
							mediator.getFrame(), 
							errors.toString(),
							"Source code error ",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {// outputExe Still null
				JOptionPane.showMessageDialog(
						mediator.getFrame(), 
						"The output file has problems.\n" +
								"Cannot assemble the program",
								"Warning",
								JOptionPane.OK_OPTION);
			}
		} else {// source file does not exist
			JOptionPane.showMessageDialog(
					mediator.getFrame(), 
					"The source file has problems.\n" +
							"Cannot assemble the program",
							"Warning",
							JOptionPane.OK_OPTION);				
		}
	}

	public void loadFile() {
		JFileChooser chooser = new JFileChooser(executableDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Executable Files", "pexe");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);
		
		if(openOK == JFileChooser.APPROVE_OPTION) {
			currentlyExecutingFile = chooser.getSelectedFile();
		}
		if(openOK == JFileChooser.CANCEL_OPTION) {
			currentlyExecutingFile = null;
		}

		if(currentlyExecutingFile != null && currentlyExecutingFile.exists()) {
			executableDir = currentlyExecutingFile.getAbsolutePath();
			executableDir = executableDir.replace('\\','/');
			int lastSlash = executableDir.lastIndexOf('/');
			executableDir = executableDir.substring(0, lastSlash + 1);
			try { 
				properties.setProperty("SourceDirectory", sourceDir);
				properties.setProperty("ExecutableDirectory", executableDir);
				properties.store(new FileOutputStream("propertyfile.txt"), 
						"File locations");
			} catch (Exception e) {
				System.out.println("Error writing properties file");
			}			
		}
		if(currentlyExecutingFile != null) 	finalLoad_ReloadStep();
		else 
			JOptionPane.showMessageDialog(
					mediator.getFrame(),  
					"No file selected.\n" +
							"Cannot load the program",
							"Warning",
							JOptionPane.OK_OPTION);
	}

	void finalLoad_ReloadStep() {
		mediator.clear();
		String str = Loader.load(model, currentlyExecutingFile);
		try {
			mediator.makeReady("Load Code");
		} catch (NumberFormatException e ) {
			JOptionPane.showMessageDialog(
					mediator.getFrame(),  
					"The file being selected has problems.\n" +
							str + "\n" +
							"Cannot load the program",
							"Warning",
							JOptionPane.OK_OPTION);
		}
	}
}
