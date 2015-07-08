package JolleeB.BachelorT;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.util.ArrayList;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class FrameGetter {

	
	public double getFPS(String fileString){
		try {
			OpenCVFrameGrabber fg = new OpenCVFrameGrabber(fileString);
			fg.start();
			double fps = fg.getFrameRate();
			fg.stop();
			return fps;
		} catch (Exception e) {
			ErrorHandler.handleTermitatingError(e);
			return -1;
		}
		
	}
	
	public ArrayList<BufferedImage> getFramesAsBufferedImages(int frameInterval, File file, int start, int end){
		return getFramesAsBufferedImages(frameInterval, file.getAbsolutePath(),start,end);
	}
	
	public ArrayList<BufferedImage> getFramesAsBufferedImages(int frameInterval, String fileString, int start, int end){
		long timeSave = System.currentTimeMillis();
		ArrayList<BufferedImage> returnList = new ArrayList<BufferedImage>();
		Java2DFrameConverter converter = new Java2DFrameConverter();
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(fileString);
        int counter = 0;
        org.bytedeco.javacv.Frame f;
        try{
	        fg.start();
	        System.out.println("Getting frames..");
	        
	        while((f = fg.grab())!=null) {
	        	counter++;
				BufferedImage capturedImage = converter.convert(f);
				 ColorModel imageColorMode = capturedImage.getColorModel();
				if(counter>=start && (frameInterval ==0 || (counter+frameInterval-1)%frameInterval == 0)){
					//manually cloning BufferedImage, otherwise everything has the same ID
					returnList.add(new BufferedImage(imageColorMode, capturedImage.copyData(null), imageColorMode.isAlphaPremultiplied(), null));
					f = new org.bytedeco.javacv.Frame();
				}
				if(counter >= end){
					break;
				}
	        }
	
	        fg.stop();
	        System.out.println("Frames gotten");
        }
        catch(Exception e){
        	ErrorHandler.handleTermitatingError(e);
        }
        timeSave = System.currentTimeMillis()-timeSave;
        System.out.println("Done with "+counter+" frames in "+timeSave+" milliseconds -> "+(timeSave/counter)+"ms/img");
        return returnList;
	}
	
	public ArrayList<Frame> getFrames(String fileString, int start, int end){
		return getFrames(new File(fileString),start,end);
	}
	
	public ArrayList<Frame> getFrames(File file, int start, int end){
		ArrayList<Frame> returnList = new ArrayList<Frame>();
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber("../../Videos/Sample1.mp4");
        int counter = 0;
        Frame f;
        try{
	        fg.start();
	        System.out.println("Getting frames..");
	        
	        while((f = fg.grab())!=null) {
	        	counter++;
				if(counter>=start){
					returnList.add(f);
				}
				if(counter >= end){
					break;
				}
	        }
	        fg.stop();
	        System.out.println("Frames gotten");
        }
        catch(Exception e){
        	ErrorHandler.handleTermitatingError(e);
        }
        
        System.out.println("done after "+counter+" frames");
        return returnList;
	}
}
