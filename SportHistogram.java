import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import static java.util.Arrays.asList;


public class SportHistogram {
    //private static Mat currentImage;
    private static int binSize;
    //private static MatOfFloat histRange;
    //private static MatOfInt histSize;
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

    /*public static void main(String[] args) {
        //Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
        //System.out.println( "mat = " + mat.dump());

    }*/

    public static int[] getDominantColor(String filename) {
        /*try{
            currentImage = imread(fileName);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }*/

        MatOfFloat histRange = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(binSize);
        //boolean accumulate = true;

        VideoCapture video = new VideoCapture(filename);

        Mat frame = new Mat();

        Mat b_hist = new Mat();
        Mat g_hist = new Mat();
        Mat r_hist = new Mat();

        Mat hist = new Mat();

        int j = 0;

        while (video.isOpened()) {
            for (int i = 0; i < 5; i++) {
                //if(i%1000 == 0){ System.out.println("Skipping...");}
                if (!video.read(frame)) break; // skip i frames
            }
            video.read(frame);

            /*Mat b = new Mat();
            Core.extractChannel(frame, b, 0);
            Mat g = new Mat();
            Core.extractChannel(frame, g, 1);
            Mat r = new Mat();
            Core.extractChannel(frame, r, 2);*/

            Imgproc.calcHist(asList(frame), new MatOfInt(0), new Mat(), b_hist, histSize, histRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(1), new Mat(), g_hist, histSize, histRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(2), new Mat(), r_hist, histSize, histRange, accumulate);

            //ArrayList<Mat> bgr_planes = new ArrayList<>();
            //split(frame,bgr_planes);

            MatOfInt channels = new MatOfInt(0, 1, 2);
            MatOfFloat ranges = new MatOfFloat(0f, 256f, 0f, 256f, 0f, 256f);

            Imgproc.calcHist(asList(frame), channels, new Mat(), hist, new MatOfInt(16, 16, 16), ranges, accumulate);

            System.out.println("J:" + j);
            j = j + 1;
            if (j > 5) video.release();
        }
        System.out.println("End of while loop");
        Core.normalize(b_hist, b_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(g_hist, g_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(r_hist, r_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());

        //Core.normalize(hist,hist,0,100,Core.NORM_MINMAX,-1, new Mat());

        //System.out.println(hist.dump());

        /*System.out.println(b_hist.t().dump());
        System.out.println(g_hist.t().dump());
        System.out.println(r_hist.t().dump());*/
        /*System.out.println("dims:" + b_hist.dims());
        System.out.println("elemSize:" + b_hist.elemSize());
        System.out.println("size:" + b_hist.size());

        System.out.println("total:" + b_hist.total());*/
       /* Mat b = new Mat();
        Core.extractChannel(hist, b, 0);
        Mat g = new Mat();
        Core.extractChannel(hist, g, 1);*/

        int maxB = findMaxIndex(matToArr(b_hist));
        int maxG =  findMaxIndex(matToArr(g_hist));
        int maxR = findMaxIndex(matToArr(r_hist));
        System.out.println("Max B: "+ maxB );
        System.out.println("Max G: " +maxG);
        System.out.println("Max R: " + maxR);
        System.out.println("together:" + getRGB(maxB,maxG,maxR).toString());
        video.release();

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
        Mat v_hist = new Mat();

        Mat hist = new Mat();

        int j = 0;

        while (video.isOpened()) {
            for (int i = 0; i < 5; i++) {
                //if(i%1000 == 0){ System.out.println("Skipping...");}
                if (!video.read(frame)) break; // skip i frames
            }

            video.read(frame);
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);

            /*Mat b = new Mat();
            Core.extractChannel(frame, b, 0);
            Mat g = new Mat();
            Core.extractChannel(frame, g, 1);
            Mat r = new Mat();
            Core.extractChannel(frame, r, 2);*/

            Imgproc.calcHist(asList(frame), new MatOfInt(0), new Mat(), h_hist, histSize, hsvRange, accumulate);
            Imgproc.calcHist(asList(frame), new MatOfInt(1), new Mat(), s_hist, histSize, histRange, accumulate);
            //Imgproc.calcHist(asList(frame), new MatOfInt(2), new Mat(), v_hist, histSize, histRange, accumulate);


            System.out.println("J:" + j);
            j = j + 1;
            if (j > 5) video.release();
        }
        System.out.println("End of while loop");
        Core.normalize(h_hist, h_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(s_hist, s_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        //Core.normalize(r_hist, r_hist, 0, 100, Core.NORM_MINMAX, -1, new Mat());


        System.out.println(h_hist.t().dump());
        System.out.println(s_hist.t().dump());


        int maxS = findMaxIndex(matToArr(s_hist));
        int maxH =  findMaxIndex(matToArr(h_hist));
        System.out.println("Max S: "+ maxS);
        System.out.println("Max H: " +maxH);
/*        System.out.println("H: " + (int)(maxH * 180* 1.5 / binSize));
        System.out.println("S: " + (int)(maxS * 256* 1.5 / binSize));*/

        video.release();
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