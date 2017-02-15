package manager.resources;

public class ResourceVector {

	private double[] resources;
	
	public ResourceVector(double... resources){
		this.resources = resources;
	}
	
	public double getResource(int i){
		return resources[i];
	}
	
	public int size(){
		return resources.length;
	}
	
	public ResourceVector copy(){
		return new ResourceVector(resources);
	}
	
	/***
	 * Gives the maximum resource
	 * @return
	 */
	public double max(){
		double m = Double.MIN_VALUE;
		for(int i = 0; i < resources.length; i++){
			if(resources[i] > m){
				m = resources[i];
			}
		}
		return m;
	}
	
	/***
	 * Gives the minimum resource
	 * @return
	 */
	public double min(){
		double m = Double.MAX_VALUE;
		for(int i = 0; i < resources.length; i++){
			if(resources[i] < m){
				m = resources[i];
			}
		}
		return m;
	}
	
	public static ResourceVector divide(ResourceVector a, ResourceVector b){
		
		if(a.resources.length != b.resources.length){
			throw new RuntimeException("Resource vector lengths differ in division");
		}
		
		double[] newResources = new double[a.resources.length];
		for(int i = 0; i < a.size(); i++){
			newResources[i] = a.resources[i] / b.resources[i];
		}
		return new ResourceVector(newResources);
	}
	
	public ResourceVector divide(ResourceVector vector){
		return divide(this, vector);
	}
	
	public static ResourceVector multiply(ResourceVector a, ResourceVector b){
		if(a.resources.length != b.resources.length){
			throw new RuntimeException("Resource vector lengths differ in multiplication");
		}
		
		double[] newResources = new double[a.resources.length];
		for(int i = 0; i < a.size(); i++){
			newResources[i] = a.resources[i] * b.resources[i];
		}
		return new ResourceVector(newResources);
	}
	
	public ResourceVector multiply(ResourceVector vector){
		return multiply(this, vector);
	}
	
	public static ResourceVector add(ResourceVector a, ResourceVector b){
		if(a.resources.length != b.resources.length){
			throw new RuntimeException("Resource vector lengths differ in addition");
		}
		
		double[] newResources = new double[a.resources.length];
		for(int i = 0; i < a.size(); i++){
			newResources[i] = a.resources[i] + b.resources[i];
		}
		return new ResourceVector(newResources);
	}
	
	public static boolean largerThan(ResourceVector a, ResourceVector b){
		if(a.resources.length != b.resources.length){
			throw new RuntimeException("Resource vector lengths differ in larger than");
		}
		
		boolean larger = true;
		for(int i = 0; i < a.size(); i++){
			if(a.resources[i] < b.resources[i]){
				larger = false;
				break;
			}
		}
		
		return larger;
	}
	
	public boolean largerThan(ResourceVector vector){
		return largerThan(this, vector);
	}
	
	
	public ResourceVector add(ResourceVector vector){
		return add(this, vector);
	}
	
	public static ResourceVector sub(ResourceVector a, ResourceVector b){
		if(a.resources.length != b.resources.length){
			throw new RuntimeException("Resource vector lengths differ in subtraction");
		}
		
		double[] newResources = new double[a.resources.length];
		for(int i = 0; i < a.size(); i++){
			newResources[i] = a.resources[i] - b.resources[i];
		}
		return new ResourceVector(newResources);
	}
	
	public ResourceVector sub(ResourceVector vector){
		return sub(this, vector);
	}
	
	
	
}
