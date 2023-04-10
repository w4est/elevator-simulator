@startuml
package "floor-module" #DDDDDD  {
 
class Floor {
    - FLOOR_NUMBER : int
    - numberOfPeople : int
    - upButton : boolean
    - downButton : boolean
    - floorLamps : ArrayList<Integer>

    + addNumberOfPeople(peopleOnFloor:int) : void
    + setUpButton(up:boolean) : void
    + setDownButton(down:boolean) : void
    + removePeople(peopleLeavingFloor:int) : void
    + getNumPeople() : int
    + getFloorNumber() : int
    + getUpButton() : int
    + getDownButton() : int
    + setFloorLamp(elevatorNumber:int,elevatorPosition:int) : void
    + getFloorLamp() : ArrayList<Integer>
}

class FloorSubsystem <<Runnable>> {
    - allFloors : ArrayList<Floor>
    - peopleWaitingOnAllFloors : int
    - MAX_FLOOR : int
    - allRequests : ArrayList<Request>
    - firstThreadActive : boolean
    - receiveSocket : DatagramSocket
    - sendSocket : DatagramSocket
    - receivePacket : DatagramPacket
    - sendPacket : DatagramPacket

    + getFloorRequests() : ArrayList<Request>
    + getAllFloors() : ArrayList<Floor>
    + addFloorRequests(r : Request) : void
    - closeSocket() : void
    - addFloor(floorNumber:int, numPeople:int, numOfElevators:int) : void
    - updatePeopleWaitingOnAllFloors() : void
    + getPeopleWaitingOnAllFloors() : int
    - removePersonFromFloor(floorNumber : int) : void
    + operate() : void
    + receiveInfo() : void
    + sendInfoToScheduler(requestByte : byte[]) : void
    + run() : void
    + {static} main(args:String[]): void
}

class FloorSimulationListener <<Runnable>> {
    - floorSys : FloorSubsystem
    - sendPacket : DatagramPacket
    # socket : DatagramSocket

    + floorSubsystemUpdatePacket() : void
    + run() : void
}

   FloorSubsystem "1" *-- "1..*" Floor
   FloorSubsystem ..> FloorSimulationListener

}




package "common classes" #DDDDDD {
  class Request {
    - localTime : LocalTime 
    - floorNumber : int
    - floorButton : Direction
    - carButton : int
    - reachedStartFloor : boolean
    - requestComplete : boolean

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

class PacketUtils {
    + {static} BUFFER_SIZE : int
    + {static} ELEVATOR_PORT : int
    + {static} SCHEDULER_FLOOR_PORT : int
    + {static} SCHEDULER_ELEVATOR_PORT : int
    + {static} FLOOR_PORT : int
    + {static} SIMULATION_PORT : int

    + {static} putStringIntoByteBuffer(int, byte[], String) : int
    + {static} packetContainsString(byte[], String) : boolean
    + {static} isEmptyBuffer(byte[]) : boolean
    + {static} localTimeToByteArray(LocalTime) : byte[]
    + {static} byteArrayToLocalTime(byte[]) : LocalTime
    + {static} stateToByteArray(ElevatorState): byte[]
}

class FloorStatusRequest {
    - floorNumber : int
    - numOfPeople : int
    - upButtonPressed : boolean
    - downButtonPressed : boolean 
    - elevatorCarNum : int
    - elevatorCurrentFloor : int

    + toByteArray(): byte[]
    + {static} fromByteArray(message:byte[]): FloorStatusRequest
    + getFloorNumber() : int
    + getNumOfPeople() : int
    + getUpButton() : boolean
    + getDownButton() : boolean
    + getElevatorCarNum() : int
    + getElevatorCurrentFloor() : int
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


   Request -> Direction
   FloorStatusRequest ..> PacketHeaders
   
}

FloorSimulationListener ..> FloorStatusRequest
FloorSubsystem ..> PacketUtils
FloorSubsystem -> Request
Request -> LocalTime


@enduml