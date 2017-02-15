package manager.overlay;

import java.util.Comparator;

public class OverlayViewComparator implements Comparator<OverlayView> {

	@Override
	public int compare(OverlayView a1, OverlayView a2) {
		
		if(a1.nodeId.equals(a2.nodeId))
			return 0;
		
		return a1.nodeId.compareTo(a2.nodeId);
		
	}

	
}
