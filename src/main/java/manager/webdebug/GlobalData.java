package manager.webdebug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalData {

	
	public HashMap<Integer, List<Integer>> neighborList = new HashMap<Integer, List<Integer>>();
    
    public HashMap<Integer, List<Integer>> fingerList = new HashMap<Integer, List<Integer>>();
    
    
    public void InsertNodeNeighbors(int selfAdr, List<Integer> neighbors)
    {
        neighborList.put(selfAdr, neighbors);
    }
	
}
