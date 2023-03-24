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
				sendDoorFault(model.getNumber().intValue());
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
		frame.setSize(1280, 720);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	
	// TODO, verify that the ELEVATOR_PORT is the proper starting port per elevator subsystem, or alter the class to have 
	// an effected elevator
	private void sendDoorFault(int elevatorNumber) {
		try (DatagramSocket faultSocket = new DatagramSocket()) {
			byte[] fault = PacketHeaders.DoorFault.getHeaderBytes();
			DatagramPacket doorFaultPacket = new DatagramPacket(fault, fault.length, InetAddress.getLocalHost(),
					elevatorNumber + PacketUtils.ELEVATOR_PORT);
			faultSocket.send(doorFaultPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendSlowFault(int elevatorNumber) {
		try (DatagramSocket faultSocket = new DatagramSocket()) {
			byte[] fault = PacketHeaders.SlowFault.getHeaderBytes();
			DatagramPacket slowFaultPacket = new DatagramPacket(fault, fault.length, InetAddress.getLocalHost(),
					elevatorNumber + PacketUtils.ELEVATOR_PORT);
			faultSocket.send(slowFaultPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
