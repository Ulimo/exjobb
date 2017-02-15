package manager.simulation;

import se.sics.kompics.simulator.network.identifier.Identifier;

public class SimNodeIdentifier implements Identifier {

	public final int nodeId;
	
	public SimNodeIdentifier(int nodeId) {
        this.nodeId = nodeId;
    }
	
	@Override
	public int partition(int nrPartitions) {
		return nodeId % nrPartitions;
	}
	
	 @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.nodeId;
        return hash;
    }
	 
	 
	 @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimNodeIdentifier other = (SimNodeIdentifier) obj;
        if (this.nodeId != other.nodeId) {
            return false;
        }
        return true;
    }

}
