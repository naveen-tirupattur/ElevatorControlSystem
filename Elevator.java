/**
 * Elevator class
 */

package my.mesosphere;

import java.util.PriorityQueue;

public class Elevator implements Runnable {

	int elevatorId=-1;
	ElevatorUtil.direction state;
	int currentFloor=0;
	int destinationFloor=0;
	
	PriorityQueue<Integer> upQueue, downQueue;
	
	public Elevator(){
		
	}
	
	public Elevator(int elevatorId) {
		
		this.elevatorId = elevatorId;
		upQueue = new PriorityQueue<Integer>();
		downQueue = new PriorityQueue<Integer>();
	}
	
	public int getElevatorId() {
		return elevatorId;
	}

	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	public ElevatorUtil.direction getState() {
		return state;
	}

	public void setState(ElevatorUtil.direction state) {
		this.state = state;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}

	public PriorityQueue<Integer> getUpQueue() {
		return upQueue;
	}

	public void setUpQueue(PriorityQueue<Integer> upQueue) {
		this.upQueue = upQueue;
	}

	public PriorityQueue<Integer> getDownQueue() {
		return downQueue;
	}

	public void setDownQueue(PriorityQueue<Integer> downQueue) {
		this.downQueue = downQueue;
	}
	
	public int getNextFloor() throws Exception {
		// If the floor queues are not initialized then throw an exception
		if(upQueue == null && downQueue == null) {
			throw new Exception("Elevator not initialized");
		} else if(upQueue != null && !upQueue.isEmpty()) { // Check if there is a floor in up queue
			this.state = ElevatorUtil.direction.UP;
			return upQueue.peek();
		} else if(downQueue != null && !downQueue.isEmpty()) { // Check if there is a floor in down queue
			this.state = ElevatorUtil.direction.DOWN;
			return downQueue.peek();
		}
		
		// Return invalid floor value when there are no more floors in the queue
		return Integer.MAX_VALUE;
	}

	@Override
	public void run() {
		while(true) {
			try {
				// Get the next floor to visit
				int nextFloor = getNextFloor();
				if(nextFloor==Integer.MAX_VALUE) {
					this.state = ElevatorUtil.direction.STILL;
				} else {
					// There are more floors to visit going up 
					if(this.state==ElevatorUtil.direction.UP) {
						synchronized (upQueue) {
							upQueue.poll();
							System.out.println("Elevator: "+this.elevatorId+" going to floor "+nextFloor+" from floor "+this.currentFloor);
							setCurrentFloor(nextFloor);
						}
					} else {
						// No more floors to visit going up
						synchronized (downQueue) {
							downQueue.poll();
							System.out.println("Elevator: "+this.elevatorId+" going to floor "+nextFloor+" from floor "+this.currentFloor);
							setCurrentFloor(nextFloor);
						}
					}
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
