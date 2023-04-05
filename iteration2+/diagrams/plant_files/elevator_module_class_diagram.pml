@startuml
package "elevator-module" #DDDDDD  {
 
class ElevatorRunner {
    + {static} int DEFAULT_MAX_FLOOR
    + {static} int DEFAULT_MIN_FLOOR
    + {static} long DEFAULT_FLOOR_MOVEMENT_TIME
    + {static} long DEFAULT_DOOR_MOVEMENT_TIME
    + {static} long DEFAULT_LOAD_TIME_PER_PERSON
    + {static} int DEFAULT_NUM_ELEVATORS 

    + {static} main: void
}

class ElevatorSubsystem <<Runnable>> {
    - Elevator elevator
    - int MAX_FLOOR
    - int MIN_FLOOR
    - ArrayList<Request> floorQueues
    - boolean running

    + getElevator(): Elevator
    + getMinFloor(): int
    + getMaxFloor(): int
    + getFloorQueues(): ArrayList<Request>
    + addRequests(List<Request> requests): void
    - operate(): void
    + moveElevator(): void
    + movePeopleOnElevator(int currentFloor): void
    + changeDirection(): void
    + stopElevator(): int
    + emergencyStop(): void
    + activateSlowFault(): void
    + run(): void
}

class Elevator {
    - long floorMovementTime
    - long doorMovementTime
    - long loadTimePerPerson

    - int currentFloor
    - Direction currentDirection
    - int carNumber
    - ElevatorState elevatorState
    - ArrayQueue<Request> elevatorQueue
    - boolean slowMode
    - boolean broken

    + getFloorMovementTime: long
    + setFloorMovementTime(long): void
    + getDoorMovementTime(): long
    + setDoorMovementTime(long): void
    + getLoadTimePerPerson(): long
    + setLoadTimePerPerson(long): void

    + getCurrentElevatorState(): ElevatorState
    + nextElevatorState(): void
    + setElevatorStateManually(ElevatorState newState): void
    + getCarNumber(): int
    + getCurrentFloor(): int
    + getCurrentDirection(): Direction
    + setCurrentFloor(int currentFloor): void
    + setCurrentDirection(Direction currentDirection): void
    + getElevatorQueue(): ArrayList<Request>
    + addPeople(Request r): void
    + isSlowMode(): boolean
    + setSlowMode(boolean slowMode): void
    + allPeoplePickedUp(): boolean
    + stopStartFloorCheck(): boolean
    + stopDestinationCheck(): boolean
    + clearFloor(): int
    + openOrCloseDoor(): void
    + waitForPeopleToMoveOffOrOnElevator(int numPeople): void
}

class ElevatorListener <<Runnable>> {
    - ElevatorSubsystem elevSys
    - DatagramPacket sendPacket
    - DatagramPacket receivePacket
    - DatagramSocket socket

    - setupSocket(): void
    + updateFloorQueue(): void
    - addRequestsFromBytes(byte[] requestData): void
    - sendElevatorRequestPacket(byte[] data): byte[]
    + run(): void
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


class SimulationListener {
    + ElevatorSubsystem elevSys
    + DatagramPacket sendPacket
    DatagramSocket socket
    - static boolean debug

    - setupSocket(): void
    + sendElevatorUpdatePacket(): void
    + run(): void
}

   ElevatorSubsystem -> Elevator
   ElevatorRunner ..|> ElevatorSubsystem
   ElevatorRunner ..|> ElevatorListener
   ElevatorRunner ..|> ElevatorFaultListener
   ElevatorRunner ..|> SimulationListener


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
    - int carNumber;
    - int floorNumber;
    - Direction direction;
    - ElevatorState state;

    + toByteArray() : byte[]
    + {static} fromByteArray(byte[]) : ElevatorInfoRequest

    + getCarNumber() : int
    + getFloorNumber() : int
    + setFloorNumber(int) : void
    + getDirection() : Direction
    + setDirection(Direction) : void
    + getState() : ElevatorState
    + setState(ElevatorState) : void
}

class ElevatorStatusRequest {
    - int elevatorNumber
    - int floorNumber
    - int pendingRequests
    - boolean broken
    - ElevatorState state

    + toByteArray() : byte[]
    + {static} fromByteArray(byte[]) : ElevatorStatusRequest

    + getFloorNumber(): int
    + isBroken(): boolean
    + getState(): ElevatorState
    + getElevatorNumber(): int
    + getPendingRequests(): int
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
    + {static} int SIMULATION_PORT

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

class FloorStatusRequest {
    - int floorNumber
    - int numOfPeople
    - boolean upButtonPressed
    - private boolean downButtonPressed
    - private int elevatorCarNum
    - private int elevatorCurrentFloor

    + toByteArray(): byte[]
    + {static} fromByteArray(message:byte[]): FloorStatusRequest
}


enum PacketHeaders {
   Request
   ElevatorInfoRequest
   ElevatorStatus
   FloorStatus
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
   ElevatorInfoRequest ..|> PacketHeaders
   ElevatorStatusRequest ..|> PacketHeaders
   FaultMessage ..|> PacketHeaders
   FloorStatusRequest ..|> PacketHeaders
   
   FaultMessage -> Fault
   
}


Request -> LocalTime



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