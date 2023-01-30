package scheduler;

import java.util.ArrayList;

import elevator.*;
import elevator.ElevatorSubsystem.Direction;

/**
 * @author Jacob Hovey
 * 
 * The scheduler for the elevator system. All thread synchronization
 * will occur in this class.
 *
 */
public class Scheduler {	
	//this will be set to true when there are people in the queue who have not been serviced
	private boolean elevatorNeeded = false;
	private ArrayList<ElevatorSubsystem> elevatorSubsys;
	
	public Scheduler() {
		elevatorSubsys = new ArrayList<>();
	}
	/**
	 * TBD
	 */
	public static void main(String[] args) {
		// TBD

	}
	
	public void addElevatorSubsys(ElevatorSubsystem e) {
		elevatorSubsys.add(e);
	}
	
	/**
	 * This is the command that runs when a user presses a call elevator button on a specific floor.
	 * 
	 * 
	 * @param floorNumber
	 * @param direction		The Direction enum. If this is named differently I can fix that.
	 * @param elevatorNumber
	 */
	public synchronized void requestElevator(int floorNumber, Direction direction, int elevatorNumber) {
		//this will log the time it was received and parse the information to prioritize the request.
		
		/*
		 * Scheduler parses through the different elevator subsystems and find the one whose car number is elavatorNumber
		 * It sends the floor request to the elevator subsystem
		 */
		
	}
	
	/**
	 * this is the command that runs when a user presses an elevator button 
	 * to select a floor.
	 * 
	 * @param currentFloor
	 * @param targetFloor
	 * @param elevatorNumber
	 */
	public synchronized void setDestination(int currentFloor, int targetFloor, int elevatorNumber) {
		//this will log the time it was received and parse the information to prioritize the request.
	}
	
	/**
	 * This should be constantly called by waiting elevator threads
	 * to see if there is a job for them.
	 */
	public synchronized void elevatorNeeded() {
		//this will set the elevator to wait if elevatorNeeded is false, 
		//otherwise will pull the queue and prioritize where the elevator should go
	}
	
	
	//below are ideas for me later of how to send pending info
	
	public void setDirectionLamp(int elevatorNumber, Direction direction) {
		//this will set the direction lamp for a specific elevator to the correct direction
	}
	
	public void setFloorButton(int elevatorNumber, int floorNumber) {
		//this will set the floor button for a specific elevator to on or off
	}
	
	public void setDirectionButton(int floorNumber, Direction direction) {
		//this will set the direction button for a specific elevator to the correct direction
		//these probably need more differentiated names tbh
	}
	
	public void closeDoor(int elevatorNumber) {
		//this will tell a specific elevator to close the door (will likely be wrapped into "elevatorNeeded" function)
	}
	
	public void startMotor(int elevatorNumber) {
		//this will tell a specific elevator to start the motor (will likely be wrapped into "elevatorNeeded" function)
	}
	
	//finally, need to add and maintain the queue of requests, and determine how best to prioritize it
}
