package JolleeB.BachelorT.Algorithmen;

import JolleeB.BachelorT.Utils.Conversion;

public class GMMAnalyzerMRF {
	private int imgLength;
	private int imgWidth;
	private int imgHeight;
	private float learningRate = 0.01f;
	private int mrfIterations = 3;
	private MRFOptimizer mrfOptimizer = new MRFOptimizer();
	private float[] mean;
	private float[] variance;
	private final float SQRT2PI = (float)Math.sqrt(Math.PI);
	
	public GMMAnalyzerMRF(int width, int height){
		this.imgLength = width*height;
		this.imgHeight = height;
		this.imgWidth = width;
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
		float[] probabilityField = new float[imgLength];
		for(int i = 0; i <imgLength; i++){
			int x = Conversion.byteToInt(imgIn[i]);
			float diff = x-mean[i];
			
			probabilityField[i] = diff*diff;
			probabilityField[i] /= -2*(variance[i]*variance[i]);
			//	Calculate rest
			probabilityField[i] = (float)Math.pow(Math.E, probabilityField[i]);
			probabilityField[i] /= variance[i]*SQRT2PI;
			
			//Update mean and variance values
			mean[i] = mean[i] * (1-learningRate) + (learningRate * x);
			variance[i] = (float)Math.sqrt(variance[i] * (1-learningRate) + (learningRate *(x-mean[i])*(x-mean[i])));
		}
		probabilityField = mrfOptimizer.optimizeRandomField(imgIn, probabilityField, imgWidth, imgHeight, mrfIterations);
		for(int i =0; i < imgLength; i++){
			if(probabilityField[i]>0.5) imgOut[i] = imgIn[i];
			else imgOut[i] = (byte) mean[i];
		}
		return imgOut;
	}
}
