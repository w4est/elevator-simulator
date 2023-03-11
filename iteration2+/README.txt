# SYSC3303A2 - L2 GROUP 2 : ELEVATOR CONTROL SYSTEM & SIMULATOR

* Subear Jama - 101154626
* Jacob Hovey - 101163798
* William Forrest - 100803271
* Farhan Mahamud - 101147861

## Overview
Our program is composed of four major components; the floor subsystem and the elevator subsystem are the threads, and 
the scheduler is the lock for the threads. Our fourth component, what we have called the simulator, is the 
entry point to run the program. For this iteration, all the simulator does is contain our main function. 

To run the program, you must run Simulation.java in the simulator package. This should then automatically 
input an example text file that we have created with elevator request data, and will print the results 
showing that the data was passed back and forth between the subsystems, through the scheduler.

In our test folder, we have created tests for the floor subsystem, file reader and elevator subsystem. We
believe that these tests are enough to fully encompass the functionality of our program for iteration 1. 
An important thing to note is that we have used Mockito in some of our tests. It is a mocking framework that 
helped us to write clean and simple tests. This should not present any issue when running our program or tests, but
 we believe it is important to note that this library has been used in case there are any discrepancies.

All of the tests are included in the same java packages as the source, but under the test folder:
1. elevator (package)
   * ElevatorSubsystemTest.java
   * ElevatorTest.java
2. floor (package)
   * FloorSubsystemTest.java
   * InputFileReaderTest.java
3. scheduler (package)
   * SchedulerTest.java
4. simulator (package)
   * SimulationTest.java

Each of these suites can be run from their respective files.

## Main Filenames (Explained)
Below are the files within src that make up the Elevator Control System.
All the diagrams (UML Class Diagram and UML Sequence Diagrams) are located in the "diagrams" folder.

1. elevator (package)
   * Direction.java : enum used to indicate direction of the elevator
   * ElevatorState.java : enum used to indicate the elevator state
   * ElevatorSubsystem.java : used to communicate with the Scheduler to manage the elevator. Implements runnable to be a thread.
   * Elevator.java : used within ElevatorSubsystem to represent the state of the elevator. Implements runnable to be a thread.
2. floor (package)
   * FloorSubsystem.java : reads input text file and sets up Floors to communicate with the Scheduler. Implements runnable to be a thread.
   * Floor.java : used in FloorSubsystem to set up a floor in a building.
   * InputFileReader.java : used within FloorSubsystem to help read a text file.
   * SimulationEntry.java : used within FloorSubsystem to help read a text file.
3. scheduler (package)
   * Scheduler.java : responsible for communication between FloorSubsystem and ElevatorSubsystem. keeps everything thread-safe (Mutual Exclusion and Condition Synchronization)
   * Request.java : used to save all relevant request information to use for optimizing scheduling.
   * SchedulerStates.java : A enumeration of states that the scheduler can be in
4. simulator (package) 
   * Simulation.java : responsible for program initialization & running the simulation from the console

## Set Up Instructions (Using Eclipse)
Below are the set up instructions. For more information, see "L2G2_Test_Instructions.pdf".

### How to run
1. Extract the zip file
2. In Eclipse go to File > Import
3. Click the general folder and underneath select "Existing Maven Projects" and then click next
4. Check "Select root directory" and click browse to select the folder "iteration2". Click Finish
5. Right click the lab2group2 folder in the "Package Explorer" sidebar.
6. Go to "Maven"
7. Select Update Project...
8. Select the project and click "Ok"
9. Let eclipse download the appropriate dependencies (Such as JUnit)
10. In the "scheduler-module" package run the Scheduler.java
11. In the "elevator-module" package run the ElevatorSubsystem.java
12. In the "floor-module" package run the FloorSubsystem.java
13. In the "simulator" package run "Simulation.java" to send data the to the elevator system!
14. See progress in the console.

### Another way to run:
1. Extract the zip file
2. Open Eclipse and click File > New > Maven Project
3. Use file location folder where the zip was extracted to (Uncheck "Use Default Location")
4. Click Finish
5. Right click the lab2group2 folder in the "Package Explorer" sidebar.
6. Go to "Maven"
7. Select Update Project...
8. Select the project and click "Ok"
9. Let eclipse download the appropriate dependencies (Such as JUnit)
10. Run Simulation.java to see the simulation run in the console!

### Running tests (Using JUnit)
1. Right click the "tests" folder in the project explorer
2. Select "run as JUnit tests"


### Running tests (Using maven and JUnit)
1. Right click the lab2group2 folder in the "Package Explorer" sidebar.
2. Go to "Run as"
3. Select "Maven build..."
4. Input the goals as "compile test"


## Breakdown Of Responsibilities
For more information on how everything was split equally, see "L2G2_Teamwork_Breakdown.pdf".
