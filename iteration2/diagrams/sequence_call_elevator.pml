@startuml

actor Patron
participant FloorSubsystem
participant Scheduler
participant ElevatorSubsystem


Patron -> FloorSubsystem: Call elevator to floor 1
activate FloorSubsystem
loop Elevator polling queue
activate ElevatorSubsystem
ElevatorSubsystem -> Scheduler: I'm idle
activate Scheduler
Scheduler -> ElevatorSubsystem: Instruction queue empty
deactivate Scheduler
deactivate ElevatorSubsystem
end loop



FloorSubsystem -> Scheduler: Add floor 1 to visit queue
deactivate FloorSubsystem
activate Scheduler
activate ElevatorSubsystem
ElevatorSubsystem -> Scheduler: I'm idle
Scheduler --> ElevatorSubsystem: Next Instruction, go to floor 1
ElevatorSubsystem --> Scheduler: on my way

Scheduler --> FloorSubsystem: elevator on way
deactivate Scheduler
activate FloorSubsystem
FloorSubsystem --> Patron : turn on glowing light
deactivate FloorSubsystem


@enduml