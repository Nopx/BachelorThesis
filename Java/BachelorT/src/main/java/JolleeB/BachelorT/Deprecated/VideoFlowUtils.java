package JolleeB.BachelorT.Deprecated;

import JolleeB.BachelorT.Utils.Conversion;
import JolleeB.BachelorT.Utils.ImageFormatingUtils;
import JolleeB.BachelorT.Utils.ImageUtils;

public class VideoFlowUtils {
	public static int DEBUGCOUNTER =0;

	public static int[] globalFlowThreeStepSearch(byte[] imgIn1, byte[] imgIn2, int width, int height, int blockWidth, int blockHeight){
		
		int blockAmountY = (int)Math.floor(height/blockHeight);
		int blockAmountX = (int)Math.floor(width/blockWidth);
		int centerX = (int)Math.floor(blockWidth/2);
		int centerY = (int)Math.floor(blockHeight/2);
		int[] coords = new int[2];
		byte[] imgOut = new byte[imgIn1.length];
		for(int y = 0; y < blockAmountY; y++){
			for(int x = 0; x < blockAmountX; x++){
				
				int block1x = (x*blockWidth+centerX);
				int block1y = (y*blockHeight+centerY);
				int stepSize = 8;
				int[] resultCoords = threeStepSearch(imgIn1, imgIn2, width, height, block1x, block1y, blockWidth, blockHeight, stepSize, imgOut);
				coords[0] += resultCoords[0];
				coords[1] += resultCoords[1];
			}
		}
		coords[0] /= blockAmountX*blockAmountY;
		coords[1] /= blockAmountX*blockAmountY;
		System.out.println("X: "+coords[0]);
		System.out.println("Y: "+coords[1]);
		return coords;
	}
	
public static byte[] globalFlowThreeStepSearchPaint(byte[] imgIn1, byte[] imgIn2, int width, int height, int blockWidth, int blockHeight){
		
		int blockAmountY = (int)Math.floor(height/blockHeight);
		int blockAmountX = (int)Math.floor(width/blockWidth);
		int centerX = (int)Math.floor(blockWidth/2);
		int centerY = (int)Math.floor(blockHeight/2);
		int[] coords = new int[2];
		byte[] imgOut = new byte[imgIn1.length];
		for(int y = 2; y < blockAmountY-2; y++){
			for(int x = 2; x < blockAmountX-2; x++){
				
				int block1x = (x*blockWidth+centerX);
				int block1y = (y*blockHeight+centerY);
				int stepSize = blockWidth;
				int[] resultCoords = threeStepSearch(imgIn1, imgIn2, width, height, block1x, block1y, blockWidth, blockHeight, stepSize, imgOut);
				coords[0] += resultCoords[0];
				coords[1] += resultCoords[1];
				for(int i = -1; i <=1; i++){
					for(int j = -1; j<=1; j++){
						imgOut[(block1y+i)*width+block1x+j] = 0;
						imgOut[(block1y+resultCoords[1]+i)*width+(block1x+resultCoords[0]+j)] = -1;
					}
				}
			}
		}
		coords[0] /= blockAmountX*blockAmountY;
		coords[1] /= blockAmountX*blockAmountY;
		return imgOut;
	}

	public static byte[] globalFlowThreeStepSearchAdvancedPaint(byte[] imgIn1, byte[] imgIn2, int width, int height, int blockWidth, int blockHeight){
		byte[] imgGray1 = ImageFormatingUtils.makeGrayScaleImage(imgIn1);
		byte[] imgGray2 = ImageFormatingUtils.makeGrayScaleImage(imgIn2);
		blockWidth *=3;
		int blockAmountY = (int)Math.floor(height/blockHeight);
		int blockAmountX = (int)Math.floor(width/blockWidth);
		int centerX = (int)Math.floor(blockWidth/2);
		int centerY = (int)Math.floor(blockHeight/2);
		int[] coords = new int[2];
		byte[] imgOut = new byte[imgIn1.length];
		for(int y = 2; y < blockAmountY-2; y++){
			for(int x = 2; x < blockAmountX-2; x++){
				
				int block1x = (x*blockWidth+centerX);
				int block1y = (y*blockHeight+centerY);
				int stepSize = blockWidth;
				//TODO ALERT DIE ZAHL DA HINTEN BESCHREIBT WIE LANGSAM DIE BEWEGUNG SEIN KANN!!! JE KLEINER, DESTO LANGSAMER WIRDS NOCH ALS BEWEGUNG ERKANNT
				int[] resultCoords = threeStepSearchAdvanced(imgGray1, imgGray2, width/3, height/3, block1x, block1y, blockWidth, blockHeight, stepSize, imgOut, 20);
				coords[0] += resultCoords[0];
				coords[1] += resultCoords[1];
				for(int i = -1; i <=1; i++){
					for(int j = -1; j<=1; j++){
						imgOut[(block1y+i)*width+block1x+j] = 0;
						int coord = (block1y+resultCoords[1]+i)*width+(block1x+resultCoords[0]+j);
						try{
							imgOut[coord] = -1;
						}
						catch(ArrayIndexOutOfBoundsException aie){
						
						}
					}
				}
			}
		}
		coords[0] /= blockAmountX*blockAmountY;
		coords[1] /= blockAmountX*blockAmountY;
		return imgOut;
	}
	
	public static int[] globalFlowThreeStepSearchAdvancedLowThreshold(byte[] imgIn1, byte[] imgIn2, int width, int height, int blockWidth, int blockHeight){
		int THRESHOLD = 1000;
		int blockcounter = 0;
		int blockAmountY = (int)Math.floor(height/blockHeight);
		int blockAmountX = (int)Math.floor(width/blockWidth);
		int centerX = (int)Math.floor(blockWidth/2);
		int centerY = (int)Math.floor(blockHeight/2);
		int[] coords = new int[2];
		byte[] imgOut = new byte[imgIn1.length];
		for(int y = 2; y < blockAmountY-2; y++){
			for(int x = 2; x < blockAmountX-2; x++){
				
				int block1x = (x*blockWidth+centerX);
				int block1y = (y*blockHeight+centerY);
				int stepSize = blockHeight;
				//TODO ALERT DIE ZAHL DA HINTEN BESCHREIBT WIE LANGSAM DIE BEWEGUNG SEIN KANN!!! JE KLEINER, DESTO LANGSAMER WIRDS NOCH ALS BEWEGUNG ERKANNT
				int[] resultCoords = threeStepSearchAdvanced(imgIn1, imgIn2, width, height, block1x, block1y, blockWidth, blockHeight, stepSize, imgOut, 0);
				if(Math.abs(resultCoords[0])<THRESHOLD && Math.abs(resultCoords[1])<THRESHOLD){
					coords[0] += resultCoords[0];
					coords[1] += resultCoords[1];
					blockcounter++;
				}
			}
		}
		coords[0] /= blockcounter;
		coords[1] /= blockcounter;
		System.out.println("X:"+coords[0]+" Y:"+coords[1]);
		return coords;
	}
	
	private static int[] threeStepSearchAdvanced(byte[] imgIn1, byte[]imgIn2, int width, int height, int block1x, int block1y,
			int blockWidth, int blockHeight, int stepSize, byte[] imgOut, int mseThreshold){
		int steps = 3;
		//returnVal: int[0] = xBeste; int[1] = yBeste;
				int block1xOrig = block1x;
				int block1yOrig = block1y;
				int[] coordinatesBest = new int[]{block1x,block1y};
				double oldMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block1x, block1y, blockWidth, blockHeight);
				if(oldMSE < mseThreshold){
					coordinatesBest[0] -= block1xOrig;
					coordinatesBest[1] -= block1yOrig;
					return coordinatesBest;
				}
				for(int i = 0; i < steps; i++){	
					for(int y = -stepSize; y <= stepSize; y+=stepSize){
						for(int x = -stepSize; x <= stepSize; x+=stepSize){
							int block2x = block1x+x;
							int block2y = block1y+y;
							double newMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block2x, block2y, blockWidth, blockHeight);
							if(newMSE < oldMSE) {
								oldMSE = newMSE;
								coordinatesBest[0] = block2x;
								coordinatesBest[1] = block2y;
							}
							if(coordinatesBest[0] != block1y) System.out.println(""+newMSE+":::\t\t"+oldMSE+":::\t\ty"+(y)+":::\t\tx"+x);
						}
					}
					if(stepSize>1){
						for(int y = -1; y <= 1; y+=1){
							for(int x = -1; x <= 1; x+=1){
								int block2x = block1x+x;
								int block2y = block1y+y;
								double newMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block2x, block2y, blockWidth, blockHeight);
								if(newMSE < oldMSE) {
									oldMSE = newMSE;
									coordinatesBest[0] = block2x;
									coordinatesBest[1] = block2y;
								}
							}
						}
					}
					block1x = coordinatesBest[0];
					block1y = coordinatesBest[1];
					stepSize = (int)Math.floor(stepSize/2);
				}
//				ImageUtils.paintWhite(imgOut,block1x, block1y, coordinatesBest[0], coordinatesBest[1], width, height);
				coordinatesBest[0] -= block1xOrig;
				coordinatesBest[1] -= block1yOrig;
				return coordinatesBest;
	}
	
	private static int[] threeStepSearch(byte[] imgIn1, byte[]imgIn2, int width, int height, int block1x, int block1y,
												int blockWidth, int blockHeight, int stepSize, byte[] imgOut){
		//returnVal: int[0] = xBeste; int[1] = yBeste;
		int block1xOrig = block1x;
		int block1yOrig = block1y;
		int[] coordinatesBest = new int[]{block1x,block1y};
		double oldMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block1x, block1y, blockWidth, blockHeight);
		for(int i = 0; i < 3; i++){	
			for(int y = -stepSize; y <= stepSize; y+=stepSize){
				for(int x = -stepSize; x <= stepSize; x+=stepSize){
					int block2x = block1x+x;
					int block2y = block1y+y;
					double newMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block2x, block2y, blockWidth, blockHeight);
					if(newMSE < oldMSE) {
						oldMSE = newMSE;
						coordinatesBest[0] = block2x;
						coordinatesBest[1] = block2y;
					}
				}
			}
			if(stepSize>1){
				for(int y = -1; y <= 1; y+=1){
					for(int x = -1; x <= 1; x+=1){
						int block2x = block1x+x;
						int block2y = block1y+y;
						double newMSE = compareBlocksMSE(imgIn1, imgIn2, width, height, block1x, block1y, block2x, block2y, blockWidth, blockHeight);
						if(newMSE < oldMSE) {
							oldMSE = newMSE;
							coordinatesBest[0] = block2x;
							coordinatesBest[1] = block2y;
						}
					}
				}
			}
			block1x = coordinatesBest[0];
			block1y = coordinatesBest[1];
			stepSize = (int)Math.floor(stepSize/2);
		}
		ImageUtils.paintWhite(imgOut,block1x, block1y, coordinatesBest[0], coordinatesBest[1], width, height);
		coordinatesBest[0] -= block1xOrig;
		coordinatesBest[1] -= block1yOrig;
		return coordinatesBest;
	}
	
	public static double compareBlocksMSE(byte[] imgIn1, byte[]imgIn2, int width, int height, int block1x, int block1y,
												int block2x, int block2y, int blockWidth, int blockHeight){
		double mse = 0;
		int counter = 0;
		int centerX = (int)Math.floor(blockWidth/2);
		int centerY = (int)Math.floor(blockHeight/2);
		for(int y =0; y <blockHeight; y++){
			for(int x = 0; x < blockWidth; x++){
				int realX1 = x+block1x-centerX;
				int realY1 = y+block1y-centerY;
				if(realX1 >= width || realY1 >=height || realY1<0 || realX1<0) break;
				int realX2 = x+block2x-centerX;
				int realY2 = y+block2y-centerY;
				int pos1 = realY1*width+realX1;
				int pos2 = realY2*width+realX2;
				counter++;
				mse += Math.pow(Conversion.byteToInt(imgIn1[pos1])-Conversion.byteToInt(imgIn2[pos2]),2);
			}
		}
		mse /= counter;
		return mse;
	}
}
