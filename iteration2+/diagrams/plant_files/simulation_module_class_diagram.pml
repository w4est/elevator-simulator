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
   - {static} isGuiFlagInStringArgs(String[]): boolean
   - {static} getElevatorMaxFloorInArgs(String[]): boolean
   - {static} getElevatorNumberInArgs(String[]): boolean
}

class SimulationGUI {
    + {static} String idleStateOpen
    + {static} String idleStateClosed
    + {static} String movingUp
    + {static} String movingDown
    + {static} String broken

    - SimulationGUI selfReference
    - String[] args
    - JFrame frame
    - JPanel centerPanel
    - JButton startButton
    - JButton doorFault
    - JButton slowFault
    - Map<Integer, Map<Integer, JPanel>> floorPanels
    - Map<Integer, JPanel> stateLabels
    - ArrayList<JLabel> floorLamps

   + openScreen() : void
   - visualFloorSetup(int, int): void
   - sendDoorFault(int) : void
   - sendSlowFault(int) : void
   - sendFaultToElevatorListener(byte[], int): void
   + updateState(ElevatorStatusRequest): void
   + updateFloor(FloorStatusRequest): void
   + simulationComplete(long): void
}

class SimulationRunnable <<Runnable>> {
    - Simulation sim;
    - String[] args;
    - DatagramSocket socket;
    + run(): void
}

class StatusUpdater <<Runnable>> {
    - SimulationGUI gui
    - Simulation simulation
    - boolean running = true
    - DatagramSocket listenSocket
	
    - ElevatorStatusRequest[] lastUpdate
    - long[] lastUpdateTime
    - long startTime = 0L
    - boolean elevatorStable
    - {static} long stabilityDelta

    + run(): void
    checkForUpdates(): void
    checkIfFinished(): boolean
}

    SimulationRunnable --> Simulation
    SimulationRunner ..> SimulationRunnable
    SimulationRunner ..> SimulationGUI
    SimulationGUI ..> SimulationRunnable
    SimulationGUI --> SimulationGUI
    InputFileReader ..> SimulationEntry
    InputFileReader ..> Direction
    Simulation ..> InputFileReader
    Simulation ..> SimulationEntry
    StatusUpdater --> SimulationGUI
    StatusUpdater --> Simulation
}


StatusUpdater ..|> ElevatorStatusRequest
StatusUpdater ..|> FloorStatusRequest
Simulation ..|> PacketUtils
SimulationGUI ..|> PacketUtils
SimulationEntry -> LocalTime
Request -> LocalTime
@enduml