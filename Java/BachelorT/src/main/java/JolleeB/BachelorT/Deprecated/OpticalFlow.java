package JolleeB.BachelorT.Deprecated;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import JolleeB.BachelorT.Utils.ImageFormatingUtils;
import JolleeB.BachelorT.Utils.ImageUtils;
import JolleeB.BachelorT.Utils.VideoUtils;

public class OpticalFlow {

	public static int COUNTER =0;
	public ArrayList<BufferedImage> test(ArrayList<BufferedImage> framesInput){
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		final byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		final byte[][] pixVidOut;
		
		
		
		System.out.println("Extracting pixels..");
		for(int i = 0; i < framesInput.size(); i++){
			BufferedImage bi = framesInput.get(i);
			pixVidIn[i] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		}
		pixVidOut = pixVidIn.clone();
		
		//Make Gray Scale 
		//getLucasK...
		byte[] frame1 = ImageFormatingUtils.makeGrayScaleImage(pixVidIn[0]);
		byte[] frame2 = new byte[frame1.length];
		for(int i = 0; i< pixVidIn.length; i++){
			try{frame2 = ImageFormatingUtils.makeGrayScaleImage(pixVidIn[i+1]);}
			catch(Exception e){}
			frame1 = ImageUtils.medianFilterGrayScale(frame1, width, height, 3);
			frame1 = ImageFormatingUtils.scaleIntToByteArray(
					ImageUtils.filterGrayScale(frame1, width, height, new int[][]{	new int[]{1,1,1},
																					new int[]{1,1,1},
																					new int[]{1,1,1}})
					);
//			frame1 = ImageUtils.harrisCorners(frame1, width, height, 10);
//			frame1 = ImageFormatingUtils.scaleIntToByteArray(ImageUtils.filterGrayScale(frame1, width, height, new int[][]{new int[]{-1,0},
//																														new int[]{0,1}}));
						
//			frame2 = ImageUtils.medianFilter(frame2, width, height, 3);
			frame2 = ImageFormatingUtils.scaleIntToByteArray(
					ImageUtils.filterGrayScale(frame2, width, height, new int[][]{	new int[]{1,1,1},
																					new int[]{1,1,1},
																					new int[]{1,1,1}})
					);
//			frame2 = ImageUtils.harrisCorners(frame2, width, height, 1000000);
			int[][] movement = getLucasKanadeMotion(frame1, width, height, frame2, width, height);
			int[] movementSize = visualizeLucasKanade(movement);
			byte[] movementByte = ImageFormatingUtils.scaleIntToByteArray(movementSize);
//			movementByte = ImageUtils.binaryThresholding(movementByte, 20);
			movementByte = ImageFormatingUtils.makeColorScale(movementByte);
			//TODO gotta mask the movement image thing with the corners
			pixVidOut[i] = movementByte;
			frame1 = frame2;
		}
		
		

		System.out.println(COUNTER);
		
		System.out.println("Saving pixels..");
		for(int i = 0; i < framesInput.size(); i++){
			ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	        DataBuffer dataBuf = new DataBufferByte(pixVidOut[i], pixVidOut[i].length);
	        WritableRaster raster = Raster.createInterleavedRaster(dataBuf, width, height,width*3, 3, new int[]{2, 1, 0}, new Point());
	        //true oder false macht keinen unterschied hier?????!
			framesInput.set(i, new BufferedImage(cm, raster, false, null));
		}
		return framesInput;
	}
	

	public ArrayList<BufferedImage> simpleMotionDetection(ArrayList<BufferedImage> imgsIn){
		ArrayList<BufferedImage> imgsOut = imgsIn;
		int size = imgsIn.size();
		
//		imgsIn = VideoUtils.medianByWindow(imgsIn, 100);
		
//		byte[][] imgsInByte = VideoUtils.arrayListToByteArray(imgsIn);
//		imgsInByte = VideoUtils.makeGrayScale(imgsInByte);
//		imgsInByte = VideoUtils.gauss(imgsInByte, 4, 1, imgsIn.get(0).getWidth(), imgsIn.get(0).getHeight());
//		imgsInByte = VideoUtils.absoluteFramePerPixelVarianz(imgsInByte, VideoUtils.avgByteImage(imgsInByte),128);
////		byte[] initImage = imgsInByte[0];
////		int[][] der = VideoUtils.getDerivative(imgsInByte);
////		imgsInByte = VideoUtils.getAntiderivative(der, initImage);
//		imgsInByte = VideoUtils.makeColorScale(imgsInByte);
//		imgsIn = VideoUtils.byteArrayToArrayList(imgsInByte, imgsIn.get(0).getWidth(), imgsIn.get(0).getHeight());
		
//		return imgsIn;
		
		imgsIn = VideoUtils.avgByWindow(imgsIn, 2);
		BufferedImage b0 = imgsIn.get(0);
		int width = b0.getWidth();
		int height = b0.getHeight();
		int gaussKernelSize = 3;
		double gaussSigma = 5;
		byte[] frame1 = ((DataBufferByte)b0.getRaster().getDataBuffer()).getData();
		frame1 = ImageFormatingUtils.makeGrayScaleImage(frame1);
		frame1 = ImageUtils.gaussGrayScaleToByte(frame1,width,height,gaussKernelSize,gaussSigma);
		byte[] frame2;
		for(int i = 0; i < size-1; i++){
			BufferedImage bi = imgsIn.get(i+1);
			frame2 = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
			frame2 = ImageFormatingUtils.makeGrayScaleImage(frame2);
			frame2 = ImageUtils.gaussGrayScaleToByte(frame2,width,height,gaussKernelSize,gaussSigma);
			frame1 = ImageUtils.subtractGrayScaleImgByteToByteAbsoluteVal(frame1, frame2,0);
//			frame1 = ImageUtils.thresholding(frame1, 180, 255, 0);
			frame1 = ImageUtils.binaryThresholding(frame1, 2);
			frame1 = ImageUtils.medianFilterGrayScale(frame1, width, height, 7);
			frame1 = ImageFormatingUtils.makeColorScale(frame1);
			
			ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	        DataBuffer dataBuf = new DataBufferByte(frame1, frame1.length);
	        WritableRaster raster = Raster.createInterleavedRaster(dataBuf, width, height,width*3, 3, new int[]{2, 1, 0}, new Point());
	        //true oder false macht keinen unterschied hier?????!
	        imgsOut.set(i, new BufferedImage(cm, raster, false, null));
	        frame1 = frame2;
		}
		imgsOut.remove(imgsOut.size()-1);
		return imgsOut;
	}
	
	public ArrayList<BufferedImage> computeGlobalFlow(ArrayList<BufferedImage> imgsIn){
		ArrayList<BufferedImage> imgsOut = imgsIn;
		int size = imgsIn.size();
		imgsIn = VideoUtils.avgByWindow(imgsIn, 2);
		BufferedImage b0 = imgsIn.get(0);
		int width = b0.getWidth();
		int height = b0.getHeight();
		int gaussKernelSize = 3;
		double gaussSigma = 5;
		byte[] frame1 = ((DataBufferByte)b0.getRaster().getDataBuffer()).getData();
		frame1 = ImageFormatingUtils.makeGrayScaleImage(frame1);
		frame1 = ImageUtils.gaussGrayScaleToByte(frame1,width,height,gaussKernelSize,gaussSigma);
		byte[] frame2;
		for(int i = 0; i < size-1; i++){
			BufferedImage bi = imgsIn.get(i+1);
			frame2 = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
			frame2 = ImageFormatingUtils.makeGrayScaleImage(frame2);
			frame2 = ImageUtils.gaussGrayScaleToByte(frame2,width,height,gaussKernelSize,gaussSigma);
			VideoFlowUtils.globalFlowThreeStepSearchAdvancedLowThreshold(frame1, frame2, width, height, 16, 16);
//			frame1 = ImageFormatingUtils.scaleIntToByteArray(ImageUtils.addGrayScaleImgByte(frame1, VideoFlowUtils.globalFlowThreeStepSearchAdvancedPaint(frame1, frame2, width, height, 16, 16)));
//			frame1 = ImageFormatingUtils.scaleIntToByteArray(ImageUtils.filterGrayScale(frame1, width, height, new int[][]{new int[]{-1,1},new int[]{-1,1}}));
			frame1 = ImageFormatingUtils.makeColorScale(frame1);
			ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	        DataBuffer dataBuf = new DataBufferByte(frame1, frame1.length);
	        WritableRaster raster = Raster.createInterleavedRaster(dataBuf, width, height,width*3, 3, new int[]{2, 1, 0}, new Point());
	        //true oder false macht keinen unterschied hier?????!
	        imgsOut.set(i, new BufferedImage(cm, raster, false, null));
	        frame1 = frame2;
		}
		imgsOut.remove(imgsOut.size()-1);
		return imgsOut;
	}

	
	public int[][] getLucasKanadeMotion(byte[] pixImg1,int width1, int height1, byte[] pixImg2, int width2, int height2){
		int imgLength = pixImg1.length;
		byte[][] pyramid1 = ImageFormatingUtils.getpyramid(pixImg1, width1, height1);
		byte[][] pyramid2 = ImageFormatingUtils.getpyramid(pixImg2, width2, height2);
		if(!(imgLength == pixImg2.length && imgLength == width1*height1 && width1==width2 &&height1 == height2)) 
			throw new IllegalArgumentException();
		
		int[][] movementVector = null;
		
		for(int i =pyramid1.length-1; i >=0 ; i--){
			int width = width1;
			int height = height1;
			for(int j = 0; j< i; j++){
				width = (int)Math.floor(width/2);
				height = (int)Math.floor(height/2);
			}
			if(movementVector != null)
				movementVector = interpolateMovement(movementVector, width,height);
			int[][] resultImg = new int[width*height][2];
			byte[] frame1 = pyramid1[i];
			byte[] frame2 = pyramid2[i];
			int[] fx = ImageUtils.filterGrayScale(frame1, width, height, new int[][]{new int[]{-1,1},
																						new int[]{-1,1}});
			int[] fy = ImageUtils.filterGrayScale(frame1, width, height, new int[][]{new int[]{-1,-1},
																						new int[]{1,1}});
			int[] ft = lucasKanadeFTCalculator(frame1, frame2, width, height, movementVector);
			
			int fxft =0;
			int fxfy =0;
			int fyft =0;
			int fxSquares =0;
			int fySquares =0;
			int kernelSize =1;
			int center = (int)Math.floor(kernelSize/2);
			
			for(int y = 0; y< height; y++){
				int pos = y*width-1;
				for(int x = 0; x< width; x++){
					pos ++;
					for(int k = 0; k < kernelSize; k++){
						for(int l = 0; l < kernelSize; l++){
							int kIndex = k-center +x;
							int lIndex = l-center +y;
							if(kIndex<0) 			kIndex =0;
							if(kIndex>=width) 		kIndex =width-1;
							if(lIndex<0) 			lIndex =0;
							if(lIndex>=height) 		lIndex =height-1;
							
							//Forumla from http://crcv.ucf.edu/courses/CAP5415/Fall2012/Lecture-6b-OpticalFlow.pdf
							int posKernel = lIndex*width+kIndex;
							fxft += fx[posKernel]*ft[posKernel];
							fxfy += fx[posKernel]*fy[posKernel];
							fyft += fy[posKernel]*ft[posKernel];
							
							fxSquares += Math.pow(fx[posKernel], 2);
							fySquares += Math.pow(fy[posKernel], 2);
						}
					}
					int divisor = fxSquares*fySquares - (int)Math.pow(fxfy, 2);
					if(divisor ==0){
						//TODO
						COUNTER ++;
						//u formula
						resultImg[pos][0] = 0;
						//v formula
						resultImg[pos][1] = 0;
						
					}
					else{
						//u formula
						resultImg[pos][0] = ((fySquares*fxft)+(fxfy*fyft))/divisor;
						//v formula
						resultImg[pos][1] = ((fxft*fxfy)-(fxSquares*fyft))/divisor;
					}
				}
			}
			if(movementVector!=null)
				movementVector = lucasKanadeMovementVectorAdder(resultImg, movementVector,width,height);
			else
				movementVector = resultImg;
		}
		return movementVector;
	}
	
	public int[] visualizeLucasKanade(int[][] lk){
		int length = lk.length;
		int[] result = new int[length];
		for(int i = 0; i < length ; i++){
//			if(lk[i][0] >0 && lk[i][1] > 0)
				result[i] = (int)Math.sqrt(Math.pow(lk[i][0], 2)+Math.pow(lk[i][1], 2));
		}
		return result;
	}
	
	public int[] lucasKanadeFTCalculator(byte[] frame1, byte[] frame2, int width, int height, int[][] movementVector){
		if(movementVector == null) movementVector = new int[width*height][2];
		int length = frame1.length;
		int[] frameOut = new int[length];
		int filtersize = 2;
		if(length!=frame2.length|| length != width*height) throw new IllegalArgumentException();
		for(int y = 0 ; y < height; y++){
			int pos = y*width;
			for(int x = 0; x < width; x++){
				for(int l = 0; l<filtersize; l++){
					for(int k = 0; k<filtersize; k++){
						int frame1k = x-k;
						if(frame1k < 0) frame1k=0;
						
						int frame1l = y-l;
						if(frame1l <0 ) frame1l=0;
						
						int frame2k = frame1k+2*movementVector[pos+x][0];
						if(frame2k < 0) frame2k = 0;
						if(frame2k >=width) frame2k = width-1;
						int frame2l = frame1l+2*movementVector[pos+x][1];
						if(frame2l < 0) frame2l = 0;
						if(frame2l >=height) frame2l = height-1;
						
						frameOut[pos+x] += -frame1[frame1l*width+frame1k] + frame2[frame2l*width+frame2k];
					}
				}
				frameOut[pos+x] /= 8;
			}
		}
		return frameOut;
	}
	
	private int[][] lucasKanadeMovementVectorAdder(int[][] movementVectorNewIn, int[][] movementVectorOldIn, int width, int height){
		int length = movementVectorNewIn.length;
		if(length != width*height) throw new IllegalArgumentException();
		int[][] movementVectorOut = new int[length][2];
		for(int y = 0; y < height; y++){
			int yIn = (int)Math.floor(y/2);
			int pos = y*width;
			for(int x = 0; x< width; x++){
				movementVectorOut[pos+x][0] = movementVectorNewIn[pos+x][0]+movementVectorOldIn[pos+x][0]*2;
				movementVectorOut[pos+x][1] = movementVectorNewIn[pos+x][1]+movementVectorOldIn[pos+x][1]*2;
			}
		}
		return movementVectorOut;
	}
	
	private int[][] interpolateMovement(int[][] movementIn, int width, int height){
		int widthIn = (int)Math.floor(width/2);
		int heightIn = (int)Math.floor(height/2);
		int[][] movementOut = new int[width*height][2];
		
		for(int y = 0; y < height; y+=2){
			for(int x = 0; x < width; x+=2){
				int pos = y*width+x;
				int xIn = Math.floorDiv(x, 2);
				int yIn = Math.floorDiv(y, 2);
				int posIn = yIn*widthIn+xIn;
				if(posIn < widthIn * heightIn){
					movementOut[pos][0] = 2*movementIn[posIn][0];
					movementOut[pos][1] = 2*movementIn[posIn][1];
					if(xIn < widthIn-2){
						movementOut[pos+1][0] = (movementIn[posIn][0]+movementIn[posIn+1][0]);
						movementOut[pos+1][1] = (movementIn[posIn][1]+movementIn[posIn+1][1]);
					}
				}
			}
			y++;
			if(y >= height) break;
			for(int x = 0; x < width; x+=2){
				int pos = y*width+x;
				int xIn = Math.floorDiv(x, 2);
				int yIn = Math.floorDiv(y, 2);
				int posIn = yIn*widthIn+xIn;
				if(yIn < heightIn-2){
					movementOut[pos][0] = (movementIn[posIn][0]+movementIn[posIn+widthIn][0]);
					movementOut[pos][1] = (movementIn[posIn][1]+movementIn[posIn+widthIn][1]);
					if(xIn < widthIn-2){
						movementOut[pos+1][0] = (movementIn[posIn][0]+movementIn[posIn+widthIn+1][0]);
						movementOut[pos+1][1] = (movementIn[posIn][1]+movementIn[posIn+widthIn+1][1]);
					}
				}
			}
		}
		return movementOut;
	}
}
