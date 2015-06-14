/**
 * 
 */
package my.mesosphere;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import my.mesosphere.ElevatorUtil.direction;

/**
 * @author naveen-tirupattur
 *
 */
public class MyElevatorControlSystem implements ElevatorControlSystem{

	int numFloors;
	List<Elevator> elevators;
	LinkedList<ElevatorRequest> elevatorRequestsQueue;


	public int getNumFloors() {
		return numFloors;
	}

	public void setNumFloors(int numFloors) {
		this.numFloors = numFloors;
	}

	public List<Elevator> getElevators() {
		return elevators;
	}

	public void setElevators(List<Elevator> elevators) {
		this.elevators = elevators;
	}

	public LinkedList<ElevatorRequest> getElevatorRequestsQueue() {
		return elevatorRequestsQueue;
	}

	public void setElevatorRequestsQueue(
			LinkedList<ElevatorRequest> elevatorRequestsQueue) {
		this.elevatorRequestsQueue = elevatorRequestsQueue;
	}

	public MyElevatorControlSystem(int numFloors) {
		this.numFloors = numFloors;
		elevators = new ArrayList<Elevator>();
		elevatorRequestsQueue = new LinkedList<ElevatorRequest>();

	}

	// Object to store elevator requests
	public class ElevatorRequest {
		int currentFloor;
		ElevatorUtil.direction direction;

		public ElevatorRequest(int currentFloor, ElevatorUtil.direction direction) {
			this.currentFloor = currentFloor;
			this.direction = direction;
		}
	}

	@Override
	public Elevator status(int elevatorId) throws Exception{
		// Get the elevator
		for(Elevator e:elevators) {
			if(e.getElevatorId()==elevatorId) {
				return e;
			}
		}
		return null;
	}

	@Override
	public void pickup(int floorNumber, direction direction) throws Exception{
		// Add the request to a queue
		if(floorNumber > numFloors || floorNumber < 0) throw new Exception("Invalid floor");
		ElevatorRequest newRequest = new ElevatorRequest(floorNumber, direction);
		synchronized (elevatorRequestsQueue) {
			elevatorRequestsQueue.add(newRequest);
		}
	}

	// Get the nearest elevator
	public Elevator getNearestElevator(ElevatorRequest request) throws Exception{
		// Maintain the
		int minDistance = Integer.MAX_VALUE, tempMinimumDistance = 0, elevatorId = -1;
		if(request.direction.equals(direction.UP)) {
			for(Elevator e: elevators) {
				// Check all the elevators going up or stationary elevators close to the current floor
				if(e.currentFloor < request.currentFloor && (e.state.equals(direction.UP) || e.state.equals(direction.STILL))) {
					tempMinimumDistance = request.currentFloor - e.currentFloor;
					if(tempMinimumDistance < minDistance) {
						minDistance = tempMinimumDistance;
						elevatorId = e.elevatorId;
					}
				}
			}

		} else if(request.direction.equals(direction.DOWN)) {
			for(Elevator e: elevators) {
				// Check all the elevators going down or stationary elevators close to the current floor
				if(e.currentFloor > request.currentFloor && ( e.state.equals(direction.DOWN) || e.state.equals(direction.STILL))) {
					tempMinimumDistance = e.currentFloor - request.currentFloor;
					if(tempMinimumDistance < minDistance) {
						minDistance = tempMinimumDistance;
						elevatorId = e.elevatorId;
					}
				}
			}
		}

		// No closest elevator found - then get the first elevator in the list
		if(elevatorId == -1) return elevators.get(0);

		return status(elevatorId);
	}


	@Override
	public void step() throws Exception{
		System.out.println("Checking for any new requests");
		ElevatorRequest request = null;
		// Check if there are more requests in the queue
		if(!elevatorRequestsQueue.isEmpty()) {

			synchronized (elevatorRequestsQueue) {
				if(!elevatorRequestsQueue.isEmpty()) {
					// Get the next request
					request = elevatorRequestsQueue.removeFirst();
					System.out.println("Found a new request at floor: "+request.currentFloor+" in direction: "+request.direction);
				}
			}

			// If request exists
			if(request != null) {
				// Get the nearest elevator for this request
				Elevator e = getNearestElevator(request);
				System.out.println("Found elevator: "+e.elevatorId);
				if(request.direction.equals(direction.UP) && e.currentFloor!=numFloors) {
					synchronized (e.upQueue) {
						// Update the elevator's queue with the request
						e.upQueue.add(request.currentFloor);
					}
				} else if(request.direction.equals(direction.DOWN) && e.currentFloor!=0) {
					synchronized (e.downQueue) {
						// Update the elevator's queue with the request
						e.downQueue.add(request.currentFloor);
					}
				}
			}
		}

	}

	public static void main(String[] args) {

		//Start the simulation
		System.out.println("*******************************************************************");
		System.out.println("Starting the elevator control system simulation - Type quit to exit");
		System.out.println("*******************************************************************");

		if(args.length != 2) {
			System.out.println("Invalid arguments. Please enter the number of floors and number of elevators");
			System.out.println("Usage: java -jar mesosphere.jar <num of floors> <num of elevators>");
			System.exit(1);
		}

		// Get the number of floors and elevators
		int numFloors = Integer.parseInt(args[0]);
		int numElevators = Integer.parseInt(args[1]);

		final MyElevatorControlSystem controlSystem = new MyElevatorControlSystem(numFloors);

		// Start the elevators
		for(int i=0;i<numElevators;i++) {
			Elevator e = new Elevator(i);
			controlSystem.getElevators().add(e);
			Thread t = new Thread(e);
			t.setName("Elevator "+i);
			t.start();
		}

		// Start the request handler
		Thread stepThread = new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						controlSystem.step();
						Thread.sleep(10000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});  
		stepThread.setName("Scheduler thread");
		stepThread.start();

		// Send the requests in intervals
		try {
			controlSystem.pickup(1, direction.UP);
			Thread.sleep(1000);
			controlSystem.pickup(2, direction.UP);
			Thread.sleep(1000);
			controlSystem.pickup(1, direction.UP);
			Thread.sleep(1000);
			controlSystem.pickup(1, direction.DOWN);
			Thread.sleep(1000);
			controlSystem.pickup(5, direction.DOWN);
			Thread.sleep(1000);
			controlSystem.pickup(4, direction.DOWN);
			Thread.sleep(1000);
			controlSystem.pickup(3, direction.UP);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// End the simulation
		while(true) {
			Scanner scan = new Scanner(System.in);
			String s = scan.next();
			if(s.equalsIgnoreCase("quit")) {
				System.out.println("***********************************************");
				System.out.println("Exiting the elevator control system simulation");
				System.out.println("***********************************************");
				scan.close();
				System.exit(0);
			}
		}


	}
}
