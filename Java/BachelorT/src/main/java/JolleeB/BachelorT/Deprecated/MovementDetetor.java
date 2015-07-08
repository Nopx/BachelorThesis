package JolleeB.BachelorT.Deprecated;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class MovementDetetor {
	ArrayList<BufferedImage> frames;
	static private final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
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
	
	static public ArrayList<BufferedImage> detectWColorsIntOptimized(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
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
		
		
		//****************************************************
		//CALCULATIONBODY START
		
		//Making a histogram over time per pixel
		final Semaphore indebtedSemaphore = new Semaphore(1 - PROCESSOR_COUNT);
		for (int i = 0; i < PROCESSOR_COUNT; ++i) {
			final int imageSize = pixVidIn[0].length;
			final int vidLength = pixVidIn.length;
			System.out.println("Calculating pixels..");
			final int startIndex = (i*(imageSize/3)/PROCESSOR_COUNT)*3;
			final int runLength = (i == PROCESSOR_COUNT -1)? imageSize-startIndex : (((i+1)*(imageSize/3)/PROCESSOR_COUNT)*3) - startIndex;
			final Runnable pixelRunner = new Runnable() {
				public void run() {
					try{
					int tolerance = 30;
					int z =0;
					for(int i = startIndex; i < startIndex+runLength; i+=3){
						z++;
						long percentMem = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())*100)/Runtime.getRuntime().maxMemory() ;
						if(z == 50000){
							System.out.println(percentMem+"% Mem Usage");
							z = 0;
						}

						short histLength = 256;
						byte[] histPxTime;
						histPxTime = new byte[histLength];
						HashMap<Short, Integer> colorCodes = new HashMap<Short, Integer>();
						short nextCode = 0;
						for(int v = 0; v < vidLength; v++){
							short code = -1;
							int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | (pixVidIn[v][i+2]&0x0000ff);
							
							for(Short c: colorCodes.keySet()){
								if(value == colorCodes.get(c)){
									code = c;
									break;
								}
							}
							if(code == -1 && nextCode < histLength){
								code = nextCode;
								nextCode++;
								colorCodes.put(code, value);
							}
							histPxTime[code]++;
						}
						short[] topFew = new short[tolerance];
						for(int j = 0; j < 255; j++){
							int newVal = histPxTime[j];
							for(int k = 0; k < tolerance; k++){
								if(newVal > histPxTime[topFew[k]]){
									for(int l = tolerance-1; l > k; l--){
										topFew[l] = topFew[l-1];
									}
									topFew[k] = (short)j;
									break;
								}
							}
						}
						
						for(int v = 0; v < vidLength; v++){
							int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | (pixVidIn[v][i+2]&0x0000ff);
							for(int k = 0; k < tolerance; k++){
								try{
									if(value == colorCodes.get(topFew[k])){
										break;
									}
								}
								catch(NullPointerException npe){
									//When there werent enough colors to fill the topfew
									break;
								}
								if(k == tolerance-1){
									value = colorCodes.get(topFew[0]);
									pixVidOut[v][i+0] = (byte)((value>>16)&0xff);
									pixVidOut[v][i+1] = (byte)((value>>8)&0xff);
									pixVidOut[v][i+2] = (byte)(value&0xff);
								}
							}
						}
					}
					}
					catch(final Throwable Exception){
						
					}
					finally{
						indebtedSemaphore.release();
					}
				}
			};
			new Thread(pixelRunner).start();
		}

		indebtedSemaphore.acquireUninterruptibly();
		System.out.println("Completed");
		
		
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
	
	
	/*public static ArrayList<BufferedImage> detectWColorsIntDeprecated(ArrayList<BufferedImage> framesInput){
		final int SCHWELLWERT = 125;
		final int VERGLEICHWEITE = 3;
		
		int width = framesInput.get(0).getWidth();
		int height = framesInput.get(0).getHeight();
		
		byte[][] pixVidIn = new byte[framesInput.size()][width*height];
		byte[][] pixVidOut;
		
		byte[][] histPxTime;
		//TODO Think whether it'd be better to use a 3 length byte array instead of Integer for memory usage and computing power
		ArrayList<HashMap<Short, Integer>> colorCodes = new ArrayList<HashMap<Short, Integer>>();
		
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
		short maxMemUse = 30;
		for(int i = 0; i < imageSize; i+=3){
			long percentMem = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())*100)/Runtime.getRuntime().maxMemory() ;
			int percentComplete = (int)((double)i/imageSize*100);
			System.out.println(percentComplete+" % completed USED MEM "+percentMem);
			
			colorCodes.add(new HashMap<Short, Integer>());
			short nextCode = 0;
			for(int v = 0; v < vidLength; v++){
				short code = -1;
				int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | (pixVidIn[v][i+2]&0x0000ff);
				
				for(Short c: colorCodes.get(i/3).keySet()){
					if(value == colorCodes.get(i/3).get(c)){
						code = c;
						break;
					}
				}
				if(code == -1 && nextCode < histLength){
					code = nextCode;
					nextCode++;
					colorCodes.get(i/3).put(code, value);
				}
				histPxTime[i/3][code]++;
				
			}
		}
		System.out.println("Histogram finished");
		int tolerance = 30;
		for(int i = 0; i < imageSize; i+=3){
			
			short[] topFew = new short[tolerance];
			for(int j = 0; j < 255; j++){
				int newVal = histPxTime[i/3][j];
				for(int k = 0; k < tolerance; k++){
					if(newVal > histPxTime[i/3][topFew[k]]){
						for(int l = tolerance-1; l > k; l--){
							topFew[l] = topFew[l-1];
						}
						topFew[k] = (short)j;
						break;
					}
				}
			}
			
			for(int v = 0; v < vidLength; v++){
				int value = ((pixVidIn[v][i]<<16)&0xff0000) | ((pixVidIn[v][i+1]<<8)&0x00ff00) | (pixVidIn[v][i+2]&0x0000ff);
				for(int k = 0; k < tolerance; k++){
					try{
						if(value == colorCodes.get(i/3).get(topFew[k])){
							break;
						}
					}
					catch(NullPointerException npe){
						//When there werent enough colors to fill the topfew
						break;
					}
					if(k == tolerance-1){
						value = colorCodes.get(i/3).get(topFew[0]);
						pixVidOut[v][i+0] = (byte)((value>>16)&0xff);
						pixVidOut[v][i+1] = (byte)((value>>8)&0xff);
						pixVidOut[v][i+2] = (byte)(value&0xff);
					}
				}
			}
		}
		

		
		System.out.println("MedianFilter");
		for(int v = 0; v < vidLength; v++){
			int kernelSize = 7;
			int[] kernel = new int[kernelSize];
			byte[] imageOut = new byte[imageSize];
			for(int y = 0 ; y < height; y++){
				
				//kernel auffüllen am anfang
				for(int k = 0; k < kernelSize; k++){
					if(k < kernelSize/2){
						int pos = (y*width*3);
						kernel[k] = ((pixVidOut[v][pos]<<16)&0xff0000) | ((pixVidOut[v][pos+1]<<8)&0x00ff00) | (pixVidOut[v][pos+2]&0x0000ff);
					}
					else{
						int pos = (y*width*3)+(k-(kernelSize/2))*3;
						kernel[k] = ((pixVidOut[v][pos]<<16)&0xff0000) | ((pixVidOut[v][pos+1]<<8)&0x00ff00) | (pixVidOut[v][pos+2]&0x0000ff);
					}
				}
				
				for(int x = 0; x < width*3; x+=3){
					int pos = y*width*3+x;
					for(int k = 0; k < kernelSize; k++){
						try{
							kernel[k] = kernel[k+1];
						}
						catch(ArrayIndexOutOfBoundsException aiobe){
							try{
								kernel[k] = ((pixVidOut[v][pos+kernelSize/2]<<16)&0xff0000) | ((pixVidOut[v][pos+1+kernelSize/2]<<8)&0x00ff00) | (pixVidOut[v][pos+2+kernelSize/2]&0x0000ff);
							}
							catch(ArrayIndexOutOfBoundsException aiobe2){
								kernel[k] = ((pixVidOut[v][pos]<<16)&0xff0000) | ((pixVidOut[v][pos+1]<<8)&0x00ff00) | (pixVidOut[v][pos+2]&0x0000ff);
							}
						}
					}
					int[] valuesKernel = kernel.clone();
					for(int sk = 0; sk < valuesKernel.length; sk++){
						valuesKernel[sk] = (((valuesKernel[sk]>>16) & 0x0000ff) + ((valuesKernel[sk]>>8) & 0x0000ff) + ((valuesKernel[sk]) & 0x0000ff)) / 3;
					}
					int[] sortedKernel = valuesKernel.clone();
					Arrays.sort(sortedKernel);
					int medVal = (Integer)sortedKernel[kernelSize/2];
					int indexVal = kernelSize/2;
					for(int j = 0; j < valuesKernel.length; j++){
						if(valuesKernel[j] == medVal){
							indexVal = j;
							break;
						}
					}
					int value = kernel[indexVal];
					imageOut[pos+0] = (byte)((value>>16)&0x0000ff);
					imageOut[pos+1] = (byte)((value>>8)&0x0000ff);
					imageOut[pos+2] = (byte)(value&0x0000ff);
				}
				
			}
			pixVidOut[v] = imageOut;
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
	}*/
}
