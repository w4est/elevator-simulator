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
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import common.ElevatorStatusRequest;
import common.PacketHeaders;
import common.PacketUtils;

public class SimulationGUI {

	public static final String idleStateOpen = "IDLE Open";
	public static final String idleStateClosed = "IDLE Closed";
	public static final String movingUp = "Moving Up";
	public static final String movingDown = "Moving Down";
	public static final String broken = "Broken";
	
	private SimulationGUI selfReference;

	
	private JFrame frame;
	private JPanel centerPanel;
	
	private JButton startButton;
	private JButton doorFault;
	private JButton slowFault;
	
	private Map<Integer, Map<Integer, JPanel>> floorPanels = new HashMap<>();
	private Map<Integer, JPanel> stateLabels = new HashMap<>();

	/**
	 * Constructor sets up the GUI for the Elevator Simulator
	 * @param floors int, the number of floors in the building
	 * @param elevators int, the number of elevators
	 */
	public SimulationGUI(int floors, int elevators) {
		this.selfReference = this;
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
		
		doorFault = new JButton("Send door fault");
		doorFault.setBounds(230, 100, 200, 40);
		doorFault.setActionCommand("doorFault");
		doorFault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendDoorFault(model.getNumber().intValue());  // passing in elevatornum
			}
		});
		doorFault.setEnabled(false);

		slowFault = new JButton("Send slow fault");
		slowFault.setBounds(430, 100, 200, 40);
		slowFault.setActionCommand("slowFault");
		slowFault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendSlowFault(model.getNumber().intValue());
			}
		});
		slowFault.setEnabled(false);

		startButton = new JButton("start simulation");
		startButton.setBounds(130, 100, 100, 40);
		startButton.setActionCommand("start");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				startButton.setEnabled(false);
				doorFault.setEnabled(true);
				slowFault.setEnabled(true);
				System.out.println("Starting simulation from gui.");

				SimulationRunnable simulationRunnable = null;
				try {
					simulationRunnable = new SimulationRunnable(new String[] {});
				} catch (SocketException e) {
					e.printStackTrace();
				}
				Thread simThread = new Thread(simulationRunnable);
				simThread.start();
				
				Thread statusThread = new Thread(
						new StatusUpdater(selfReference, elevators, simulationRunnable.getSimulation()));
				statusThread.start();
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
		for (int i = 0; i < elevators; i++) {
			visualFloorSetup(i + 1, floors);
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
	private void visualFloorSetup(int elevatorNumber, int floors) {
		//every floor will be a JPanel & the jpanel with green border represents elevator position
		
		// Each elevator column needs a map of floors
		floorPanels.put(elevatorNumber, new HashMap<>());
		
		//sub panel elevatorPanel within centerPanel
		JPanel elevatorPanel = new JPanel();
		elevatorPanel.setBackground(Color.blue);
		elevatorPanel.setLayout(new BorderLayout());
			
		// floors
		JPanel oneFloorPanel = new JPanel();
		oneFloorPanel.setLayout(new BoxLayout(oneFloorPanel, BoxLayout.PAGE_AXIS)); // top to bottom
		for (int i = floors; i > 0; i--) {
			JPanel oneFloor = new JPanel();
			if (i != 1) {
				oneFloor.setBorder(BorderFactory.createLineBorder(Color.gray));
				oneFloor.setPreferredSize(new Dimension(30, 22));
			}
			else {
				//first floor is green by default (elevator starts there)
				oneFloor.setBorder(BorderFactory.createLineBorder(Color.green, 2));
				oneFloor.setPreferredSize(new Dimension(30,22));
			}

			JLabel floorLabel = new JLabel("Floor " + i);
			oneFloor.add(floorLabel);
			// Add the floor panel to the list.
			floorPanels.get(elevatorNumber).put(i, oneFloor);
			oneFloorPanel.add(oneFloor);
		}
		
		elevatorPanel.add(oneFloorPanel, BorderLayout.CENTER);
		
		// elevator state (open or closed)
		JPanel doors = new JPanel();
		doors.setBackground(Color.gray);
		JLabel stateTitle = new JLabel(idleStateOpen);
		doors.add(stateTitle);
		stateLabels.put(elevatorNumber, doors);
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
	
	public synchronized void updateState(ElevatorStatusRequest status) {
		int elevatorNumber = status.getElevatorNumber();
		switch (status.getState()) {
		case MOVING_DOWN:
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setBackground(Color.blue);
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setText(movingDown);
			break;
		case MOVING_UP:
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setBackground(Color.blue);
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setText(movingUp);
			break;
		case STOP_CLOSED:
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setBackground(Color.blue);
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setText(idleStateClosed);
			break;
		case STOP_OPENED:
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setBackground(Color.gray);
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setText(idleStateOpen);
			break;
		default:
			break;
		}
		
		if (status.isBroken()) {
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setBackground(Color.orange);
			((JLabel) stateLabels.get(elevatorNumber).getComponent(0)).setText(idleStateOpen);
		}
		
		// Set where the floor is
		Map<Integer, JPanel> elevatorFloors = floorPanels.get(elevatorNumber);
		
		// Set them all to default colors
		for (Map.Entry<Integer, JPanel> entry: elevatorFloors.entrySet()) {
			entry.getValue().setBorder(BorderFactory.createLineBorder(Color.gray));
			
			if (entry.getKey() == status.getFloorNumber()) {
				// If we are at the current floor, make it green
				entry.getValue().setBorder(BorderFactory.createLineBorder(Color.green, 2));
			}
		}
		
	}
	
	public synchronized void simulationComplete(long endTime) {
		
		// TODO, show the completion time somewhere on GUI?
		System.out.println("Simulation Complete, time taken: " + endTime);
		
		startButton.setEnabled(true);
		doorFault.setEnabled(false);
		slowFault.setEnabled(false);
	}
}
