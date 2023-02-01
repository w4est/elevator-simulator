@startuml

actor Patron
participant FloorSubsystem
participant Scheduler
participant ElevatorSubsystem


activate ElevatorSubsystem
ElevatorSubsystem -> Scheduler : Arrived at Floor 1
activate Scheduler
Scheduler -> FloorSubsystem : Elevator at Floor 1
activate FloorSubsystem
Scheduler -> ElevatorSubsystem : Open doors
deactivate Scheduler

FloorSubsystem -> Patron: Doors are open


Patron --> ElevatorSubsystem : Enter elevator

ElevatorSubsystem -> Scheduler : Closing doors
activate Scheduler
Scheduler -> FloorSubsystem : Close doors
FloorSubsystem -> Patron : Doors are closed
deactivate Scheduler
deactivate FloorSubsystem

Patron -> ElevatorSubsystem : Go to floor
ElevatorSubsystem -> Scheduler : Go to floor
activate Scheduler
Scheduler --> ElevatorSubsystem : Next instruction floor X
deactivate Scheduler


@enduml