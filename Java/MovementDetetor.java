package JolleeB.BachelorT;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;

public class MovementDetetor {
	ArrayList<BufferedImage> frames;
	public MovementDetetor(ArrayList<BufferedImage> frames){
		this.frames = frames;
	}
	public MovementDetetor(){
		
	}
	
	/**
	 * Determines the Maximum Value for each Channel for each Pixel and keeps that.
	 * @param framesInput
	 * @return
	 */
	public static ArrayList<BufferedImage> detectWChannels(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		byte[][] pixVidOut;
		
		byte[][] histPxTime;
		byte[][] top5;
		
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
		top5 = new byte[imageSize][5];
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
				if(newVal>maxValue){
					maxValue = newVal;
					maxIndex = i;
				}
//				for(int k = 0; k <5; k++){
//					if(newVal > top5[j][k]){
//						for(int l = 0; l <k; l++){
//							
//						}
//					}
//				}
			}
			pixVidOut[0][j]=(byte)(maxIndex-128);
		}
		for(int i = 0; i < vidLength; i++){
			pixVidOut[i] = pixVidOut[0];
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
	
	/**
	 * Like detect, just with 5 max values instead of 1. If the color in the Video is
	 * @param framesInput
	 * @return
	 */
	public static ArrayList<BufferedImage> detectDetailed(ArrayList<BufferedImage> framesInput){
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
	
	public static ArrayList<BufferedImage> detectWColors(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		byte[][] pixVidOut;
		
		byte[][] histPxTime;
		//TODO Think whether it'd be better to use a 3 length byte array instead of Integer for memory usage and computing power
		HashMap<Short, Integer> colorCodes = new HashMap<Short, Integer>();
		
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
		short histLength = 256;
		histPxTime = new byte[width*height][histLength];
		System.out.println("Calculating pixels..");
		for(int i = 0; i < imageSize; i+=3){
			short nextCode = 0;
			for(int v = 0; v < vidLength; v++){
				short code = -1;
				int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | pixVidIn[v][i+2];
				for(Short c: colorCodes.keySet()){
					if(value == colorCodes.get(c)){
						code = c;
						break;
					}
				}
				if(code == -1 && nextCode <histLength){
					code = nextCode;
					nextCode++;
					colorCodes.put(code, value);
				}
				histPxTime[i][code]++;
			}
		}
		//Maximal vorkommende Farbe ermitteln
		for(int j = 0; j < imageSize; j++){
			int maxValue = 0;
			int maxIndex = 0;
			for(int i = 0; i < 255; i++){
				int newVal = histPxTime[j][i];
				if(newVal>maxValue){
					maxValue = newVal;
					maxIndex = i;
				}
//				for(int k = 0; k <5; k++){
//					if(newVal > top5[j][k]){
//						for(int l = 0; l <k; l++){
//							
//						}
//					}
//				}
			}
			pixVidOut[0][j]=(byte)(maxIndex-128);
		}
		for(int i = 0; i < vidLength; i++){
			pixVidOut[i] = pixVidOut[0];
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
	
	public static ArrayList<BufferedImage> detectWColorsInt(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		byte[][] pixVidOut;
		
		byte[][] histPxTime;
		//TODO Think whether it'd be better to use a 3 length byte array instead of Integer for memory usage and computing power
		HashMap<Short, Integer> colorCodes = new HashMap<Short, Integer>();
		
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
		short histLength = 256;
		histPxTime = new byte[width*height][histLength];
		System.out.println("Calculating pixels..");
		for(int i = 0; i < imageSize; i+=3){
			short nextCode = 0;
			for(int v = 0; v < vidLength; v++){
				short code = -1;
				int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | pixVidIn[v][i+2];
				byte xxxv = (byte)((value >> 16) );
				int zwf = 250;
				byte zwfd = (byte)zwf;
				
//				System.out.println(xxxv);
				pixVidOut[v][i+0] = (byte)(value>>16&0xff);
				pixVidOut[v][i+1] = (byte)(value>>8&0xff);
				pixVidOut[v][i+2] = (byte)(value&0xff);
				byte xxv = (byte)(value & 0x0000ff);
//				System.out.println(xxv);
//				pixVidOut[v][i] = pixVidIn[v][i];
//				pixVidOut[v][i+1] = pixVidIn[v][i+1];
//				pixVidOut[v][i+2] = pixVidIn[v][i+2];
//				for(Short c: colorCodes.keySet()){
//					if(value == colorCodes.get(c)){
//						code = c;
//						break;
//					}
//				}
//				if(code == -1 && nextCode <histLength){
//					code = nextCode;
//					nextCode++;
//					colorCodes.put(code, value);
//				}
//				histPxTime[i][code]++;
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
	
	public static ArrayList<BufferedImage> detectWColorsIntSave(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		int[][] pixVidOut;
		
		byte[][] histPxTime;
		//TODO Think whether it'd be better to use a 3 length byte array instead of Integer for memory usage and computing power
		HashMap<Short, Integer> colorCodes = new HashMap<Short, Integer>();
		
		System.out.println("Extracting pixels..");
		for(int i = 0; i < framesInput.size(); i++){
			BufferedImage bi = framesInput.get(i);
			pixVidIn[i] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		}
		
		
		//****************************************************
		//CALCULATIONBODY START
		
		//Making a histogram over time per pixel
		int imageSize = pixVidIn[0].length;
		int vidLength = pixVidIn.length;
		pixVidOut = new int[vidLength][width*height];
		short histLength = 256;
		histPxTime = new byte[width*height][histLength];
		System.out.println("Calculating pixels..");
		for(int i = 0; i < width*height; i++){
			short nextCode = 0;
			for(int v = 0; v < vidLength; v++){
				short code = -1;
				int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | pixVidIn[v][i+2];
				
				pixVidOut[v][i] = value;
//				System.out.println(xxv);
//				pixVidOut[v][i] = pixVidIn[v][i];
//				pixVidOut[v][i+1] = pixVidIn[v][i+1];
//				pixVidOut[v][i+2] = pixVidIn[v][i+2];
//				for(Short c: colorCodes.keySet()){
//					if(value == colorCodes.get(c)){
//						code = c;
//						break;
//					}
//				}
//				if(code == -1 && nextCode <histLength){
//					code = nextCode;
//					nextCode++;
//					colorCodes.put(code, value);
//				}
//				histPxTime[i][code]++;
			}
		}
		//CALCULATIONBODY END
		//****************************************************
			
			
		System.out.println("Saving pixels..");
		for(int i = 0; i < framesInput.size(); i++){
//			ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
//	        DataBuffer dataBuf = new DataBufferByte(pixVidOut[i], pixVidOut[i].length);
//			WritableRaster raster = Raster.createInterleavedRaster(dataBuf, width, height,width*3, 3, new int[]{2, 1, 0}, new Point());
//			framesInput.set(i, new BufferedImage(cm, raster, false, null));
	        DataBufferInt dataBufInt = new DataBufferInt(pixVidOut[i],width*height);
	        int[] bitMask = new int[]{0xff0000,0x00ff00,0x0000ff,0xff000000};
	        SinglePixelPackedSampleModel smp = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, bitMask);
	        WritableRaster raster = Raster.createWritableRaster(smp, dataBufInt, null);
	        framesInput.set(i, new BufferedImage(ColorModel.getRGBdefault(), raster, false, null));
	        //true oder false macht keinen unterschied hier?????!
		}
		return framesInput;
	}
}
