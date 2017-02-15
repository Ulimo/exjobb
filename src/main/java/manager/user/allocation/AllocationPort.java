package manager.user.allocation;

import se.sics.kompics.PortType;

/***
 * Port for allocation events on a user
 * @author Alexander
 *
 */
public class AllocationPort extends PortType {
	{
		indication(AllocateRequest.class);
		request(AllocateRequest.class);
		
		indication(AllocateDone.class);
		request(AllocateDone.class);
		
		indication(AllocateSubmit.class);
		request(AllocateSubmit.class);
	}
	
}
