@startuml
loop Every 100ms
ElevatorSimulationListener -> StatusUpdater: Send Elevator Updates
StatusUpdater -> SimulationGUI: Send Elevator Updates  
end loop
@enduml