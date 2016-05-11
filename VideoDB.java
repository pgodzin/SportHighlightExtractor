/**
 * Created by sneha on 3/31/16.
 */

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

<<<<<<< HEAD
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
=======
import java.io.File;
import java.util.ArrayList;
import java.util.List;
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c

public class VideoDB {
    private static MongoClient mongoClient;
    private static DB database;
    private static GridFS gridfs;
    private static SportHistogram sportHist;
<<<<<<< HEAD
    private static GridFS frames;
=======
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c

    //public static void setUpDatabase
    public static MongoClient getMongoClient() {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mongoClient;
    }

    public static void initialSetUp() {
        mongoClient = getMongoClient();
<<<<<<< HEAD
        database = mongoClient.getDB("test");
        gridfs = new GridFS(database, "videos");
        frames = new GridFS(database, "frames");
=======

        database = mongoClient.getDB("test");
        gridfs = new GridFS(database, "videos");
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
    }

    private  static void saveFile(File file, String property, String value){
        try {
            GridFSInputFile gfsFile = gridfs.createFile(file);
            gfsFile.setFilename(file.getName());
<<<<<<< HEAD
=======

>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
            // add property & value (in our case sport type)
            gfsFile.put(property, value);

            // find and add histogram data
<<<<<<< HEAD
=======


>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
            gfsFile.save();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private static void saveFile(File file) throws Exception {
        GridFSInputFile gfsFile = gridfs.createFile(file);
        gfsFile.setFilename(file.getName());
        gfsFile.save();
        System.out.println("Saved File: " + file.getName());
    }

    public static void getFile() {
        String newFileName = "videos1.1";
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("videos1.1");

        //GridFS gfsPhoto = new GridFS(database, "videos");
        //printAllFiles(database.getCollection("videos.files"));
        //GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);

    }

    public static void printAllFiles(DBCollection collection) {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toString());
        }
    }

    private static void addFilesFromDirectory(File directoryName, String sportType) {
        System.out.println("addFilesFromDirectory().....");
        File[] directoryListing = directoryName.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                try {
                    System.out.println("Video file found....");
                    saveFile(child, "sport", sportType);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        sportHist = new SportHistogram();
<<<<<<< HEAD
        initialSetUp();
        loadLocalFiles();
        if(gridfs.findOne(new BasicDBObject("filename", ".DS_Store"))!=null ){
            gridfs.remove(new BasicDBObject("filename", ".DS_Store"));
        }

        //sportHist.getDominantColorWithHSV("../testVideos/test.mp4");
        BasicDBObject query = new BasicDBObject();
        if(gridfs.find(query) != null){
            System.out.println("Found it");
            //System.out.println(gridfs.find(query));
            //addDominantColors(database.getCollection("videos.files"));//thread
            printAllFiles(database.getCollection("videos.files"));
            //int[] test = new int[] {90,114,78};
            //String s = labelSportVideo(test,3);
            //testDatabase();
            saveAllFrames();
            //getRGBForFrames();
        }else{
            System.out.println("Did not find it");
        }



        int[] rgbArr = sportHist.getDominantColor("../testVideos/test.mp4");
        System.out.println("Dominant color of Test Video: " + rgbArr[0]+" " + rgbArr[1]+" " +rgbArr[2]);
        labelSportVideo(rgbArr, 5);
        printAllColorsAndSports();
    }
    public static void testDatabase(){
        int kn = 5;
        labelSportVideo(new int[] {78 , 114 , 66},kn);
        labelSportVideo(new int[] {30 , 42 , 42},kn);
        labelSportVideo(new int[] {78 , 114 , 30},kn);
        labelSportVideo(new int[] {90 , 114 , 78},kn);
        labelSportVideo(new int[] {18 , 18 , 18},kn);
        labelSportVideo(new int[] {90 , 114 , 78},kn);
        labelSportVideo(new int[] {78 , 114 , 54},kn);
        labelSportVideo(new int[] {126 , 174 , 90},kn);
        labelSportVideo(new int[] {150 , 114 , 90},kn);

        labelSportVideo(new int[] {186 , 174 , 186},kn);
        labelSportVideo(new int[] {186 , 6 , 186},kn);
        labelSportVideo(new int[] {222 , 198 , 198},kn);
        labelSportVideo(new int[] {222 , 198 , 198},kn);
        labelSportVideo(new int[] {186 , 174 , 186},kn);
        labelSportVideo(new int[] {186 , 6 , 186},kn);
        labelSportVideo(new int[] {222 , 198 , 198},kn);
        labelSportVideo(new int[] {186 , 210 , 210},kn);
        labelSportVideo(new int[] {198 , 198 , 198},kn);
        labelSportVideo(new int[] {198 , 198 , 198},kn);
        labelSportVideo(new int[] {210 , 210 , 210},kn);
        labelSportVideo(new int[] {198 , 198 , 198},kn);
        labelSportVideo(new int[] {210 , 210 , 210},kn);
        labelSportVideo(new int[] {198 , 198 , 198},kn);
        labelSportVideo(new int[] {186 , 210 , 210},kn);
        labelSportVideo(new int[] {222 , 198 , 198},kn);

        System.out.println("Here are sample inputs: ");
        labelSportVideo(new int[] {128,128,0},kn);
        labelSportVideo(new int[] {210,180,140},kn);
        labelSportVideo(new int[] {205,133,63},kn);

        labelSportVideo(new int[] {255,255,255},kn);
        labelSportVideo(new int[] {240,255,255},kn);
=======

        initialSetUp();
        loadLocalFiles();

        //sportHist.getDominantColor("../baseballvideos/asset_2500K-3.mp4");
        //sportHist.getDominantColorWithHSV("../baseballvideos/asset_2500K-3.mp4");

        BasicDBObject query = new BasicDBObject();
        if(gridfs.find(query)  != null){
            System.out.println("Found it");
            //System.out.println(gridfs.find(query));
            addDominantColors(database.getCollection("videos.files"));//thread
            //printAllFiles(database.getCollection("videos.files"));
        }else{
            System.out.println("Did not found it");
        }

        printAllColorsAndSports();

            //DBCollection collection= db.getCollection("testcollection1");

        //collection.insert(file);
        /*File file = new File("/Users/snehasilwal/IdeaProjects/VisualDB/SportHighlightExtractor/tessdata/videos/hockey1.mp4");
        saveFile(file);
        System.out.println("Saved file to db.");

        File file2 = new File("/Users/snehasilwal/IdeaProjects/VisualDB/SportHighlightExtractor/tessdata/videos/hockey2.mp4");
        saveFile(file2);
        System.out.println("Saved file2 to db.");
        */
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c

    }

    public static void printAllColorsAndSports(){
     /*   DBCollection collection = database.getCollection("videos.files");

        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            System.out.println(object.get("sport").toString());
            System.out.println(object.get("rgb").toString());
        }*/
        List<GridFSDBFile> baseballVids = gridfs.find( new BasicDBObject("sport", "baseball"));
        System.out.println("Baseball");
        for(GridFSDBFile files: baseballVids){
<<<<<<< HEAD
            System.out.println(files.get("filename"));
=======
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
            System.out.println(files.get("rgb").toString());
        }
        List<GridFSDBFile> hockeyVids = gridfs.find( new BasicDBObject("sport", "hockey"));
        System.out.println("Hockey");
        for(GridFSDBFile files: hockeyVids){
            System.out.println(files.get("rgb").toString());
        }

<<<<<<< HEAD
    }

    public static void saveAllFrames(){
        System.out.println("Save All Frames has been called.");
        List<GridFSDBFile> baseballVids = gridfs.find( new BasicDBObject("sport", "baseball"));
        System.out.println("Saving 500 frames of baseball videos....");
        for(GridFSDBFile files: baseballVids){
            ArrayList<BufferedImage> vidFrames = null;
            try {
                vidFrames = sportHist.getFramesOfVideo("../baseballvideos/" + files.get("filename").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(BufferedImage l: vidFrames){
                //BasicDBObject dbo = new BasicDBObject("sport","baseball");
                //dbo.append("frame", l);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(l, "jpg", baos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                GridFSInputFile gfsFile = frames.createFile(inputStream, true);
                gfsFile.put("sport","baseball");
                int[] rgb = sportHist.getDominantColorOfFrame(l);
                gfsFile.put("rgb",rgb);
                gfsFile.put("yuv", convertToYUV(rgb));
                gfsFile.save();
            }
        }
        List<GridFSDBFile> hockeyVids = gridfs.find( new BasicDBObject("sport", "hockey"));
        System.out.println("Hockey");
        for(GridFSDBFile files: hockeyVids){
            ArrayList<BufferedImage> vidFrames = null;
            try {
                vidFrames = sportHist.getFramesOfVideo("../hockeyvideos/" + files.get("filename").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(BufferedImage l: vidFrames){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(l, "jpg", baos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                GridFSInputFile gfsFile = frames.createFile(inputStream, true);
                gfsFile.put("sport","hockey");
                int[] rgb = sportHist.getDominantColorOfFrame(l);
                gfsFile.put("rgb",rgb);
                gfsFile.put("yuv", convertToYUV(rgb));

                gfsFile.save();
            }
        }
        System.out.println("Count of frames collection: ");
    }

    public static void getRGBForFrames(){
        System.out.println("Assigning RGB values to frames...");
        DBCursor frameCursor  = frames.getFileList();
        while(frameCursor.hasNext()){
            DBObject dbObj = frameCursor.next();
            GridFSDBFile next = frames.findOne(dbObj);;
            BufferedImage bimg = null;
            try {
                bimg = ImageIO.read(next.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int[] rgb = sportHist.getDominantColorOfFrame(bimg);
            next.put("rgb",rgb);
            frames.findOne(dbObj).put("rgb", rgb);
            int[] r =(int []) next.get("rgb");
            System.out.println("RGB for frame: ");
            for(int j = 0; j<r.length;j++){System.out.print( r[j] + " ");}
        }
        System.out.println("Printing Frames (258): ");
        printAllFiles(database.getCollection("frames.files"));
    }


    /**
     * Calculate and returns the average color histogram for a sport given
     * the sport type and the color measurement used (RGB or HSV). Will accept
     * either baseball or hockey.
     * @param baseball - Default value is to retrieve baseball values, will do so if true,
     *                 else will retrieve hockey values from the local database.
     * @param rgbValue - Default method is to retrieve the rgb values, will do so if true,
     *                 else will return the HSV values.
     * @return An array of integers describing the average color value for the given sport and color rep.
     */
    public int[] getDominantColor(boolean baseball, boolean rgbValue){

        List<GridFSDBFile> vidList;
        String sport = (baseball)? "baseball":"hockey";
        String colorQuery = (rgbValue) ? "rgb":"hsv";

        vidList = gridfs.find(new BasicDBObject("sport", sport));

        //create averageColor arr. that will be returned
        int[] avgColors;
        if(rgbValue){
            avgColors = new int[3]; //3 for RGB
        }else{
            avgColors = new int[2]; //2 for HSV
        }

        //iterate through all files retrieved
        for(GridFSDBFile files:vidList){
            int[] temp = (int[])files.get(colorQuery);
            for(int i=0;i<avgColors.length;i++){
                avgColors[i]+=temp[i];
            }
        }
        for(int i=0;i<avgColors.length;i++){
            avgColors[i] = avgColors[i]/vidList.size();
        }
        return avgColors;
    }

    private static class Node{
        double dist;
        String label;
        String name;
        Node(String sport, double d){
            label = sport;
            dist = d;
        }
        void setName(String x){
            name =x;
        }

    }
    /**
     *
     * @param testVals
     * @param k
     * @return
     */
    public static void labelSportVideo(int[] testVals, int k) {
        PriorityQueue<Node> queue = new PriorityQueue<>(k, (Comparator<Node>) (node1, node2) -> {
            int num;
            if (node1.dist > node2.dist) {
                num = 1;
            } else if (node1.dist == node2.dist) {
                num = 0;
            } else {
                num = -1;
            }
            return num;
        });

        //Retrieve all videos from DB
        List<GridFSDBFile> vidList;
        DBCursor cursor = frames.getFileList();

            while (cursor.hasNext()) {
                DBObject obj = (DBObject) cursor.next();
                GridFSDBFile objRGB = frames.findOne(obj);
/*            BasicDBList dbl = (BasicDBList) obj.get("rgb");
            Object [] ol = dbl.toArray();

            int[] nextVal = new int[3];
            for(int i =0;i<ol.length;i++ ){
                nextVal[i] = (Integer)ol[i];
            } *///new int[3]; //(int []) (obj.get("rgb"));
            /*int j = 0;
            BasicDBList bdl = obj.get("rgb");
            for(Object i: bdl.toArray()){
                nextVal[j] = (int) i;
                j++;
            }*/
                //double dx = compareColorDist(convertToYUV(testVals),convertToYUV(nextVal));

                double dx = compareColorDist(convertToYUV(testVals), (double[]) objRGB.get("yuv"));
                Node n = new Node((String) (obj.get("sport")), dx);
                n.setName((String) obj.get("filename"));
                queue.add(n);
            }

            int bCount = 0;
            int hCount = 0;

            //TODO just for testing...

            Node m = queue.remove();
            System.out.println("CLOSEST " + m.label + " DIST: " + m.dist + "\t" + m.name);
            if (m.label.equals("hockey")) {
                hCount++;
            } else if (m.label.equals("baseball")) {
                bCount++;
            }
            int aCount = 1;
            while (aCount < k) {
                //while(!queue.isEmpty()){
                Node n = queue.remove();
                System.out.println("CLOSEST " + n.label + " DIST: " + n.dist + "\t" + n.name);
                if (n.label.equals("hockey")) {
                    hCount++;
                } else if (n.label.equals("baseball")) {
                    bCount++;
                }
                aCount++;
            }
            String sprt = (hCount > bCount) ? "hockey" : "baseball";
            System.out.println("\n CLASSIFICATION:" + sprt + "\n\n");
        }

    /*public static int[] listToArr(BasicDBList bList){
        bList.toArray();
    }*/
    public static double[] convertToYUV(int [] rgb){
        double[] yuv = new double[3];
        double R = (double) rgb[0];
        double G = (double) rgb[1];
        double B = (double) rgb[2];

        //http://www.fourcc.org/fccyvrgb.php
        yuv[0] = (0.257*R) + (0.504*G) + (0.098*B) + 16;
        yuv[1] = - (0.148*R) - (0.291*G) + (0.439*B) + 128; //U value
        yuv[2] = (0.439*R) - (0.368*G) - (0.071*B) + 128; //V value
        return yuv;
    }
    public static double compareColorDist(double[] x1, double[] x2){
        double a1 = Math.pow((x1[0]- x2[0]),2.0);
        double a2 = Math.pow((x1[1]- x2[1]),2.0);
        double a3 = Math.pow((x1[2]- x2[2]),2.0);

        return Math.sqrt(a1 + a2 + a3);
    }

=======


    }
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
    public static void loadLocalFiles(){
        BasicDBObject hockeyQuery = new BasicDBObject("sport", "hockey");
        BasicDBObject baseballQuery = new BasicDBObject("sport", "baseball");
        if(gridfs.findOne(hockeyQuery) == null){
            System.out.println("Adding local hockey files to database ...");
            File baseballDir = new File("../hockeyvideos");
            addFilesFromDirectory(baseballDir, "hockey");
        }
        if(gridfs.findOne(baseballQuery) == null){
            System.out.println("Adding local baseball files to database ...");
            File baseballDir = new File("../baseballvideos");
            addFilesFromDirectory(baseballDir, "baseball");
        }
        if(gridfs.findOne(baseballQuery) != null && gridfs.findOne(baseballQuery) != null){
            System.out.println("Database already up to date");
        }
        printAllFiles(database.getCollection("videos.files"));
    }

    public static void addDominantColors(DBCollection collection) throws InterruptedException {
        //get all files
        //run in groups of 10
       // ArrayList<String> fileNames = getListOfFileNames(database.getCollection("videos.files"));
       // int half = (int)(fileNames.size()/2);

        System.out.println("Yes quinton, it worked");
        List<GridFSDBFile> baseballVids = gridfs.find( new BasicDBObject("sport", "baseball"));
        System.out.println("Baseball");
        System.out.println(Thread.currentThread().getId());
        Thread bthread = new Thread(){
            public void run(){
                System.out.println("baseballthread" + Thread.currentThread().getId());
                //System.out.println("T:JKSDHF:JSDHFJKSHDF:LDJSKF");
             /*   for(int i = 0; i<half; i++){

                    //getDominantColor(filename);

                    System.out.println(s.toString());
                }*/
                for(GridFSDBFile files: baseballVids){
                    int[] rgb = sportHist.getDominantColor("../baseballvideos/" + files.getFilename());
<<<<<<< HEAD
                    //int [] hsv = sportHist.getDominantColorWithHSV("../baseballvideos/" + files.getFilename());
                    files.put("rgb", rgb);
                    //files.put("hsv", hsv);
                    files.save();
                    System.out.println("Updated RGB value for a baseball video");
=======
                    int [] hsv = sportHist.getDominantColorWithHSV("../baseballvideos/" + files.getFilename());

                    files.put("rgb", rgb);
                    files.put("hsv", hsv);
                    files.save();
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
                }

            }
        };
        bthread.start();

        List<GridFSDBFile> hockeyVids = gridfs.find( new BasicDBObject("sport", "hockey"));
        System.out.println("Hockey");
        Thread hthread = new Thread(){
            public void run(){
               /* for(int i = half; i<fileNames.size(); i++){

                    //getDominantColor(filename);

                    int[] rgb = sportHist.getDominantColor(fileNames.get(i));
                    int [] hsv = sportHist.getDominantColorWithHSV(fileNames.get(i));

                    GridFSDBFile s = gridfs.find(fileNames.get(i)).get(0);
                    s.put("rgb", rgb);
                    s.put("hsv", hsv);
                    s.save();
                }*/

                for(GridFSDBFile files: hockeyVids){
                    int[] rgb = sportHist.getDominantColor("../hockeyvideos/" + files.getFilename());
<<<<<<< HEAD
                    //int [] hsv = sportHist.getDominantColorWithHSV("../hockeyvideos/" + files.getFilename());

                    files.put("rgb", rgb);
                    //files.put("hsv", hsv);
                    files.save();
                    System.out.println("Updated RGB value for a hockey video");
=======
                    int [] hsv = sportHist.getDominantColorWithHSV("../hockeyvideos/" + files.getFilename());

                    files.put("rgb", rgb);
                    files.put("hsv", hsv);
                    files.save();
>>>>>>> 890331d848203430f0c931f43141c5ebb4ab9d1c
                }
            }
        };
        hthread.start();

        bthread.join();
        hthread.join();
    }
    public static ArrayList<String> getListOfFileNames(DBCollection collection){
        ArrayList<String> fileNames = new ArrayList<>();
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            String s = cursor.next().get("filename").toString();
            fileNames.add(s);
        }
        return fileNames;
    }
}
