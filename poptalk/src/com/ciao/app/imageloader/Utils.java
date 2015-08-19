package com.ciao.app.imageloader;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rajat on 22/12/14.
 * This is image loader utils class use to copy input stream
 * into output stream
 */
public class Utils {
    /**
     * This method is used to copy input stream into output stream
     * @param is
     * @param os
     * @return void
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
