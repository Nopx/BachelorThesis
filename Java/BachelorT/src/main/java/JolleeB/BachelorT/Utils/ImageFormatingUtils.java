package JolleeB.BachelorT.Utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageFormatingUtils {

	public static byte[] bufferedimageToByteArray(BufferedImage bi){
		return ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
	}
	
	 public static byte[] makeColorScale(byte[] imgIn){
		int length = imgIn.length;
		byte[] imgOut = new byte[length*3];
		for(int i = 0; i < length; i++){
			imgOut[i*3] = imgIn[i];
			imgOut[(i*3)+1] = imgIn[i];
			imgOut[(i*3)+2] = imgIn[i];
		}
		return imgOut;
	}
	
	 public static byte[] scaleIntToByteArray(int[] arrayIn){
		int length = arrayIn.length;
		byte[] arrayOut = new byte[length];
		for(int i=0; i<length; i++) {
			int value = arrayIn[i];
			if(value >255) value =255;
			if(value <0) value = 0;
			arrayOut[i] = (byte)(value);
		}
		return arrayOut;
	}
	
	 public static byte[] makeGrayScaleImage(byte[] imgIn){
		int length = imgIn.length/3;
		byte[] imgOut = new byte[length];
		for(int i = 0; i < length; i++){
			byte val1 = imgIn[i*3];
			byte val2 = imgIn[i*3+1];
			byte val3 = imgIn[i*3+2];
			int val1Int = Conversion.byteToInt(val1);
			int val2Int = Conversion.byteToInt(val2);
			int val3Int = Conversion.byteToInt(val3);
			int mixVal = (val1Int+val2Int+val3Int)/3;
			byte val = (byte)mixVal;
			imgOut[i] = val;
		}
		return imgOut;
	}
	 
	 public static byte[] halveGrayScaleImage(byte[] imgIn, int width, int height){
		 int length = imgIn.length;
		 byte[] imgOut = new byte[length/4];
		 int width2 =width/2;
		 int height2 =height/2;
		 for(int y = 0; y < height2; y++){
			 for(int x = 0; x < width2; x++){
				 int value = Conversion.byteToInt(imgIn[2*y*width+2*x])+Conversion.byteToInt(imgIn[(2*y+1)*width+2*x])
						 +Conversion.byteToInt(imgIn[2*y*width+2*x+1])+Conversion.byteToInt(imgIn[(2*y+1)*width+2*x+1]);
				 value /= 4;
				 imgOut[y*width2+x] = (byte)value;
			 }
		 }
		 return imgOut;
	 }
	 
	 public static byte[] doubleGrayScaleImage(byte[] imgIn, int width, int height){
		 int length = imgIn.length;
		 byte[] imgOut = new byte[length*4];
		 int width2 = width*2;
		 for(int y = 0; y < height; y++){
			 int pos = y*width;
			 for(int x = 0; x<width; x++){
				 byte p = imgIn[pos+x];
				 imgOut[2*y*width2+2*x]=p;
				 imgOut[(2*y+1)*width2+2*x]=p;
				 imgOut[2*y*width2+2*x+1]=p;
				 imgOut[(2*y+1)*width2+2*x+1]=p;
			 }
		 }
		 return imgOut;
	 }
	 
	 public static byte[] pyramidStepsDownRes(byte[] imgIn, int width, int height,int steps){
		 byte[] imgOut = halveGrayScaleImage(imgIn, width, height);
		 if(steps==0) return imgOut;
		 return pyramidStepsDownRes(imgOut, width/2, height/2, steps-1);
	 }
	 
	 public static byte[] pyramidStepsUpRes(byte[] imgIn, int width, int height,int steps){
		 byte[] imgOut = doubleGrayScaleImage(imgIn, width, height);
		 if(steps==0) return imgOut;
		 return pyramidStepsUpRes(imgOut, width*2, height*2, steps-1);
	 }
	 
	 public static byte[][] getpyramid(byte[] imgIn, int width, int height){
		 int pyramidHeight = (int)(Math.floor(Math.log10(width)/Math.log10(2))-1);
		 byte[][] imgOut = new byte[pyramidHeight][];
		 imgOut[0] = imgIn.clone();
		 return getpyramid(imgOut, width, height, pyramidHeight);
	 }
	 
	 private static byte[][] getpyramid(byte[][] imgIn, int width, int height, int pyramidHeight){
		 byte[][] imgOut = imgIn;
		 int newWidth = (int)Math.floor(width/2);
		 int newHeight = (int)Math.floor(height/2);
		 int stepsLeft = (int)(Math.floor(Math.log10(newWidth)/Math.log10(2))-1);
		 int stepNr = pyramidHeight - stepsLeft;
		 imgOut[stepNr] = new byte[(int)(newWidth*newHeight)];
		 for(int y = 0; y < newHeight*2; y++){
			 int pos = y*width;
			 for(int x = 0; x<newWidth*2; x++){
				 int index1 = pos+x;
				 int index2 = pos+x+1;
				 int index3 = pos+x+width;
				 int index4 = pos+x+width+1;
				 if(y >= height-1){
					 index3 = index1;
					 index4 = index2;
				 }
				 if(x >= width-1){ 
					 index2 = index1;
					 index4 = index3;
				 }
				 int value = imgIn[stepNr-1][index1]+imgIn[stepNr-1][index2]+ imgIn[stepNr-1][index3]+imgIn[stepNr-1][index4];
				 imgOut[stepNr][(((int)Math.floor(y/2)*newWidth))+((int)Math.floor(x/2))] = (byte)(value/4);
			 }
		 }
		 if(stepsLeft<=1) return imgOut;
		 return getpyramid(imgOut, newWidth, newHeight, pyramidHeight);
	 }
}
