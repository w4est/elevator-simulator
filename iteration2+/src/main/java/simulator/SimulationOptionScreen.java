package simulator;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SimulationOptionScreen {

	private JFrame frame;

	public SimulationOptionScreen() {
		frame = new JFrame();
		JButton startButton = new JButton("start simulation");
		startButton.setBounds(130,100,100,40);
		frame.add(startButton);
	}

	public void openScreen() {
		frame.setSize(640,480);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
