/**
 * 
 */
package my.mesosphere;

/**
 * @author naveen-tirupattur
 *
 */
public interface ElevatorControlSystem {
	
	public Elevator status (int elevatorId) throws Exception;
	
	public void pickup (int floorNumber, ElevatorUtil.direction direction) throws Exception;
	
	public void step() throws Exception;

}
