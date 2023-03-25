package simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import common.PacketHeaders;
import common.PacketUtils;

public class SimulationGUI {

	private JFrame frame;

	public SimulationGUI() {
		frame = new JFrame();

		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setMaximum(10);
		model.setMinimum(1);
		model.setValue(1);
		JLabel l = new JLabel("elevator to message:");
		JSpinner elevatorSpinner = new JSpinner(model);
		l.setLabelFor(elevatorSpinner);
		
		JButton doorFault = new JButton("Send door fault");
		doorFault.setBounds(230, 100, 200, 40);
		doorFault.setActionCommand("doorFault");
		doorFault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendDoorFault(model.getNumber().intValue());  // passing in elevatornum
			}
		});
		doorFault.setEnabled(false);

		JButton slowFault = new JButton("Send slow fault");
		slowFault.setBounds(430, 100, 200, 40);
		slowFault.setActionCommand("slowFault");
		slowFault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendSlowFault(model.getNumber().intValue());
			}
		});
		slowFault.setEnabled(false);

		JButton startButton = new JButton("start simulation");
		startButton.setBounds(130, 100, 100, 40);
		startButton.setActionCommand("start");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				startButton.setEnabled(false);
				doorFault.setEnabled(true);
				slowFault.setEnabled(true);
				System.out.println("Starting simulation from gui.");

				Thread simulationThread = new Thread(new SimulationThread(new String[] { "--realtime" }));
				simulationThread.start();
			}
		});
		frame.add(startButton);
		frame.add(doorFault);
		frame.add(slowFault);
		frame.add(l);
		frame.add(elevatorSpinner);
		l.setBounds(250, 150, 150, 30);
		elevatorSpinner.setBounds(400, 150, 50, 30);
	}

	public void openScreen() {
		frame.setSize(800, 300);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Fault used for someone pushing at the door. (first 2 bytes: {9,1})
	 * Send fault to FloorSubsystem -> Scheduler -> ElevatorSubsystem
	 */
	private void sendDoorFault(int elevatorNum) {
		byte[] fault = PacketHeaders.DoorFault.getHeaderBytes();
		sendFaultToElevatorListener(fault, elevatorNum);
	}

	/**
	 *  Fault used for trying to leave and exit the system. (first 2 bytes: {9,2})
	 *  Elevator slows and floor timer fault will shut it down.
	 * Send fault to FloorSubsystem -> Scheduler -> ElevatorSubsystem
	 */
	private void sendSlowFault(int elevatorNum) {
		byte[] fault = PacketHeaders.SlowFault.getHeaderBytes();
		sendFaultToElevatorListener(fault, elevatorNum);
	}
	
	/**
	 * Method used to send faults directly to ElevatorFault Listener
	 * @param requestByte byte[],the request to store into a packet and send
	 */
	public synchronized void sendFaultToElevatorListener(byte[] requestByte, int carNum) {
		System.out.println("CAR NUMBER : " + carNum);
		int elavatorPortNum = PacketUtils.ELEVATOR_PORT + carNum;
		
		try (DatagramSocket sendSocket = new DatagramSocket()){
			DatagramPacket sendPacket = new DatagramPacket(requestByte, requestByte.length,InetAddress.getLocalHost(), elavatorPortNum);
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
