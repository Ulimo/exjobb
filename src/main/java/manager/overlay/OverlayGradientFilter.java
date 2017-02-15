package manager.overlay;

import se.sics.ktoolbox.gradient.GradientFilter;

public class OverlayGradientFilter implements GradientFilter<OverlayView> {

	@Override
	public boolean cleanOldView(OverlayView arg0, OverlayView arg1) {
		return false;
	}

	@Override
	public boolean retainOther(OverlayView arg0, OverlayView arg1) {
		return true;
	}

	
	
}
