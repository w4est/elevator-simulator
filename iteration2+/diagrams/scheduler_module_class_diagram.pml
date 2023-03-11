@startuml

package "common classes" #DDDDDD {

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
    + {static} fromByteArray(byte[]) : Request
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


Request -> LocalTime
@enduml


package "scheduler module" #DDDDDD {

    class Scheduler {
       - TreeMap<LocalTime, Request> requests
       
       + organizeRequest(LocalTime, Request) : void
       + sendPriorityRequest(Direction, int) : Request
       + {static} main(String[]) : void
    }

    class FloorHelper <<Runnable>> {
        - DatagramSocket receiveSocket
        - DatgramSocket sendSocket
	- DatagramPacket receivePacket
        - DatagramPacket sendPacket
	- Scheduler scheduler
  
        + run() : void
        + receivePacket() : void
        + sendPacket(byte[]) : void
    }

    class ElevatorHelper <<Runnable>> {
        - DatagramSocket receiveSocket
        - DatgramSocket sendSocket
	- DatagramPacket receivePacket
        - DatagramPacket sendPacket
	- Scheduler scheduler
	- FloorHelper floorHelper
  
        + run() : void
        + receiveSendPacket() : void
    }
}


class DatagramSocket {

}

Request -> LocalTime
FloorHelper -> DatagramSocket
ElevatorHelper -> DatagramSocket
FloorHelper -> DatagramPacket
ElevatorHelper -> DatagramPacket
FloorHelper ..|> Request
ElevatorHelper -> ElevatorInfoRequest
FloorHelper ..|> PacketUtils
ElevatorHelper ..|> PacketUtils
Scheduler ..|> Direction
Scheduler ..|> PacketUtils
Scheduler ..|> FloorHelper
Scheduler ..|> ElevatorHelper
@enduml