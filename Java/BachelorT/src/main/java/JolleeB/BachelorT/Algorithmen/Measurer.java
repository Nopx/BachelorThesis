package JolleeB.BachelorT.Algorithmen;

public class Measurer {

	private long startTime=System.currentTimeMillis();
	private long memoryUsage =0;
	private int measurements =0;
	private long endTime = startTime;
	Runtime runtime = Runtime.getRuntime();
	
	public void startMeasurement(){
		this.startTime=System.currentTimeMillis();
		this.endTime = this.startTime;
	}
	
	public void measure(){
		this.measurements++;
		this.memoryUsage += runtime.totalMemory()-runtime.freeMemory();
	}
	
	public void stopMeasurement(){
		this.endTime = System.currentTimeMillis();
	}
	
	public double getAvgMemoryUsage(){
		try{
			return this.memoryUsage/(this.measurements*1024*1024);
		}
		catch(Exception e){
			return 0;
		}
	}
	
	public long getAvgTime(){
		try{
			return getTotalTime()/this.measurements;
		}
		catch(Exception e){
			return 0;
		}
	}
	
	public long getAvgTimePerPixel(int imgLength){
		try{
			return getTotalTime()/(this.measurements*imgLength);
		}
		catch(Exception e){
			return 0;
		}
	}
	
	public long getTotalTime(){
		return this.endTime-this.startTime;
	}
}
