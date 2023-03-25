@startuml

package "elevator-module" #DDDDDD  {
 

class ElevatorSubsystem <<Runnable>> {
    - Elevator elevator
    + {static} int DEFAULT_MAX_FLOOR
    + {static} int DEFAULT_MIN_FLOOR
    - int MAX_FLOOR
    - int MIN_FLOOR
    - ArrayList<Request> floorQueues

    + Elevator getElevator()
    + int getMinFloor()
    + int getMaxFloor()
    + ArrayList<Request> getFloorQueues()
    + void addRequests(List<Request> requests)
    - void operate()
    + void moveElevator()
    + void movePeopleOnElevator(int currentFloor)
    + void changeDirection()
    + int stopElevator()
    + void emergencyStop()
    + {static} void main(String[] args)
    + void run() 
}

class Elevator {
    - int currentFloor
    - Direction currentDirection
    - int carNumber
    - ElevatorState elevatorState
    - ArrayQueue<Request> elevatorQueue
    - boolean slowMode

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
    + boolean isSlowMode()
    + void setSlowMode(boolean slowMode)
    + boolean allPeoplePickedUp()
    + boolean stopStartFloorCheck()
    + boolean stopDestinationCheck()
    + int clearFloor()
    + void openOrCloseDoor()
    + void waitForPeopleToMoveOffOrOnElevator(int numPeople)
}

class ElevatorListener <<Runnable>> {
    - ElevatorSubsystem elevSys
    - DatagramPacket sendPacket
    - DatagramPacket receivePacket
    - DatagramSocket socket
    - {static} boolean debug

    - void setupSocket()
    + void updateFloorQueue()
    - void addRequestsFromBytes(byte[] requestData)
    - byte[] sendElevatorRequestPacket(byte[] data)
    + {static} void printByteArray(byte[] message, int length)
    - void printInfo(byte[] data)
}

class ElevatorFaultListener <<Runnable>> {
    - ElevatorSubsystem elevSys
    - DatagramPacket receivePacket
    - DatagramSocket receiveSocket
    - int portNum
    - Thread elevatorThread

    + void checkForFaults()
    + void run()
}


}





package "common classes" #DDDDDD {
  class Request {
    - LocalTime localTime
    - int floorNumber
    - Direction floorButton
    - int carButton
    - boolean reachedStartFloor
    - boolean requestComplete

    + getLocalTime() : LocalTime
    + getFloorNumber() : int
    + getFloorButton() : Direction
    + getCarButton() : int
    + getReachedStartFloor() : boolean
    + setReachedStartFloor(boolean arrived) : void
    + getRequestComplete() : boolean
    + setRequestComplete() : void
    + toByteArray() : byte[]
    + {static} fromByteArray(message:byte[]) : List<Request>
} 

class ElevatorInfoRequest {
    - int floorNumber;
    - Direction direction;
    - ElevatorState state;

    + toByteArray() : byte[]
    + fromByteArray(byte[]) : ElevatorInfoRequest

    + getFloorNumber() : int
    + setFloorNumber(int) : void
    + getDirection() : Direction
    + setDirection(Direction) : void
    + getState() : ElevatorState
    + setState(ElevatorState) : void
}

enum Direction {
   UP
   DOWN
   IDLE

   + toInt()
   + {static} fromInt(int)
}

class PacketUtils {
    + {static} int BUFFER_SIZE
    + {static} int ELEVATOR_PORT
    + {static} int SCHEDULER_FLOOR_PORT
    + {static} int SCHEDULER_ELEVATOR_PORT
    + {static} int FLOOR_PORT
    + {static} int SYNC_PORT

    + {static} putStringIntoByteBuffer(int, byte[], String) : int
    + {static} packetContainsString(byte[], String) : boolean
    + {static} isEmptyBuffer(byte[]) : boolean
    + {static} localTimeToByteArray(LocalTime) : byte[]
    + {static} byteArrayToLocalTime(byte[]) : LocalTime
    + {static} stateToByteArray(ElevatorState): byte[]
}

enum Fault {
    DoorFault
    SlowFault
}

class FaultMessage {
    - Fault fault
    + getFault(): Fault
    + setFault(fault:Fault): void
    + toByteArray(): byte[]
    + {static} fromByteArray(message:byte[]): FaultMessage
}


enum PacketHeaders {
   Request
   ElevatorInfoRequest
   DoorFault
   SlowFault

   + getHeaderBytes() : byte[]
}

enum ElevatorState {
   STOP_OPENED
   STOP_CLOSED
   MOVING_UP
   MOVING_DOWN

   + nextState() : ElevatorState
   + toInt() : int
   + {static} fromInt() : ElevatorState
}

   Request -> Direction
   ElevatorInfoRequest -> Direction
   ElevatorInfoRequest -> ElevatorState
}

FaultMessage -> Fault



Elevator -> ElevatorState
Elevator -> Direction
Elevator -> Request

ElevatorListener -> ElevatorSubsystem
ElevatorFaultListener -> ElevatorSubsystem
ElevatorSubsystem -> Request
ElevatorListener ..|> ElevatorInfoRequest
ElevatorFaultListener  ..|> FaultMessage



ElevatorListener  ..|> PacketUtils
ElevatorFaultListener  ..|> PacketHeaders



Request -> LocalTime
@enduml