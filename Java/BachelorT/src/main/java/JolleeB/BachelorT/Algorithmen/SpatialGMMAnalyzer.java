package JolleeB.BachelorT.Algorithmen;

import JolleeB.BachelorT.Utils.Conversion;

public class SpatialGMMAnalyzer {
	private int imgLength;
	private int imgWidth;
	private int imgHeight;
	private float learningRate = 0.01f;
	private float[] mean;
	private float[] variance;
	
	public SpatialGMMAnalyzer(int imgWidth, int imgHeight){
		this.imgLength = imgWidth*imgHeight;
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
		this.mean = new float[imgLength];
		this.variance = new float[imgLength];
		for(int i =0; i <this.imgLength; i++){
			this.mean[i] = 128;
			this.variance[i] = 3;
		}
	}
	
	public byte[] convertImage(byte[] imgIn){
		if(imgIn.length != imgLength) throw new IllegalArgumentException();
		byte[] imgOut = new byte[imgLength];
		float[] meanCopy = mean.clone();
		float[] varianceCopy = variance.clone();
		for(int y = 0; y < this.imgHeight; y++){
			for(int x =0; x < this.imgWidth; x++){
				int backgroundCounter = 0;
				int pos = y*this.imgWidth+x;
				int val = Conversion.byteToInt(imgIn[pos]);
				int stepSize =3;
				for(int lFactor =-stepSize; lFactor<=stepSize; lFactor+=stepSize){
					for(int kFactor =-stepSize; kFactor<=stepSize;kFactor+=stepSize){
						int l = lFactor*3;
						int k = kFactor*3;
						int newY = y+l;
						int newX = x+k;
						if(newY <0 || newY >=this.imgHeight)newY-=l;
						if(newX <0 || newX >=this.imgWidth )newX-=k;
						int newPos = newY*this.imgWidth+newX;
						backgroundCounter += Math.abs(val-mean[newPos])<variance[newPos]?1:0;
					}
				}
				//Update mean and variance values
				meanCopy[pos] = mean[pos] * (1-learningRate) + (learningRate * val);
				varianceCopy[pos] = (float)Math.sqrt(variance[pos] * (1-learningRate) + (learningRate *(val-mean[pos])*(val-mean[pos])));
				
				//Update Pixel in new Picture
				if(backgroundCounter>1) 	imgOut[pos] = imgIn[pos];
				else imgOut[pos] = (byte)mean[pos];
			}
		}
		mean = meanCopy;
		variance = varianceCopy;
		return imgOut;
	}
}
