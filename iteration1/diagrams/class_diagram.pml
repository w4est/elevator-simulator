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
    ArrayList<Floor> allFloors
    Scheduler schedular
    int peopleWaitingForFloor
    int MAX_FLOOR
    TreeMap<LocalTime, Request> allRequests

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

    + void addElevatorSubsys(ElevatorSubsystem e)
    + void addFloorSubsys(FloorSubsystem f)
    + void requestElevator(LocalTime time, Request request)
    + void elevatorNeeded()
    + void requestReceived(int elevatorNumber, int departureFloorNumber, int targetFloorNumber)
    + boolean getElevatorNeeded()
    + boolean isDone()
    + void toggleDone()
}

class ElevatorSubsystem <<Runnable>> {
    - Elevator elevator;
    - Scheduler scheduler;
    + static int DEFAULT_MAX_FLOOR = 7;
    + static int DEFAULT_MIN_FLOOR = 1;
    - int MAX_FLOOR;
    - int MIN_FLOOR;
    - ArrayList<Request> floorQueues;

    + Elevator getElevator()
    + int getMinFloor()
    + int getMaxFloor()
    + ArrayList<Request> getFloorQueues()
    + void updateFloorQueue(Request r)
    + void addJob(int destination, int people)
    + void move()
    + void changeDirection()
    + int getPeopleWaiting(int floor)
    - boolean goUp()
    - boolean goDown()
    + boolean allCompleted()
    + void run() 
}

class Elevator <<Runnable>> {
    - int currentFloor
    - String currentDirection
    - int carNumber

    + int getCarNumber()
    + int getCurrentFloor()
    + String getCurrentDirection()
    + void setCurrentFloor(int currentFloor)
    + void setCurrentDirection(String currentDirection)
    + void run()
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
    - String floorButton
    - int carButton
    - boolean requestSent

    + int getFloorNumber()
    + String getFloorButton()
    + int getCarButton()
    + void setRequest(boolean sentRequest)
    + boolean getRequestStatus()
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
FloorSubsystem "1" ---> "1" Scheduler
ElevatorSubsystem "1" ---> "1" Scheduler
ElevatorSubsystem "1" --> "N" Elevator
ElevatorSubsystem "1" --> "N" Request
Scheduler "1" --> "N" Request
Simulation "1" --> "1" ElevatorSubsystem
Simulation "1" --> "1" FloorSubsystem
@enduml