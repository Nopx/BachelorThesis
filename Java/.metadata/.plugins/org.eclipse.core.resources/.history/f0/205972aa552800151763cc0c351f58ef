package JolleeB.BachelorT.Algorithmen;


import JolleeB.BachelorT.Utils.Conversion;

public class GMMAnalyzer {

	private int imgLength;
	private float learningRate = 0.01f;
	private float[] mean;
	private float[] variance;
	
	public GMMAnalyzer(int imgLength){
		this.imgLength = imgLength;
		this.mean = new float[imgLength];
		this.variance = new float[imgLength];
		for(int i =0; i <imgLength; i++){
			this.mean[i] = 0;
			this.variance[i] = 3;
		}
	}
	
	public byte[] convertImage(byte[] imgIn){
		if(imgIn.length != imgLength) throw new IllegalArgumentException();
		byte[] imgOut = new byte[imgLength];
		for(int i = 0; i <imgLength; i++){
			int x = Conversion.byteToInt(imgIn[i]);
			boolean isForeground = Math.abs(x-mean[i])>variance[i];
			
			//Update mean and variance values
			mean[i] = mean[i] * (1-learningRate) + (learningRate * x);
			variance[i] = (float)Math.sqrt(variance[i] * (1-learningRate) + (learningRate *(x-mean[i])*(x-mean[i])));
			
			//Update Pixel in new Picture
			if(isForeground) imgOut[i] = (byte)mean[i];	
			else imgOut[i] = imgIn[i];
		}
		return imgOut;
	}
	
}