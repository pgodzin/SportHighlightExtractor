/**
 * Created by sneha on 3/31/16.
 */

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoDB {
    private static MongoClient mongoClient;
    private static DB database;
    private static GridFS gridfs;
    private static SportHistogram sportHist;

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

        database = mongoClient.getDB("test");
        gridfs = new GridFS(database, "videos");
    }

    private  static void saveFile(File file, String property, String value){
        try {
            GridFSInputFile gfsFile = gridfs.createFile(file);
            gfsFile.setFilename(file.getName());

            // add property & value (in our case sport type)
            gfsFile.put(property, value);

            // find and add histogram data


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
            System.out.println(files.get("rgb").toString());
        }
        List<GridFSDBFile> hockeyVids = gridfs.find( new BasicDBObject("sport", "hockey"));
        System.out.println("Hockey");
        for(GridFSDBFile files: hockeyVids){
            System.out.println(files.get("rgb").toString());
        }



    }
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
                    int [] hsv = sportHist.getDominantColorWithHSV("../baseballvideos/" + files.getFilename());

                    files.put("rgb", rgb);
                    files.put("hsv", hsv);
                    files.save();
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
                    int [] hsv = sportHist.getDominantColorWithHSV("../hockeyvideos/" + files.getFilename());

                    files.put("rgb", rgb);
                    files.put("hsv", hsv);
                    files.save();
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
