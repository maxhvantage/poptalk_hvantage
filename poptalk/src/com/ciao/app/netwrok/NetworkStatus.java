package com.ciao.app.netwrok;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rajat on 3/2/15.
 */
public class NetworkStatus {
/**
 * This method checks the internet availability
 * @param context
 * @return boolean
 */

   public static boolean isConected(Context context){
       ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
       if(connectivityManager!=null){
          NetworkInfo[] networkInfos =  connectivityManager.getAllNetworkInfo();
          if(networkInfos!=null){
               for (int i=0;i<networkInfos.length;i++){
                   if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
                       return true;
                   }
               }
           }

       }
       return false;
   }
   
   
}
