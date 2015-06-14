# ElevatorControlSystem
Elevator Control System Simulator

Elevator Class:

-> Has 2 Priority queues to store the requests for the direction of travel
-> This will cause the elevator to go to the next nearest floor in either direction.
-> Each elevator is started as a thread.
-> Continuously checks if its queues have any requests and attends to them.


Elevator Control System class:

-> Keeps track of elevators in the system
-> Stores all the requests in a queue
-> Scheduler runs continously to check the requests in the queue
-> Based on the requests, the scheduler tries to find the closest elevator going in same direction as request. This will reduce a lot of back and forth between elevators. Though not optimal it is better than FCFS in the sense that, you always find the closest elevator, so waiting time is reduced for the customer. If no elevator is found then the first elevator in the queue is returned. Several other variants can also be tried like closest elevator going in opposite direction or next closest elevator but due to lack of time, I did not implement those.
-> When a closest elevator is found, the scheduler updates the elevators queue with the request.











