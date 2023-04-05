@startuml
loop FloorHelper Run Sequence
FloorHelper -> Scheduler: Organize Requests
end loop

loop ElevatorHelper Run Sequence
ElevatorHelper -> Scheduler: Get Requests for Elevator
Scheduler -> ElevatorHelper: Send Requests for Elevator
ElevatorHelper -> Scheduler: Send Elevator Status
end loop

Scheduler -> FloorHelper: Send Elevator Status
@enduml