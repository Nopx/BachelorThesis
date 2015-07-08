package JolleeB.BachelorT;

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

public class MovementDetetorNew {
	ArrayList<BufferedImage> frames;
	public MovementDetetorNew(ArrayList<BufferedImage> frames){
		this.frames = frames;
	}
	public MovementDetetorNew(){
		
	}
	
	public static ArrayList<BufferedImage> detect(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		byte[][] pixVidOut;
		
		byte[][] histPxTime;
		int[][] top5;
		int[][] top5Index;
		int maxListLength = 5;
		
		System.out.println("Extracting pixels..");
		for(int i = 0; i < framesInput.size(); i++){
			BufferedImage bi = framesInput.get(i);
			pixVidIn[i] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		}
		pixVidOut = pixVidIn.clone();
		
		
		//****************************************************
		//CALCULATIONBODY START
		
		//Making a histogram over time per pixel
		int imageSize = pixVidIn[0].length;
		int vidLength = pixVidIn.length;
		histPxTime = new byte[imageSize][256];
		top5 = new int[imageSize][maxListLength];
		top5Index = new int[imageSize][maxListLength];
		System.out.println("Calculating pixels..");
		for(int i = 0; i < vidLength; i++){
			for(int j = 0; j < imageSize; j++){
				int value = pixVidIn[i][j];
				histPxTime[j][value+128]++;
			}
		}
		//Maximal vorkommende Farbe ermitteln
		for(int j = 0; j < imageSize; j++){
			int maxValue = 0;
			int maxIndex = 0;
			for(int i = 0; i < 255; i++){
				int newVal = histPxTime[j][i];
				for(int k = 0; k<maxListLength ; k++){
					if(newVal>top5[j][k]){
						for(int m = maxListLength-1; m>k; m--){
							top5[j][m] = top5[j][m-1];
							top5Index[j][m] = top5Index[j][m-1];
						}
						top5[j][k] = newVal;
						top5Index[j][k] = i;
						break;
					}
				}
//				for(int k = 0; k <5; k++){
//					if(newVal > top5[j][k]){
//						for(int l = 0; l <k; l++){
//							
//						}
//					}
//				}
			}
		}
		for(int i = 0; i < vidLength; i++){
			for(int j = 0; j < imageSize; j++){
				for(int m = 0; m <maxListLength; m++){
					if(pixVidIn[i][j] == (byte)(top5Index[j][m]-128)){
						pixVidOut[i][j] = (byte)(top5Index[j][m]-128);
						break;
					}
					else{
						pixVidOut[i][j] = (byte)(top5Index[j][0]-128);
					}
				}
			}
		}
		//CALCULATIONBODY END
		//****************************************************
			
			
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
}
