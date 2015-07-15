package JolleeB.BachelorT.Algorithmen;

import java.util.Arrays;

public class MedianAnalyzer {

	public byte[] convertImages(byte[][] imgsIn){
		int time = imgsIn.length;
		int timeMaxIndex = time-1;
		int length = imgsIn[0].length;
		byte[] imgOut = new byte[length];
		
		
		for(int i =0; i< length; i++){
			byte[] arr = new byte[time];
			for(int v = 0; v < time; v++){
				arr[v] = imgsIn[v][i];
			}
			Arrays.sort(arr);
			imgOut[i] = arr[(int)Math.floor(timeMaxIndex/2)];
		}
		return imgOut;
	}
}
