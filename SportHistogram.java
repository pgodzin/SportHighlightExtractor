import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import static java.util.Arrays.asList;


public class SportHistogram {
    private static int binSize;
    private static boolean accumulate;
    boolean RGBMethod = true;

    public SportHistogram(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        accumulate = true;
        binSize = 20;

        RGBMethod = true;
    }
    public SportHistogram(int bins, boolean rgb){
       super();
        binSize = bins;
        RGBMethod = rgb;
    }

    public static int[] getDominantColor(String filename) {

        MatOfFloat histRange = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(binSize);

        VideoCapture video = new VideoCapture(filename);

        Mat frame = new Mat();

        Mat b_hist = new Mat();
        Mat g_hist = new Mat();
        Mat r_hist = new Mat();

        double framesToSkip = video.get(Videoio.CAP_PROP_FRAME_COUNT) / 20;
        double frameCount = video.get(Videoio.CAP_PROP_FRAME_COUNT);

        while (video.isOpened()) {

            double posFrames = video.get(Videoio.CAP_PROP_POS_FRAMES);
            if (frameCount - posFrames < framesToSkip) break;
            video.set(Videoio.CAP_PROP_POS_FRAMES, posFrames + framesToSkip);
            video.read(frame);

            Imgproc.calcHist(asList(frame), new MatOfInt(0), new Mat(), b_hist, histSize, histRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(1), new Mat(), g_hist, histSize, histRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(2), new Mat(), r_hist, histSize, histRange, accumulate);

        }
        video.release();

        Core.normalize(b_hist, b_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(g_hist, g_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(r_hist, r_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());

        int maxB = findMaxIndex(matToArr(b_hist));
        int maxG =  findMaxIndex(matToArr(g_hist));
        int maxR = findMaxIndex(matToArr(r_hist));

        return getRGB(maxB,maxG,maxR);

    }


    public static int[] getDominantColorWithHSV(String filename) {
        MatOfFloat histRange = new MatOfFloat(0f, 256f);
        MatOfFloat hsvRange = new MatOfFloat(0f, 180f);

        MatOfInt histSize = new MatOfInt(binSize);

        VideoCapture video = new VideoCapture(filename);

        Mat frame = new Mat();

        Mat h_hist = new Mat();
        Mat s_hist = new Mat();

        double framesToSkip = video.get(Videoio.CAP_PROP_FRAME_COUNT)/ 20;
        double frameCount = video.get(Videoio.CAP_PROP_FRAME_COUNT);

        while (video.isOpened()) {
            double posFrames = video.get(Videoio.CAP_PROP_POS_FRAMES);
            if (frameCount - posFrames < framesToSkip) break;
            video.set(Videoio.CAP_PROP_POS_FRAMES, posFrames + framesToSkip);
            //System.out.println(posFrames);
            video.read(frame);

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);

            Imgproc.calcHist(asList(frame), new MatOfInt(0), new Mat(), h_hist, histSize, hsvRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(1), new Mat(), s_hist, histSize, histRange, accumulate);

        }
        video.release();

        System.out.println("End of while loop");
        Core.normalize(h_hist, h_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(s_hist, s_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());

        System.out.println(h_hist.t().dump());
        System.out.println(s_hist.t().dump());

        int maxS = findMaxIndex(matToArr(s_hist));
        int maxH =  findMaxIndex(matToArr(h_hist));
        System.out.println("Max S: "+ maxS);
        System.out.println("Max H: " +maxH);

        return getHS(maxH, maxS);
    }

    public static int findMaxIndex(double[] arr){
        int maxInt = 0;
        double maxVal = arr[maxInt];

        for(int i = 0; i < arr.length; i++){
            if (arr[i] > maxVal){
                maxInt = i;
                maxVal = arr[i];
            }
        }
        return maxInt;
    }
    public static double[] matToArr(Mat m){
        double[] arr = new double[m.rows()];
        for(int i = 0 ; i<m.rows() ; i++){
            arr[i] = m.get(i,0)[0];
        }
        return arr;
    }
    public static int[] getRGB(int b, int g, int r){
        return new int[] {(int) ((r+0.5)*(256/binSize)), (int) ((g+0.5)*(256/binSize)), (int) ((b+0.5)*(256/binSize))};
    }
    public static int[] getHS(int h, int s){
        return new int[] {(int)((h+0.5)*(256/binSize)),(int)((s+0.5)*(256/binSize))};
    }
}