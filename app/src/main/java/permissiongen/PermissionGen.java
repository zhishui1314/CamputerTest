package permissiongen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import permissiongen.internal.Utils;


/**
 * Created by namee on 2015. 11. 17..
 */
public class PermissionGen {
  private String[] mPermissions;
  private int mRequestCode;
  private Activity activity;

  private PermissionGen(Activity activity) {
    this.activity = activity;
  }

  public static PermissionGen with(Activity activity){
    return new PermissionGen(activity);
  }

  public PermissionGen permissions(String... permissions){
    this.mPermissions = permissions;
    return this;
  }

  public PermissionGen addRequestCode(int requestCode){
    this.mRequestCode = requestCode;
    return this;
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  public void request(){
    requestPermissions(activity, mRequestCode, mPermissions);
  }

  public static void needPermission(Activity activity, int requestCode, String[] permissions){
    requestPermissions(activity, requestCode, permissions);
  }

  public static void needPermission(Activity activity, int requestCode, String permission){
    needPermission(activity, requestCode, new String[] { permission });
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  private static void requestPermissions(Activity activity, int requestCode, String[] permissions){
    if(!Utils.isOverMarshmallow()) {
      doExecuteSuccess(activity, requestCode);
      return;
    }
    List<String> deniedPermissions = Utils.findDeniedPermissions(activity, permissions);

    if(deniedPermissions.size() > 0){
      if(activity instanceof Activity){
        activity.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
      } else{
        throw new IllegalArgumentException(activity.getClass().getName() + " is not supported");
      }

    } else {
      doExecuteSuccess(activity, requestCode);
    }
  }


  private static void doExecuteSuccess(Object activity, int requestCode) {
    Method executeMethod = Utils.findMethodWithRequestCode(activity.getClass(),
        PermissionSuccess.class, requestCode);

    executeMethod(activity, executeMethod);
  }

  private static void doExecuteFail(Object activity, int requestCode) {
    Method executeMethod = Utils.findMethodWithRequestCode(activity.getClass(),
        PermissionFail.class, requestCode);

    executeMethod(activity, executeMethod);
  }

  private static void executeMethod(Object activity, Method executeMethod) {
    if(executeMethod != null){
      try {
        if(!executeMethod.isAccessible()) executeMethod.setAccessible(true);
        executeMethod.invoke(activity, new Object[]{});
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
      int[] grantResults) {
    requestResult(activity, requestCode, permissions, grantResults);
  }

  private static void requestResult(Activity activity, int requestCode, String[] permissions,
      int[] grantResults){
    List<String> deniedPermissions = new ArrayList<>();
    for(int i=0; i<grantResults.length; i++){
      if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
        deniedPermissions.add(permissions[i]);
      }
    }

    if(deniedPermissions.size() > 0){
      doExecuteFail(activity, requestCode);
    } else {
      doExecuteSuccess(activity, requestCode);
    }
  }
}
