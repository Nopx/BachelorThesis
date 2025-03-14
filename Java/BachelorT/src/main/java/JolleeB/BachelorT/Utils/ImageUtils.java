package JolleeB.BachelorT.Utils;

import java.util.Arrays;

public class ImageUtils {
	
	
	public static void paintWhite(byte[] img, int x1, int y1, int x2, int y2, int width, int height){
		int vecX = x2-x1;
		int vecY = y2-y1;
		double runX=x1;
		double runY=y1;
		double compar;
		if(vecX==0&&vecY==0) {
		}
		else if(vecX==0) {
			compar = 0;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runY++;
			}
		}
		else if(vecY==0) {
			compar = 0;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runX++;
			}
		}
		else if(vecX<vecY) {
			compar = vecX/vecY;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runY++;
				runX+=compar;
			}
		}
		else if(vecX>vecY) {
			compar = vecX/vecY;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runX++;
				runY+=compar;
			}
		}
		else if(vecX>vecY && vecX<0) {
			compar = vecX/vecY;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runY--;
				runX-=compar;
			}
		}
		else if(vecX<vecY && vecY <0) {
			compar = vecX/vecY;
			while((int)Math.floor(runX) != x2 || (int)Math.floor(runY) != y2){
				int pos = (int)Math.floor(runY)*width+(int)Math.floor(runX);
				img[pos] = (byte)255;
				runX--;
				runY-=compar;
			}
		}
	}
	
	public static int[] filterGrayScale(byte[] imgIn,int imgWidth,int imgHeight, int[][] kernel){
		int imgLength = imgIn.length;
		int[] imgOut = new int[imgLength];
		int kernelHeight = kernel.length;
		int kernelWidth = kernel[0].length;
		int centerX = (int)Math.floor(kernelWidth/2);
		int centerY = (int)Math.floor(kernelHeight/2);
		for(int[] sA : kernel){
			if(sA.length != kernelWidth){
				throw new IllegalArgumentException();
			}
		}
		for(int y = 0; y< imgHeight; y++){
			int pos = y*imgWidth-1;
			for(int x = 0; x< imgWidth; x++){
				pos ++;
				for(int k = 0; k< kernelWidth; k++){
					for(int l = 0; l <kernelHeight; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=imgWidth) 	kIndex =imgWidth-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=imgHeight) 	lIndex =imgHeight-1;
						
						int posKernel = lIndex*imgWidth+kIndex;
						int te = kernel[k][l]*imgIn[posKernel];
						imgOut[pos] += (int)(te);
					}
				}
			}
		}
		return imgOut;
	}
	
	public static byte[] gaussGrayScaleToByte(byte[] imgIn,int imgWidth,int imgHeight, int kernelSize, double sigma){
		double[][] kernel = new double[kernelSize][kernelSize];
		int imgLength = imgIn.length;
		byte[] imgOut = new byte[imgLength];
		int centerX = (int)Math.floor(kernelSize/2);
		double sigmaSqaured = sigma*sigma;
		double formulaPart1 = (1/(2*Math.PI*sigmaSqaured));
		double sigmaSquaredDoubled = 2* sigmaSqaured;
		double factor = 1/(formulaPart1*Math.pow(Math.E,-(centerX*centerX+centerX*centerX)/sigmaSquaredDoubled));
		double divisor = 0;
		for(int k = 0; k< kernelSize; k++){
			for(int l = 0; l <kernelSize; l++){
				int kIndex = k-centerX;
				int lIndex = l-centerX;
				
				double value = factor*(formulaPart1*Math.pow(Math.E,-(kIndex*kIndex+lIndex*lIndex)/sigmaSquaredDoubled));
				kernel[k][l] = value;
				divisor += value;
			}
		}
		for(int y = 0; y< imgHeight; y++){
			int pos = y*imgWidth-1;
			for(int x = 0; x< imgWidth; x++){
				pos ++;
				double value= 0;
				for(int k = 0; k< kernelSize; k++){
					for(int l = 0; l <kernelSize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerX +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=imgWidth) 	kIndex =imgWidth-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=imgHeight) 	lIndex =imgHeight-1;
						
						int posKernel = lIndex*imgWidth+kIndex;
						double te = kernel[k][l]*Conversion.byteToInt(imgIn[posKernel]);
						value += te;
					}
				}
				int val =(int)(Math.floor(value/divisor));
				if(val<0) val = 0;
				if(val>255)val = 255;
				imgOut[pos] = (byte)val;
			}
		}
		return imgOut;
	}
	
	public static byte[] simpleBinaryMask(byte[] imgIn, int imgWidth, int imgHeight, byte[] mask, int maskWidth, int maskHeight){
		byte[] imgOut = new byte[imgIn.length];
		for(int y = 0; y < maskHeight; y++){
			for(int x = 0; x < maskWidth; x++){
				int maskPos = y*maskWidth+x;
				int imgPos = y*imgWidth+x;
				if(mask[maskPos] == 0)	imgOut[imgPos] = 0;
				else					imgOut[imgPos] = imgIn[imgPos];
			}
		}
		return imgOut;
	}
	
	public static byte[] simpleBinaryReverseMask(byte[] imgIn, int imgWidth, int imgHeight, byte[] mask, int maskWidth, int maskHeight){
		byte[] imgOut = new byte[imgIn.length];
		for(int y = 0; y < maskHeight; y++){
			for(int x = 0; x < maskWidth; x++){
				int maskPos = y*maskWidth+x;
				int imgPos = y*imgWidth+x;
				if(mask[maskPos] != 0)	imgOut[imgPos] = 0;
				else					imgOut[imgPos] = imgIn[imgPos];
			}
		}
		return imgOut;
	}
	
	
	public static byte[] simpleCombineReverseMask(byte[] img1,byte[] img2, byte[] mask, int width, int height){
		byte[] imgOut = new byte[img1.length];
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pos = y*width+x;
				if(mask[pos] == 0)	imgOut[pos] = img2[pos];
				else					imgOut[pos] = img1[pos];
			}
		}
		return imgOut;
	}
	
	
	public static byte[] addGrayScaleImgByteToByte(byte[] img1, byte[] img2){
		int img1Length = img1.length;
		if(img1Length != img2.length) throw new IllegalArgumentException();
		byte[] imgOut = new byte[img1Length];
		for(int i = 0; i < img1Length;i++){ 
			int val = Conversion.byteToInt(img1[i])+ Conversion.byteToInt(img2[i]);
			if(val > 255) val = 255;
			imgOut[i] = (byte) val;
		}
		return imgOut;
	}
	public static int[] addGrayScaleImgByteToInt(byte[] img1, byte[] img2){
		int img1Length = img1.length;
		int[] img1Int = new int[img1Length];
		for(int i = 0; i < img1Length;i++) img1Int[i] = Conversion.byteToInt(img1[i]);
		
		int img2Length = img2.length;
		int[] img2Int = new int[img2Length];
		for(int i = 0; i < img2Length;i++) img2Int[i] = Conversion.byteToInt(img2[i]);
		
		return addGrayScaleImgInt(img1Int, img2Int);
	}
	public static int[] addGrayScaleImgInt(int[] img1, int[] img2){
		int imgLength = img1.length;
		if(img2.length != imgLength) throw new IllegalArgumentException();
		int[] resultImg = new int[imgLength];
		for(int i = 0; i < imgLength; i++) resultImg[i] = img1[i]+img2[i];
		return resultImg;
	}
	
	public static int[] addGrayScaleImgInt(byte[] img1, int[] img2){
		int length = img1.length;
		int[] img1Int = new int[length];
		for(int i = 0; i < length; i++){
			img1Int[i] = Conversion.byteToInt(img1[i]);
		}
		return addGrayScaleImgInt(img1Int, img2);
	}

	public static byte[] subtractGrayScaleImgByteToByte(byte[] img1, byte[] img2, int offset){
		int img1Length = img1.length;
		int[] img1Int = new int[img1Length];
		for(int i = 0; i < img1Length;i++) img1Int[i] = (int)img1[i];
		
		int img2Length = img2.length;
		int[] img2Int = new int[img2Length];
		for(int i = 0; i < img2Length;i++) img2Int[i] = (int)img2[i];
		
		int[] img = subtractGrayScaleImgInt(img1Int, img2Int);
		
		byte[] imgOut = new byte[img1Length];
		int imgLength = img.length;
		for(int i = 0; i < imgLength;i++) imgOut[i] = (byte)(img[i]+offset);
		
		return imgOut;
	}
	
	public static byte[] subtractGrayScaleImgByteToByteAbsoluteVal(byte[] img1, byte[] img2, int offset){
		int img1Length = img1.length;
		int[] img1Int = new int[img1Length];
		for(int i = 0; i < img1Length;i++) img1Int[i] = (int)img1[i];
		
		int img2Length = img2.length;
		int[] img2Int = new int[img2Length];
		for(int i = 0; i < img2Length;i++) img2Int[i] = (int)img2[i];
		
		int[] img = subtractGrayScaleImgInt(img1Int, img2Int);
		
		byte[] imgOut = new byte[img1Length];
		int imgLength = img.length;
		for(int i = 0; i < imgLength;i++) imgOut[i] = (byte)(Math.abs(img[i])+offset);
		
		return imgOut;
	}
	
	public static int[] subtractGrayScaleImgByte(byte[] img1, byte[] img2){
		int img1Length = img1.length;
		int[] img1Int = new int[img1Length];
		for(int i = 0; i < img1Length;i++) img1Int[i] = (int)img1[i];
		
		int img2Length = img2.length;
		int[] img2Int = new int[img2Length];
		for(int i = 0; i < img2Length;i++) img2Int[i] = (int)img2[i];
		
		return subtractGrayScaleImgInt(img1Int, img2Int);
	}
	public static int[] subtractGrayScaleImgInt(int[] img1, int[] img2){
		int imgLength = img1.length;
		if(img2.length != imgLength) throw new IllegalArgumentException();
		int[] resultImg = new int[imgLength];
		for(int i = 0; i < imgLength; i++) resultImg[i] = img1[i]-img2[i];
		return resultImg;
	}
	
	public static int[] meanGrayScaleImgByte(byte[] img1, byte[] img2){
		int img1Length = img1.length;
		int[] img1Int = new int[img1Length];
		for(int i = 0; i < img1Length;i++) img1Int[i] = (int)img1[i];
		
		int img2Length = img2.length;
		int[] img2Int = new int[img2Length];
		for(int i = 0; i < img2Length;i++) img2Int[i] = (int)img2[i];
		
		return meanGrayScaleImgInt(img1Int, img2Int);
	}
	public static int[] meanGrayScaleImgInt(int[] img1, int[] img2){
		int imgLength = img1.length;
		if(img2.length != imgLength) throw new IllegalArgumentException();
		int[] resultImg = new int[imgLength];
		for(int i = 0; i < imgLength; i++) resultImg[i] = (img1[i]+img2[i])/2;
		return resultImg;
	}
	
	public static byte[] binaryThresholding(byte[] imgIn, int threshold){
		int length = imgIn.length;
		byte[] imgOut = new byte[length];
		for(int i = 0; i < length; i++) imgOut[i] = (byte)(imgIn[i]<threshold?0:255);
		return imgOut;
	}
	
	public static int[] thresholding(int[] imgIn, int fromThreshold, int toThreshold, int value){
		int length = imgIn.length;
		int[] imgOut = new int[length];
		for(int i = 0; i < length; i++) {
			int x = imgIn[i];
			imgOut[i] = (x>fromThreshold&&x<toThreshold?value:x);
		}
		return imgOut;
	}
	
	public static byte[] thresholding(byte[] imgIn, int fromThreshold, int toThreshold, int value){
		int length = imgIn.length;
		byte[] imgOut = new byte[length];
		for(int i = 0; i < length; i++) {
			int x = Conversion.byteToInt(imgIn[i]);
			imgOut[i] = (byte)(x>fromThreshold&&x<toThreshold?value:x);
		}
		return imgOut;
	}
	
	public static byte[] medianFilterGrayScale(byte[] imgIn, int width, int height, int kernelsize){
		byte[] imgOut = new byte[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				byte[] values = new byte[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[(int)values.length/2];
			}
		}
		return imgOut;
	}
	public static byte[] medianFilterColorScale(byte[] imgIn, int width, int height, int kernelsize){
		byte[] imgOut = new byte[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				byte[] values = new byte[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = (k-centerX)*3 +x;
						int lIndex = (l-centerY)*3 +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[(int)values.length/2];
			}
		}
		return imgOut;
	}
	
	public static byte[] maximumFilter(byte[] imgIn, int width, int height, int kernelsize){
		byte[] imgOut = new byte[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				byte[] values = new byte[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[0];
			}
		}
		return imgOut;
	}
	
	public static int[] medianFilter(int[] imgIn, int width, int height, int kernelsize){
		int[] imgOut = new int[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int[] values = new int[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[(int)values.length/2];
			}
		}
		return imgOut;
	}
	
	public static byte[] minimumFilter(byte[] imgIn, int width, int height, int kernelsize){
		byte[] imgOut = new byte[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				byte[] values = new byte[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[values.length-1];
			}
		}
		return imgOut;
	}
	
	public static int[] minimumFilter(int[] imgIn, int width, int height, int kernelsize){
		int[] imgOut = new int[imgIn.length];
		int centerX = (int)Math.floor(kernelsize/2);
		int centerY = (int)Math.floor(kernelsize/2);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int[] values = new int[kernelsize*kernelsize];
				for(int k = 0; k< kernelsize; k++){
					for(int l = 0; l <kernelsize; l++){
						int kIndex = k-centerX +x;
						int lIndex = l-centerY +y;
						if(kIndex<0) 			kIndex =0;
						if(kIndex>=width) 		kIndex =width-1;
						if(lIndex<0) 			lIndex =0;
						if(lIndex>=height) 		lIndex =height-1;
						
						
						int posKernel = lIndex*width+kIndex;
						values[k*kernelsize+l] = imgIn[posKernel];
					}
				}
				Arrays.sort(values);
				imgOut[y*width+x] = values[values.length-1];
			}
		}
		return imgOut;
	}
	
	public static int[] divideIntImageBy(int divisor, int[] img){
		int length = img.length;
		for(int i = 0; i < length; i++){
			img[i] /= divisor;
		}
		return img;
	}
	
	public static byte[] divideByteImageBy(int divisor, byte[] img){
		int length = img.length;
		for(int i = 0; i < length; i++){
			img[i] /= divisor;
		}
		return img;
	}
	
	public static byte[] harrisCorners(byte[] imgIn, int width, int height, int threshold){
		int length = imgIn.length;
		byte[] imgOut = new byte[length];
		
		int filtersize = 2;
		for(int y = 0; y< height; y++){
			if(y>=filtersize && y < height-filtersize){
				int pos = y*width;
				for(int x = 0; x < width; x++){
					if(x>=filtersize && x < width-filtersize){
						int difX = imgIn[pos+x]-imgIn[pos+x-1];
						int difY = imgIn[pos+x]-imgIn[pos+x-width];
						int difMix = difX*difY;
						difX *= difX;
						difY *= difY;
						//Formel
						int val = (int)((difX * difY + (difMix * difMix)) + (0.5*((difX + difY)*(difX + difY))));
						if(val > threshold) imgOut[pos+x] = (byte)255;
					}
				}
			}
		}
		return imgOut;
	}
	
	public static byte[] dilateBinary(byte[] imgIn, int width, int height,int times){
		int length = imgIn.length;
		byte[] imgOut = imgIn.clone();
		byte[] imgTemp = new byte[length];
		
		for(int i = 0; i < times; i++){
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					int pos = y*width+x;
					int val = Conversion.byteToInt(imgOut[pos]);
					if(val >0){
						for(int k = 0; k <3; k++){
							int yTemp = k-1+y;
							if(yTemp<0) yTemp =0;
							if(yTemp>=height) yTemp = height-1;  
							for(int l = 0; l<3; l++){
								int xTemp = l-1+x;
								if(xTemp<0) xTemp =0;
								if(xTemp>=width) xTemp = width-1;
								//make white
								imgTemp[yTemp*width+xTemp] = (byte)255;
							}
						}
					}
				}
			}
			imgOut = imgTemp.clone();
		}
		return imgOut;
	}
	
	public static byte[] eroseBinary(byte[] imgIn, int width, int height,int times){
		int length = imgIn.length;
		byte[] imgOut = imgIn.clone();
		byte[] imgTemp = new byte[length];
		
		for(int i = 0; i < times; i++){
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					int pos = y*width+x;
					int val = Conversion.byteToInt(imgOut[pos]);
					imgTemp[pos] = 0;
					if(val >0){
						boolean toErase = false;
						for(int k = 0; k <3; k++){
							if(toErase)
								break;
							int yTemp = k-1+y;
							if(yTemp<0) yTemp =0;
							if(yTemp>=height) yTemp = height-1;  
							for(int l = 0; l<3; l++){
								if(toErase)
									break;
								int xTemp = l-1+x;
								if(xTemp<0) xTemp =0;
								if(xTemp>=width) xTemp = width-1;
								//Wenns einen schwarzen Nachbarn hat, dann lösche es
								toErase = imgOut[yTemp*width+xTemp]==0;
							}
						}
						if(!toErase){
							imgTemp[pos] = (byte)255;
						}
					}
				}
			}
			imgOut = imgTemp.clone();
		}
		return imgOut;
	}
	

}
