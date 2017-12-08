package projectview;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import project.MachineModel;

public class ProcessorViewPanel implements Observer {
	private MachineModel model;
	private JTextField acc = new JTextField(); 
	private JTextField pc = new JTextField();
	
	public ProcessorViewPanel(ViewMediator view, MachineModel m) {
		model = m;
		view.addObserver(this);
	}
	public JComponent createProcessorDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		panel.add(new JLabel("Accumulator: ", JLabel.RIGHT));
		panel.add(acc);
		panel.add(new JLabel("Program Counter: ", JLabel.RIGHT));
		panel.add(pc);
		return panel;
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		if(model != null) {
			acc.setText("" + model.getAccum());
			pc.setText("" + model.getPC());
		}
	}
	
	public static void main(String[] args) {
		ViewMediator mediator = new ViewMediator(); 
		MachineModel model = new MachineModel();
		ProcessorViewPanel panel = 
			new ProcessorViewPanel(mediator, model);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createProcessorDisplay());
		frame.setVisible(true);
	}
}