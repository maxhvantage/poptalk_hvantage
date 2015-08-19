package in.lockerapplication.utility;

import android.content.Context;

import com.ciao.app.apppreference.AppSharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Simon on 7/3/2015.
 *
 * This is the base class that allows proper fetching and passing of user data for adnetworks
 */
public abstract class UserDataPassingHelper {
    private HashMap<String,Object> userParams;
    protected List<String> userParamKeys;
    private Context context;
   public UserDataPassingHelper(Context context){
       setUserParamKeys(new ArrayList<String>());
       this.context = context;
   }
    public UserDataPassingHelper(Context context, List<String> requestedParams){
        setUserParamKeys(requestedParams);
        this.context = context;
    }
    protected void setUserParamKeys(List<String> params){
        userParamKeys = params;
    }
    protected void addUserParamKey(String param){
        userParamKeys.add(param);
    }
    protected List<String> getUserParamKeys(){
        return userParamKeys;
    }
    /*
    * Different ad networks will implement this method to format the user params into their accepted formats
    * */
    protected abstract Object formatUserParams(HashMap<String,Object> params);
    protected abstract String formatKey(String key);
    protected abstract Object formatValue(Object value);

    public Object fetchUserParams() {
        userParams = AppSharedPreference.getInstance(context).getUserParams((ArrayList)userParamKeys);
        return formatUserParams(userParams);
    }
}
