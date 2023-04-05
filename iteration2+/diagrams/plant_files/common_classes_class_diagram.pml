@startuml

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
@enduml