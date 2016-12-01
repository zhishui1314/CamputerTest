package permissiongen.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import permissiongen.PermissionFail;
import permissiongen.PermissionSuccess;

/**
 * Created by namee on 2015. 11. 18..
 */
final public class Utils {
  private Utils(){}

  public static boolean isOverMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  public static List<String> findDeniedPermissions(Activity activity, String... permission){
    List<String> denyPermissions = new ArrayList<>();
    for(String value : permission){
      if(activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED){
        denyPermissions.add(value);
      }
    }
    return denyPermissions;
  }
  public static boolean isEqualRequestCodeFromAnntation(Method m, Class clazz, int requestCode){
    if(clazz.equals(PermissionFail.class)){
      return requestCode == m.getAnnotation(PermissionFail.class).requestCode();
    } else if(clazz.equals(PermissionSuccess.class)){
      return requestCode == m.getAnnotation(PermissionSuccess.class).requestCode();
    } else {
      return false;
    }
  }

  public static <A extends Annotation> Method findMethodWithRequestCode(Class clazz,
      Class<A> annotation, int requestCode) {
    for(Method method : clazz.getDeclaredMethods()){
      if(method.isAnnotationPresent(annotation)){
        if(isEqualRequestCodeFromAnntation(method, annotation, requestCode)){
          return method;
        }
      }
    }
    return null;
  }


}
