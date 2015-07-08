package JolleeB.BachelorT.DeEnCoding;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.concurrent.Semaphore;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class DynamicFrameGetter {
	private byte[][] currentFrames;
	private int width;
	private int height;
	private double framerate;
	private boolean hasFrames = true;
	private Java2DFrameConverter converter = new Java2DFrameConverter();
	private  OpenCVFrameGrabber fg;
	private Semaphore getterThreadSemaphore = new Semaphore(1);
	private int frameAmount = 0;
    
	
	//TODO make postprocessor for median and avged frames
	private Runnable frameSetter = new Runnable() {
		public void run() {
			try {
				for(int i = 0; i < currentFrames.length-1; i++) {
					currentFrames[i] = currentFrames[i+1]; 
				}
				if(currentFrames.length >1 && currentFrames[currentFrames.length-2] != null && currentFrames[currentFrames.length-1] != null)
					currentFrames[currentFrames.length-2] = currentFrames[currentFrames.length-1].clone();
				org.bytedeco.javacv.Frame frame = fg.grab(); 
				BufferedImage bi = converter.convert(frame);
				currentFrames[currentFrames.length-1] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
			}
			catch (FrameGrabber.Exception e) {
				hasFrames = false;
				try {
					fg.stop();
				} catch (Exception e1) {
					System.out.println("PROBLEM IN FRAMEGRABBER THREAD");
				}
			}
			finally{
				getterThreadSemaphore.release();
			}
		}
	};
	
	private Runnable initialFrameSetter = new Runnable() {
			public void run() {
				try {
					for(int i = 0; i < currentFrames.length; i++) {
						org.bytedeco.javacv.Frame frame = fg.grab(); 
						BufferedImage bi = converter.convert(frame);
						currentFrames[i] = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
					}
				}
				catch (FrameGrabber.Exception e) {
					hasFrames = false;
					try {
						fg.stop();
					} catch (Exception e1) {}
				}
				finally{
					getterThreadSemaphore.release();
				}
			}
		};
	
	public DynamicFrameGetter(File videoFile,int framesToLoad) throws org.bytedeco.javacv.FrameGrabber.Exception{
        fg = new OpenCVFrameGrabber(videoFile);
        fg.start();
        width = fg.getImageWidth();
        height = fg.getImageHeight();
        framerate = fg.getFrameRate();
        currentFrames = new byte[framesToLoad][];
        frameAmount = fg.getLengthInFrames();
        for(int i = 0; i < framesToLoad; i++){
			getterThreadSemaphore.acquireUninterruptibly();
	        new Thread(frameSetter).start();
        }
	}
	
	synchronized private void setNextFrame(){
		getterThreadSemaphore.acquireUninterruptibly();
		new Thread(frameSetter).start();
	}
	
	synchronized public byte[][] getNextFrames(){
		setNextFrame();
		byte[][] imgs = currentFrames.clone();
		return imgs;
	}
	
	public void stop() throws Exception{
		fg.stop();
		hasFrames = false;
	}
	
	public boolean hasFrames(){
		return hasFrames;
	}
	
	public int getFrameAmount(){
		return frameAmount;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public double getFramerate(){
		return framerate;
	}
	
}
