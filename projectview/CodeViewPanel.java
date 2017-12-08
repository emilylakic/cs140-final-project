package projectview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import project.Instruction;
import project.Loader;
import project.MachineModel;
import project.Memory;

public class CodeViewPanel implements Observer {
	
	private MachineModel model;
	private Instruction instr;
	private JScrollPane scroller;
	private JTextField[] codeText = new JTextField[Memory.CODE_SIZE];
	private JTextField[] codeBinHex = new JTextField[Memory.CODE_SIZE];
	private int previousColor = -1;
	
	public CodeViewPanel(ViewMediator view, MachineModel m) { 
		model = m;
		view.addObserver((Observer) this);	
	}

	public JComponent createCodeDisplay() {
		JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Code Memory View",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        panel.setBorder(border);
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());
        JPanel numPanel = new JPanel();
        JPanel textPanel = new JPanel();
        JPanel hexPanel = new JPanel();
        numPanel.setLayout(new GridLayout(0, 1));
        textPanel.setLayout(new GridLayout(0, 1));
        hexPanel.setLayout(new GridLayout(0, 1));
        innerPanel.add(numPanel, BorderLayout.LINE_START);
        innerPanel.add(textPanel, BorderLayout.CENTER);
        innerPanel.add(hexPanel, BorderLayout.LINE_END);
        for (int i = 0; i < Memory.CODE_SIZE; i++) {
            numPanel.add(new JLabel(i + ": ", JLabel.RIGHT));
            codeText[i] = new JTextField(10);
            codeBinHex[i] = new JTextField(12);
            textPanel.add(codeText[i]);
            hexPanel.add(codeBinHex[i]);
        }
        scroller = new JScrollPane(innerPanel);
        panel.add(scroller);
        return panel;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 != null && arg1.equals("Load Code")) {
			for(int i = 0; i <= model.getProgramSize(); i++) {
				instr = model.getCode(i);
				codeText[i].setText(instr.getText());
				codeBinHex[i].setText(instr.getBinHex());
			}	
			previousColor = model.getPC();			
			codeBinHex[previousColor].setBackground(Color.YELLOW);
			codeText[previousColor].setBackground(Color.YELLOW);
		} else if(arg1 != null && arg1 instanceof String 
				&& ((String)arg1).equals("Clear")) {
			for(int i = 0; i <= model.getProgramSize(); i++) {
				codeText[i].setText("");
				codeBinHex[i].setText("");
			}	
			if(previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
				codeText[previousColor].setBackground(Color.WHITE);
				codeBinHex[previousColor].setBackground(Color.WHITE);
			}
			previousColor = -1;
		}		
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.WHITE);
			codeBinHex[previousColor].setBackground(Color.WHITE);
		}
		previousColor = model.getPC();
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.YELLOW);
			codeBinHex[previousColor].setBackground(Color.YELLOW);
		} 
		if(scroller != null && instr != null && model!= null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			int pc = model.getPC();
			if(pc >= 0 && pc < Memory.CODE_SIZE && codeBinHex[pc] != null) { 
				Rectangle bounds = codeBinHex[pc].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			}
		}
	}
	
	public static void main(String[] args) {
		ViewMediator mediator = new ViewMediator(); 
		MachineModel model = new MachineModel();
		CodeViewPanel panel = new CodeViewPanel(mediator, model);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createCodeDisplay());
		frame.setVisible(true);
		System.out.println(Loader.load(model, new File("factorial.pexe")));
		panel.update(mediator, "Load Code");
	}
}