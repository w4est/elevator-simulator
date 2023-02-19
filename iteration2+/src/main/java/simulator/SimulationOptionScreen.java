package simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SimulationOptionScreen {

	private JFrame frame;
	private String defaultFileName = "src/test/resources/request_test.txt";
	private int defaultMaxFloors = 7;

	public SimulationOptionScreen() {
		frame = new JFrame();
		JButton startButton = new JButton("start simulation");
		startButton.setBounds(130,100,100,40);
		startButton.setActionCommand("start");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Iteration 3+ do we want selectable files?
				Simulation.runSimulation(defaultFileName, 7);
				startButton.setEnabled(false);
			}
		});
		
		
		frame.add(startButton);
	}

	public void openScreen() {
		frame.setSize(640,480);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
