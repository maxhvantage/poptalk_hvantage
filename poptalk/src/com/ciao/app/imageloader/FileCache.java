package com.ciao.app.imageloader;

/**
 * Created by rajat on 22/12/14.
 */

import java.io.File;
import java.lang.reflect.Constructor;

import android.content.Context;

/**
 * This class is use to handle file data showing in list view and clear file chache
 * according to need autometically
 */
public class FileCache {

    private File cacheDir;

    /**
     * {@link Constructor}
     * @param context
     */
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"Ciao/Images");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    /**
     * THis method is use to return file by downloading file through url
     * @param url
     * @return {@link java.io.File}
     */
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    /**
     * This mehtod is use to clear file cache
     */
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}