@startuml
loop Every 100ms
FloorSimulationListener -> StatusUpdater: Send Elevator Updates
StatusUpdater -> SimulationGUI: Send Elevator Updates  
end loop
@enduml