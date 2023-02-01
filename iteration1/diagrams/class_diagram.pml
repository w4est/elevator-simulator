@startuml
!theme spacelab-white
class Elevator
class ElevatorSubsystem

class Floor {
    - final int floorNumber
    - int numberOfPeople
    - boolean upButton
    - private boolean downButton

    + void addNumberOfPeople (int peopleOnFloor)
    + void setUpButton(boolean up)
    + void setDownButton(boolean down)
    + void removePeople(int peopleLeavingFloor)
    + int getNumPeople()
    + int getFloorNumber()
    + boolean getUpButton()
    + boolean getDownButton()
}
class FloorSubsystem {
    ArrayList<Floor> allFloors
    Scheduler schedular
    int peopleWaitingForFloor
    int MAX_FLOOR
    TreeMap<LocalTime, Request> allRequests

    - void addFloor(int floorNumber, int numPeople)
    void updatePeopleWaitingOnAllFloors()
    int getPeopleWaitingOnAllFloors()
    void readInputFromFile()
    - void removeAllPeopleFromFloor(int floorNumber)
    - void getElevatorInfoFromScheduler(int elevatorNumber, int departureFloorNumber, int targetFloorNumber)
    + void run()
}
class InputFileReader {
    - BufferedReader bufferedReader

    + getNextEntry()
    + close()
}



FloorSubsystem -> Floor : has floors
FloorSubsystem -> InputFileReader : has a reader

@enduml