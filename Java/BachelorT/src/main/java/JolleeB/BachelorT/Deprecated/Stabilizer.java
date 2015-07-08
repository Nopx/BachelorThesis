package JolleeB.BachelorT.Deprecated;

import JolleeB.BachelorT.Utils.Conversion;
import JolleeB.BachelorT.Utils.ImageFormatingUtils;
import JolleeB.BachelorT.Utils.ImageUtils;

public class Stabilizer {

	int[] movement = new int[2];

	public byte[] stabilizeImage(byte[] imgIn1, byte[] imgIn2, int width,
			int height) {
		byte[] imgGray1 = ImageFormatingUtils.makeGrayScaleImage(imgIn1);
		byte[] imgGray2 = ImageFormatingUtils.makeGrayScaleImage(imgIn2);
		byte[] imgOut = imgGray1.clone(); 
		int blockAmount = 20;
		int randSize = 40;
		int blockwidth = (height-2*randSize)/blockAmount;
		int[] newBlock = new int[2];
		for(int y =0; y< blockAmount; y++){
			for(int x =0; x< blockAmount; x++){
				int[] newBlock2 = threeStepSearchAdvanced(imgGray1, imgGray2, width, height, blockwidth*x+randSize, blockwidth*y+randSize, blockwidth, blockwidth, 9, 0);
				newBlock[0] += newBlock2[0];
				newBlock[1] += newBlock2[1];
			}
		}
		newBlock[0] /= blockAmount*blockAmount;
		newBlock[1] /= blockAmount*blockAmount;
		movement[0] -=newBlock[0];
		movement[1] -=newBlock[1];
		movement[0] +=width/2;
		movement[1] +=height/2;
//		for(int y =height/2-blockwidth/2;y<height/2+blockwidth/2;y++){
//			for(int x =width/2-blockwidth/2;x<width/2+blockwidth/2;x++){
//				imgOut[y*width+x] = -1;
//			}
//		}
		for(int y =movement[1]-blockwidth/2;y<movement[1]+blockwidth/2;y++){
			for(int x =movement[0]-blockwidth/2;x<movement[0]+blockwidth/2;x++){
				try{
				imgOut[y*width+x] = -1;
				}
				catch(Exception e){
					
				}
			}
		}
		movement[0] -=width/2;
		movement[1] -=height/2;
		return ImageFormatingUtils.makeColorScale(imgOut);
	}

	private static int[] threeStepSearchAdvanced(byte[] imgIn1, byte[] imgIn2,
			int width, int height, int block1x, int block1y, int blockWidth,
			int blockHeight, int stepSize, int mseThreshold) {
		int steps = 3;
		// returnVal: int[0] = xBeste; int[1] = yBeste;
		int block1xOrig = block1x;
		int block1yOrig = block1y;
		int[] coordinatesBest = new int[] { block1x, block1y };
		double oldMSE = compareBlocksMSE(imgIn1, imgIn2, width, height,
				block1x, block1y, block1x, block1y, blockWidth, blockHeight);
		if (oldMSE < mseThreshold) {
			coordinatesBest[0] -= block1xOrig;
			coordinatesBest[1] -= block1yOrig;
			return coordinatesBest;
		}
		for (int i = 0; i < steps; i++) {
			for (int y = -stepSize; y <= stepSize; y += stepSize) {
				for (int x = -stepSize; x <= stepSize; x += stepSize) {
					int block2x = block1x + x;
					int block2y = block1y + y;
					double newMSE = compareBlocksMSE(imgIn1, imgIn2, width,
							height, block1x, block1y, block2x, block2y,
							blockWidth, blockHeight);
					if (newMSE < oldMSE) {
						oldMSE = newMSE;
						coordinatesBest[0] = block2x;
						coordinatesBest[1] = block2y;
					}
				}
			}
			if (stepSize > 1) {
				for (int y = -1; y <= 1; y += 1) {
					for (int x = -1; x <= 1; x += 1) {
						int block2x = block1x + x;
						int block2y = block1y + y;
						double newMSE = compareBlocksMSE(imgIn1, imgIn2, width,
								height, block1x, block1y, block2x, block2y,
								blockWidth, blockHeight);
						if (newMSE < oldMSE) {
							oldMSE = newMSE;
							coordinatesBest[0] = block2x;
							coordinatesBest[1] = block2y;
						}
					}
				}
			}
			block1x = coordinatesBest[0];
			block1y = coordinatesBest[1];
			stepSize = (int) Math.floor(stepSize / 2);
		}
		coordinatesBest[0]-=block1xOrig;
		coordinatesBest[1]-=block1yOrig;
		return coordinatesBest;
	}

	public static double compareBlocksMSE(byte[] imgIn1, byte[] imgIn2,
			int width, int height, int block1x, int block1y, int block2x,
			int block2y, int blockWidth, int blockHeight) {
		double mse = 0;
		int counter = 0;
		int centerX = (int) Math.floor(blockWidth / 2);
		int centerY = (int) Math.floor(blockHeight / 2);
		for (int y = 0; y < blockHeight; y++) {
			for (int x = 0; x < blockWidth; x++) {
				int realX1 = x + block1x - centerX;
				int realY1 = y + block1y - centerY;
				if (realX1 >= width || realY1 >= height || realY1 < 0
						|| realX1 < 0)
					break;
				int realX2 = x + block2x - centerX;
				int realY2 = y + block2y - centerY;
				int pos1 = realY1 * width + realX1;
				int pos2 = realY2 * width + realX2;
				counter++;
				mse += Math.pow(
						Conversion.byteToInt(imgIn1[pos1])
								- Conversion.byteToInt(imgIn2[pos2]), 2);
			}
		}
		mse /= counter;
		return mse;
	}

}
