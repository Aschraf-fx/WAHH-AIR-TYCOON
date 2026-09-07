package com.wahhair.tycoon;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.webkit.*;
import android.net.Uri;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
 private WebView web; private String pending; private GameMusic music;
 @Override public void onCreate(Bundle b){super.onCreate(b);music=new GameMusic(this);getWindow().getDecorView().setSystemUiVisibility(5894);web=new WebView(this);setContentView(web);web.setOnApplyWindowInsetsListener((v,i)->{v.setPadding(i.getSystemWindowInsetLeft(),i.getSystemWindowInsetTop(),i.getSystemWindowInsetRight(),i.getSystemWindowInsetBottom());return i;});
 web.getSettings().setJavaScriptEnabled(true);web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);web.getSettings().setDomStorageEnabled(false);web.addJavascriptInterface(new Bridge(),"Android");web.setWebChromeClient(new WebChromeClient());web.setWebViewClient(new WebViewClient(){
 @Override public boolean shouldOverrideUrlLoading(WebView w,WebResourceRequest r){return !"game.wahhair.local".equals(r.getUrl().getHost());}
 @Override public WebResourceResponse shouldInterceptRequest(WebView w,WebResourceRequest r){Uri u=r.getUrl();if(!"https".equals(u.getScheme())||!"game.wahhair.local".equals(u.getHost()))return empty();String path=u.getPath();if(path==null||path.contains(".."))return empty();String f=path.equals("/")?"index.html":path.substring(1);String mime=f.endsWith(".js")?"application/javascript":f.endsWith(".css")?"text/css":f.endsWith(".jpg")?"image/jpeg":"text/html";try{return new WebResourceResponse(mime,"UTF-8",getAssets().open(f));}catch(IOException e){return empty();}}
 });web.loadUrl("https://game.wahhair.local/");}
 private WebResourceResponse empty(){return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));}
 public class Bridge {
  @JavascriptInterface public void musicOptions(boolean enabled,int volume){runOnUiThread(()->music.setOptions(enabled,volume));}
  @JavascriptInterface public void musicEnterGame(){runOnUiThread(()->music.enterGame());}
  @JavascriptInterface public void musicNext(){runOnUiThread(()->music.next());}
  @JavascriptInterface public String musicTitle(){return music.title();}
  @JavascriptInterface public String musicError(){return music.error();}
  @JavascriptInterface public String load(){return getPreferences(MODE_PRIVATE).getString("tycoonSave","");}
  @JavascriptInterface public boolean save(String value){try{new JSONObject(value);if(value.length()>2500000)return false;return getPreferences(MODE_PRIVATE).edit().putString("tycoonSave",value).commit();}catch(Exception e){return false;}}
  @JavascriptInterface public void exportData(String value){pending=value;runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"WAHH-AIR-Tycoon-save.json");startActivityForResult(i,1);});}
  @JavascriptInterface public void importData(){runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,2);});}
  @JavascriptInterface public void pickAvatar(){runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,3);});}
 }
 @Override protected void onPause(){if(music!=null)music.suspend();web.evaluateJavascript("if(window.onGameBackground)onGameBackground()",null);web.onPause();super.onPause();}
 @Override protected void onResume(){super.onResume();if(web!=null)web.onResume();if(music!=null)music.resume();}
 @Override protected void onDestroy(){if(music!=null)music.release();if(web!=null)web.destroy();super.onDestroy();}
 @Override public void onBackPressed(){web.evaluateJavascript("UI.close();UI.speed(0)",null);}
 @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;try{Uri uri=data.getData();if(req==1){if(pending==null)throw new IOException("Please export again");try(OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new IOException("Cannot open destination");out.write(pending.getBytes(StandardCharsets.UTF_8));}Toast.makeText(this,"Game saved to file",Toast.LENGTH_LONG).show();}else if(req==2){ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=getContentResolver().openInputStream(uri)){byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1){if(out.size()+n>2500000)throw new IOException("Save exceeds 2.5MB");out.write(buf,0,n);}}web.evaluateJavascript("receiveSave("+JSONObject.quote(new String(out.toByteArray(),StandardCharsets.UTF_8))+")",null);}else if(req==3){BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;try(InputStream in=getContentResolver().openInputStream(uri)){BitmapFactory.decodeStream(in,null,o);}if(o.outWidth<=0||o.outHeight<=0)throw new IOException("Unsupported photo format");int sample=1;while(Math.max(o.outWidth,o.outHeight)/sample>1200)sample*=2;o.inJustDecodeBounds=false;o.inSampleSize=sample;Bitmap bitmap;try(InputStream in=getContentResolver().openInputStream(uri)){bitmap=BitmapFactory.decodeStream(in,null,o);}if(bitmap==null)throw new IOException("Cannot decode photo");int orientation=1;try(InputStream in=getContentResolver().openInputStream(uri)){orientation=new ExifInterface(in).getAttributeInt(ExifInterface.TAG_ORIENTATION,1);}catch(Exception ignored){}Matrix m=new Matrix();switch(orientation){case 2:m.setScale(-1,1);break;case 3:m.setRotate(180);break;case 4:m.setScale(1,-1);break;case 5:m.setRotate(90);m.postScale(-1,1);break;case 6:m.setRotate(90);break;case 7:m.setRotate(-90);m.postScale(-1,1);break;case 8:m.setRotate(-90);break;}Bitmap upright=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);ByteArrayOutputStream out=new ByteArrayOutputStream();upright.compress(Bitmap.CompressFormat.JPEG,88,out);String image="data:image/jpeg;base64,"+Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);web.evaluateJavascript("receiveAvatar("+JSONObject.quote(image)+")",null);if(upright!=bitmap)upright.recycle();bitmap.recycle();}}catch(Exception e){Toast.makeText(this,"Could not open file: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
}
