package simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import common.PacketHeaders;
import common.PacketUtils;

public class SimulationGUI {

	private JFrame frame;
	private JPanel centerPanel;

	/**
	 * Constructor sets up the GUI for the Elevator Simulator
	 * @param floors int, the number of floors in the building
	 * @param elevators int, the number of elevators
	 */
	public SimulationGUI(int floors, int elevators) {
		this.frame = new JFrame("Group 2 Elevator Simulator");
		this.frame.setLayout(new BorderLayout());

		SpinnerNumberModel model = new SpinnerNumberModel();
		model.setMaximum(10);
		model.setMinimum(1);
		model.setValue(1);
		JLabel l = new JLabel("elevator to message:");
		JSpinner elevatorSpinner = new JSpinner(model);
		l.setLabelFor(elevatorSpinner);
		l.setBounds(250, 150, 150, 30);
		elevatorSpinner.setBounds(400, 150, 50, 30);
		
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
		//North Panel
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Color.white);
		northPanel.setPreferredSize(new Dimension(100,20));
		JLabel title = new JLabel("ELEVATOR SIMULATOR");
		northPanel.add(title);
		this.frame.add(northPanel, BorderLayout.NORTH);
		
		//Center Panel
		this.centerPanel = new JPanel();
		this.centerPanel.setBackground(Color.black);
		this.centerPanel.setPreferredSize(new Dimension(100,100));
		for (int i=0; i<elevators; i++) {
			visualFloorSetup(floors);
		}
		this.frame.add(centerPanel, BorderLayout.CENTER);
		
		//South Panel
		JPanel southPanel = new JPanel();
		southPanel.setBackground(Color.white);
		southPanel.setPreferredSize(new Dimension(100,60));
		this.frame.add(southPanel, BorderLayout.SOUTH);
		
		
		southPanel.add(startButton);
		southPanel.add(doorFault);
		southPanel.add(slowFault);
		southPanel.add(l);
		southPanel.add(elevatorSpinner);
		
	}

	public void openScreen() {
		frame.setSize(500, 700);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Method used in constructor's center panel to set up the floors per elevator
	 */
	private void visualFloorSetup(int floors) {
		//every floor will be a JPanel & the jpanel with green border represents elevator position
		
		//sub panel elevatorPanel within centerPanel
		JPanel elevatorPanel = new JPanel();
		elevatorPanel.setBackground(Color.blue);
		elevatorPanel.setLayout(new BorderLayout());
		
		// elevator direction light (up or down)
		
		JPanel upOrDown = new JPanel();
		upOrDown.setBackground(Color.orange);
		JLabel directionLabel = new JLabel("UP LIGHT");
		upOrDown.add(directionLabel);
		elevatorPanel.add(upOrDown, BorderLayout.NORTH);		
		
		// floors
		JPanel oneFloorPanel = new JPanel();
		oneFloorPanel.setLayout(new BoxLayout(oneFloorPanel, BoxLayout.PAGE_AXIS)); // top to bottom
		for (int i = floors; i>1; i--) {
			JPanel oneFloor = new JPanel();
			oneFloor.setBorder(BorderFactory.createLineBorder(Color.gray));
			oneFloor.setPreferredSize(new Dimension(20,22));
			JLabel floorLabel = new JLabel("Floor "+i);
			oneFloor.add(floorLabel);
			oneFloorPanel.add(oneFloor);
		}
		//first floor is green by default (elevator starts there)
		JPanel firstFloor = new JPanel();
		firstFloor.setBorder(BorderFactory.createLineBorder(Color.green, 2));
		firstFloor.setPreferredSize(new Dimension(20,22));
		JLabel floorLabel1 = new JLabel("Floor 1");
		firstFloor.add(floorLabel1);
		oneFloorPanel.add(firstFloor);
		
		elevatorPanel.add(oneFloorPanel, BorderLayout.CENTER);
		
		// elevator state (open or closed)
		JPanel doors = new JPanel();
		doors.setBackground(Color.gray);
		JLabel stateTitle = new JLabel("STATE");
		doors.add(stateTitle);
		elevatorPanel.add(doors, BorderLayout.SOUTH);
		
		this.centerPanel.add(elevatorPanel);
	}
	
	/**
	 * Fault used for someone pushing at the door. (first 2 bytes: {9,1})
	 * Send fault to ElevatorFaultListener -> ElevatorSubsystem
	 * @param elevatorNum int, the elevator car number selected
	 */
	private void sendDoorFault(int elevatorNum) {
		byte[] fault = PacketHeaders.DoorFault.getHeaderBytes();
		sendFaultToElevatorListener(fault, elevatorNum);
	}
	
	/**
	 * Fault used for trying to leave and exit the system. (first 2 bytes: {9,2})
	 * Elevator slows and will shut down.
	 * Send fault to ElevatorFaultListener -> ElevatorSubsystem
	 * @param elevatorNum int, the elevator car number selected
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
