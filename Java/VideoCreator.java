package JolleeB.BachelorT;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class VideoCreator {

	
	/** Solution with JavaCV
	 * 
	 * @param width
	 * @param height
	 * @param framerate
	 * @param frameList
	 * @param fileLocation
	 */
	public void createMP4(int width, int height, double framerate, ArrayList<BufferedImage> frameList, String fileLocation){
		FFmpegFrameRecorder creator = new FFmpegFrameRecorder(fileLocation,width/2,height/2);
		Java2DFrameConverter converter = new Java2DFrameConverter();
		try {
				creator.setVideoCodec(13);
				creator.setFormat("mp4");
				creator.setPixelFormat(0);
				creator.setFrameRate(framerate);
				creator.start();
				System.out.println("Initiated video creation");
		       for (int i=0;i<frameList.size();i++)
		       {
		    	   creator.record(converter.convert(frameList.get(i)));
		       }
				creator.record(converter.convert(frameList.get(5)));

				System.out.println("Video done!");
		       creator.stop();
		    }
		    catch (Exception e){
		       e.printStackTrace();
		    }
	}
	
	/**Solution with Xuggler
	 * 
	 * @param frameList
	 * @param fileLocation
	 */
	public void encode( ArrayList<BufferedImage> frameList,String fileLocation){
		IMediaWriter writer = ToolFactory.makeWriter(fileLocation);
		 writer.addVideoStream(0, 0,ICodec.ID.CODEC_ID_MPEG4, frameList.get(0).getWidth(), frameList.get(0).getHeight());
		 
		 
		 long startTime = System.nanoTime(); 
		 for (int i=0;i<frameList.size();i++)
		 {
		   BufferedImage image = new BufferedImage(frameList.get(i).getWidth(),frameList.get(i).getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		   image.getGraphics().drawImage(frameList.get(i), 0, 0, null);
				   
		   writer.encodeVideo(0, image,System.nanoTime()-startTime, TimeUnit.NANOSECONDS);
		   try {
			Thread.sleep(1000/30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 }
		 writer.close();
	}
	
	
}
