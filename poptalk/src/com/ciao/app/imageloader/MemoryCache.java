package com.ciao.app.imageloader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * Created by rajat on 22/12/14.
 * This class is use to handle memory cache automatically refresh the cache if it is
 * overloaded because bitmap is always heavy to handle
 */
public class MemoryCache {
    //    private static final String TAG = "MemoryCache";
    private Map<String, Bitmap> cache= Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10, 1.5f, true));//Last argument true for LRU ordering
    private long size=0;//current allocated size
    private long limit=1000000;//max memory in bytes

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }

    /**
     * set the limit for the memory cache in bytes
     * @param new_limit
     */
    public void setLimit(long new_limit){
        limit=new_limit;
    }

    /**
     * return bitmap on behalf of id from map
     * @param id
     * @return {@link android.graphics.Bitmap}
     */
    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * method is use to put bitmap on behalf of id after checking limit
     * @param id
     * @param bitmap
     * @return void
     */
    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id))
                size-=getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }

    /**
     * method is use to check size of bitmap in bytes
     * @param none
     * @return void
     */
    private void checkSize() {
        if(size>limit){
            Iterator<Map.Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while(iter.hasNext()){
                Map.Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
        }
    }

    /**
     * method is use to clear chache
     * @param none
     * @return void
     */
    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    /**
     * method is use to get size of bitmap
     * @param bitmap
     * @return long
     */
    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
