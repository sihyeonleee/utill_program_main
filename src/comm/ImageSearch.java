//package comm;
//
//import java.awt.Rectangle;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.imageio.ImageIO;
//
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.sikuli.script.Match;
//import org.sikuli.script.Pattern;
//import org.sikuli.script.Region;
//import org.sikuli.script.Screen;
//
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//
//public class ImageSearch {
//	
//	
//	public List<Match> findMatchesScreenBlocking(File file, float similarity) {
//
//		List<Match> results = new ArrayList<>();
//		try {
//			Screen screen = new Screen();
//
//			Pattern target = new Pattern(file.getAbsolutePath()).similar(similarity);
//			Iterator<Match> matches = screen.findAll(target);
//
//			while (matches.hasNext())
//				results.add(matches.next());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return results;
//	}
//	
//	public List<Match> findMatchesSelectBlocking(File file, float similarity) {
//
//		List<Match> results = new ArrayList<>();
//		try {
//			Screen screen = new Screen();
//
//			Region region = screen.selectRegion(); // 실행 중 드래그로 지정
//			Pattern target = new Pattern(file.getAbsolutePath()).similar(similarity);
//
//			Iterator<Match> matches = region.findAll(target);
//			while (matches.hasNext())
//				results.add(matches.next());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return results;
//	}
//	
//	int DIST_THRESHOLD = 70; // 거리 임계값
//	
//	public List<Rectangle> groupMatches(List<Match> matches) {
//	        List<Rectangle> groupedBoxes = new ArrayList<>();
//	        boolean[] visited = new boolean[matches.size()];
//
//	        for (int i = 0; i < matches.size(); i++) {
//	            if (visited[i]) continue;
//
//	            Match m1 = matches.get(i);
//	            Rectangle groupRect = new Rectangle(m1.x, m1.y, m1.w, m1.h);
//	            visited[i] = true;
//
//	            boolean changed;
//	            do {
//	                changed = false;
//	                for (int j = 0; j < matches.size(); j++) {
//	                    if (visited[j]) continue;
//	                    Match m2 = matches.get(j);
//	                    if (isNear(groupRect, m2)) {
//	                        groupRect = groupRect.union(new Rectangle(m2.x, m2.y, m2.w, m2.h));
//	                        visited[j] = true;
//	                        changed = true;
//	                    }
//	                }
//	            } while (changed);
//
//	            groupedBoxes.add(groupRect);
//	        }
//
//	        return groupedBoxes;
//	    }
//
//	    public boolean isNear(Rectangle r, Match m) {
//	        Rectangle mRect = new Rectangle(m.x, m.y, m.w, m.h);
//	        Rectangle expand = new Rectangle(r.x - DIST_THRESHOLD, r.y - DIST_THRESHOLD,
//	                r.width + 2 * DIST_THRESHOLD, r.height + 2 * DIST_THRESHOLD);
//	        return expand.intersects(mRect);
//	    }
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	public void tesseract(File file) {
//		
//		// 4. Tesseract OCR 실행
//		Tesseract tesseract = new Tesseract();
//		tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // 경로 수정
//		tesseract.setLanguage("kor");
////		tesseract.setTessVariable("user_defined_dpi", "300");
//
//		String result = null;
//		try {
//			result = tesseract.doOCR(file.getAbsoluteFile());
//		} catch (TesseractException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("OCR 결과: " + result);
//	}
//
//	public void grayTesseract(File file) {
//
//		System.load(new File("ref/opencv_java4110.dll").getAbsolutePath());
//
//		// 1. OpenCV 이미지 로딩
//		Mat src = Imgcodecs.imread(file.getAbsolutePath());
//
//		// 2. Grayscale + Threshold
//
//		// 2. 전처리
//		Mat gray = new Mat();
//		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
//
//		Mat binary = new Mat();
//		Imgproc.threshold(gray, binary, 130, 255, Imgproc.THRESH_BINARY);
//
//		// 3. OCR용 확대
//		Mat enlarged = new Mat();
//		Imgproc.resize(binary, enlarged, new Size(binary.width() * 2, binary.height() * 2));
//
//		// 4. 디버그 저장
////		Imgcodecs.imwrite("debug_binary.png", binary);
////		Imgcodecs.imwrite("debug_enlarged.png", enlarged);
//
//		// 3. Mat → BufferedImage 변환
//		BufferedImage image = null;
//		try {
//			image = matToBufferedImage(enlarged);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// 4. Tesseract OCR 실행
//		Tesseract tesseract = new Tesseract();
//		tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // 경로 수정
//		tesseract.setLanguage("kor");
////		tesseract.setTessVariable("user_defined_dpi", "300");
//
//		String result = null;
//		try {
//			result = tesseract.doOCR(image);
//		} catch (TesseractException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("OCR 결과: " + result);
//	}
//
//	private BufferedImage matToBufferedImage(Mat mat) throws Exception {
//		MatOfByte mob = new MatOfByte();
//		Imgcodecs.imencode(".png", mat, mob);
//		byte[] byteArray = mob.toArray();
//		InputStream in = new ByteArrayInputStream(byteArray);
//		return ImageIO.read(in);
//	}
//}
