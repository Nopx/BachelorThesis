package JolleeB.BachelorT.Algorithmen;

public class SmoothingAnalyzer {

	
	public byte[] convertImages(byte[][] imgsIn){
		int time = imgsIn.length;
		int length = imgsIn[0].length;
		byte[] imgOut = new byte[length];
		
		for(int i =0; i< length; i++){
			int avg = 0;
			for(int v = 0; v < time; v++){
				avg += imgsIn[v][i];
			}
			imgOut[i] = (byte)(avg/time);
		}
		return imgOut;
	}
}
