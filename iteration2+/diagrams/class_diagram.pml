@startuml

interface Runnable {
    + void run
}

class Floor {
    - final int floorNumber
    - int numberOfPeople
    - boolean upButton
    - boolean downButton

    + void addNumberOfPeople (int peopleOnFloor)
    + void setUpButton(boolean up)
    + void setDownButton(boolean down)
    + void removePeople(int peopleLeavingFloor)
    + int getNumPeople()
    + int getFloorNumber()
    + boolean getUpButton()
    + boolean getDownButton()
}
class FloorSubsystem <<Runnable>> {
    - ArrayList<Floor> allFloors
    - Scheduler scheduler
    - int peopleWaitingOnAllFloors
    - String TEST_FILE
    - int MAX_FLOOR
    - TreeMap<LocalTime, Request> allRequests

    - void addFloor(int floorNumber, int numPeople)
    - void updatePeopleWaitingOnAllFloors()
    + int getPeopleWaitingOnAllFloors()
    + void readInputFromFile()
    - void removePersonFromFloor(int floorNumber)
    + void getElevatorInfoFromScheduler(int elevatorNumber, int departureFloorNumber, int targetFloorNumber)
    + void run()
}
class InputFileReader {
    - BufferedReader bufferedReader

    + SimulationEntry getNextEntry()
    + void close()
}

class Scheduler {
    - boolean elevatorNeeded = false
    - TreeMap<LocalTime, Request> requests
    - ElevatorSubsystem elevatorSubsys
    - FloorSubsystem floorSubsystem
    - boolean done
    - SchedulerStates state

    + void addElevatorSubsys(ElevatorSubsystem e)
    + void addFloorSubsys(FloorSubsystem f)
    + void requestElevator(LocalTime time, Request request)
    + void elevatorNeeded()
    + void requestReceived(int elevatorNumber, int departureFloorNumber, int targetFloorNumber)
    + boolean getElevatorNeeded()
    + boolean isDone()
    + void toggleDone()
}

enum SchedulerStates {
    CheckForRequests
    IncompleteRequests
    CheckForResponses

    + SchedulerStates nextState()
}

class ElevatorSubsystem <<Runnable>> {
    - Elevator elevator
    - Scheduler scheduler
    + {static} int DEFAULT_MAX_FLOOR
    + {static} int DEFAULT_MIN_FLOOR
    - int MAX_FLOOR
    - int MIN_FLOOR
    - ArrayList<Request> floorQueues
    - boolean operateComplete

    + Elevator getElevator()
    + int getMinFloor()
    + int getMaxFloor()
    + ArrayList<Request> getFloorQueues()
    + void updateFloorQueue(Request r)
    - void operate()
    - void moveElevator()
    - void movePeopleOnElevator(int currentFloor)
    - void changeDirection()
    - void stopElevator()
    + void run() 
}

class Elevator <<Runnable>> {
    - int currentFloor
    - Direction currentDirection
    - int carNumber
    - ElevatorState elevatorState
    - ArrayQueue<Request> elevatorQueue

    + ElevatorState getCurrentElevatorState()
    + void nextElevatorState()
    + void setElevatorStateManually(ElevatorState newState)
    + int getCarNumber()
    + int getCurrentFloor()
    + Direction getCurrentDirection()
    + void setCurrentFloor(int currentFloor)
    + void setCurrentDirection(Direction currentDirection)
    + ArrayList<Request> getElevatorQueue()
    + void addPeople(Request r)
    + boolean allPeoplePickedUp()
    + boolean stop()
    + int clearFloor()
    + void run()
}

enum ElevatorState {
    STOP_OPENED
    STOP_CLOSED
    MOVING_UP
    MOVING_DOWN

    + ElevatorState nextState()
}

class SimulationEntry {
    - LocalTime timestamp
    - int sourceFloor
    - boolean up
    - int destinationFloor

    + SimulationEntry fromString(String line)
    + String toString()
    + LocalTime getTimestamp()
    + int getSourceFloor()
    + boolean isUp()
    + int getDestinationFloor()
}

class Request {
    - int floorNumber
    - Direction floorButton
    - int carButton
    - boolean requestSent
    - boolean reachedStartFloor

    + int getFloorNumber()
    + Direction getFloorButton()
    + int getCarButton()
    + void setRequest(boolean sentRequest)
    + boolean getRequestStatus()
    + boolean getReachStartingFloor()
    + setReachedStartFloor(boolean arrived)
}

enum Direction {
   UP
   DOWN
   IDLE
}

class Simulation {
    - {static} Thread floorSubsystemThread
    - {static} Thread elevatorSubsystemThread

    + main(String[] args)
}

Runnable <|.. FloorSubsystem
Runnable <|.. ElevatorSubsystem
FloorSubsystem "1" --> "N" Floor
FloorSubsystem ..> InputFileReader
FloorSubsystem ..> SimulationEntry
FloorSubsystem "1" --> "N" Request
InputFileReader ..> SimulationEntry
InputFileReader ..> Direction
FloorSubsystem "1" ---> "1" Scheduler
ElevatorSubsystem "1" ---> "1" Scheduler
ElevatorSubsystem "1" --> "N" Elevator
ElevatorSubsystem "1" --> "N" Request
Elevator --> ElevatorState
Elevator --> Direction
Elevator "1" --> "N" Request
Request --> Direction
Scheduler "1" --> "N" Request
Scheduler --> SchedulerStates
Simulation "1" --> "1" ElevatorSubsystem
Simulation "1" --> "1" FloorSubsystem
@enduml