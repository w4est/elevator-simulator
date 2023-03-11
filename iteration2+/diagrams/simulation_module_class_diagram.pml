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
    + {static} fromByteArray(byte[]) : Request
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

   Request -> Direction
}


package "simulation module" #DDDDDD {
class Simulation {

   - String[] args
   - DatagramSocket datagramSocket

   + runSimulation() : void
   - {static} readInputFileFromStringArgs(String[]) : String
   - {static} isRealtimeFlagInStringArgs(String[]) : boolean
   - {static} isItRequestTime(LocalTime, long, SimulationEntry) : boolean
   - {static} sendRequestAtFloor(SimulationEntry, DatagramSocket) : boolean
}

class InputFileReader {
    - BufferedReader bufferedReader

    + SimulationEntry getNextEntry()
    + void close()
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

class SimulationRunner {
   + main(String[]) : void
}

    SimulationRunner ..> Simulation
    InputFileReader ..> SimulationEntry
    InputFileReader ..> Direction
    Simulation ..> InputFileReader
    Simulation ..> SimulationEntry
}

class DatagramSocket {

}

Simulation -> DatagramSocket
Simulation ..|> PacketUtils
SimulationEntry -> LocalTime
Request -> LocalTime
@enduml