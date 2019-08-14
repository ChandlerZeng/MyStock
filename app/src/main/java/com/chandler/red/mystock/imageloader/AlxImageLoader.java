package com.chandler.red.mystock.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.chandler.red.mystock.App;
import com.chandler.red.mystock.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 2016/8/22.
 */
public class AlxImageLoader {

    private Context mcontext;
    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
    private ConcurrentHashMap<ImageView,String> currentUrls = new ConcurrentHashMap<>();//记录一个imageView应该显示哪个Url，用于中断子线程
    private Bitmap defbitmap;
    public int PLACE_HOLDER = R.mipmap.pictures_no;
    public boolean usePlaceHolder = false;//是否使用占位图，默认不使用
    private static AlxImageLoader mInstance;

    public AlxImageLoader(Context context) {
        this.mcontext = context;
        defbitmap = BitmapFactory.decodeResource(mcontext.getResources(), PLACE_HOLDER);//加载完成之前显示的占位图
    }

    public static AlxImageLoader getInstance(){
        if(mInstance==null){
            synchronized (AlxImageLoader.class){
                if(mInstance==null){
                    mInstance = new AlxImageLoader(App.getContext());
                }
            }
        }
        return mInstance;
    }

    /**
     * 从本地加载一张图片并使用imageView进行显示，可以设置是否根据图片的大小动态修改imageView的高度，宽度必须传入来控制显示图片的清晰度防止oom
     * @param uri
     * @param imageView
     * @param imageViewWidth
     * @param resizeImageView
     * @param autoRotate
     * @param loadCompleteCallback
     * @return
     */
    private Bitmap loadBitmapFromSD(
            final String uri, //图片地址
            final ImageView imageView, //要显示的imageView
            final int imageViewWidth, //imageView的宽度，单位像素
            final boolean resizeImageView, //是否要根据图像的宽高比例重设imageView的宽高比例
            final boolean autoRotate,//是否根据图片的EXIF信息旋转图片
            final boolean storeThumbnail ,//是否在sd卡中存储缩略图，下次加载快
            final ImageCallback loadCompleteCallback) //如果从本地加载新的图片成功，就调用这个方法
    {
        if (imageCache.containsKey(uri)) {//如果之前已经加载过这个图片，那么就从LRU缓存里加载
            SoftReference<Bitmap> SoftReference = imageCache.get(uri);
            Bitmap bitmap = SoftReference.get();
            if (bitmap != null) {
                return bitmap;//从系统内存里直接拿出来
            }
        }

        if(uri ==null)return null;
        if(storeThumbnail) {//如果要求从缓存中拿，那么就先去磁盘缓存里找
            final Context context = imageView.getContext();
            new AlxMultiTask<Void,Void,Bitmap>(){

                @Override
                protected Bitmap doInBackground(Void... params) {
                    File file = new File(context.getCacheDir().getAbsolutePath().concat("/" + new File(uri).getName()));
                    if (file.exists() && file.length()>1000) {
                        //因为从file中获取图片的宽高存在IO操作，所以把每个图片的宽高缓存起来
                        Bitmap thumbnail = null;
                        String targetUrl = currentUrls.get(imageView);
                        if(!uri.equals(targetUrl)) {
                            return null;
                        }
                        try {
                            thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
                        }catch (OutOfMemoryError e){
                        }catch (Exception e){

                        }
                        return thumbnail;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    String targetUrl = currentUrls.get(imageView);
                    if(!uri.equals(targetUrl)) {
                        return;
                    }
                    if(bitmap != null && loadCompleteCallback != null){//如果
                        loadCompleteCallback.imageLoaded(bitmap,imageView,uri);
                        return;
                    }else {
                        loadNewSDImage(uri,imageView,imageViewWidth,resizeImageView,autoRotate,storeThumbnail,loadCompleteCallback);
                    }

                }
            }.executeDependSDK();
            return usePlaceHolder?defbitmap:null;//如果返回默认bitmap就用默认图占位，如果返回null就用imageView父控件的背景色占位,null更快
        }
        //如果没有要求存储缩略图，直接从本地解析
        loadNewSDImage(uri,imageView,imageViewWidth,resizeImageView,autoRotate,storeThumbnail,loadCompleteCallback);
        return usePlaceHolder?defbitmap:null;//如果返回默认bitmap就用默认图占位，如果返回null就用imageView父控件的背景色占位,null更快
    }

    public void loadNewSDImage(
            final String uri, //图片地址
            final ImageView imageView, //要显示的imageView
            final int imageViewWidth, //imageView的宽度，单位像素
            final boolean resizeImageView, //是否要根据图像的宽高比例重设imageView的宽高比例
            final boolean autoRotate,//是否根据图片的EXIF信息旋转图片
            final boolean storeThumbnail ,//是否在sd卡中存储缩略图，下次加载快
            final ImageCallback loadCompleteCallback //如果从本地加载新的图片成功，就调用这个方法
    ){
        new AlxMultiTask<Void,Void,BitmapFactory.Options>(){

            @Override
            protected BitmapFactory.Options doInBackground(Void... params) {//这一块主要是用来拿宽高，确定要加载图片的大小的
                //线程开启之后，由于滚动太快，已经过了一段时间，可能imageView要显示的图片已经换了，就没有必要执行下面的东西了
                final int[] imageSize = {0,0};
                String targetUrl = currentUrls.get(imageView);//滑动的非常快的时候会在此处中断
                if(!uri.equals(targetUrl)) {
                    return null;
                }
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 不读取像素数组到内存中，仅读取图片的信息，非常重要
                BitmapFactory.decodeFile(uri, options);//读取文件信息，存放到Options对象中
                String targetUrl1 = currentUrls.get(imageView);//滑动的非常快的时候会在此处中断
                if(!uri.equals(targetUrl1)) {
                    Log.i("Alex","这个图片已经过时了1");
                    return null;
                }
                // 从Options中获取图片的分辨率
                imageSize[0] = options.outWidth;
                imageSize[1] = options.outHeight;
                Log.i("Alex","原图的分辨率是"+imageSize[0]+"  "+imageSize[1]);
                Log.i("Alex","目标宽度是"+imageViewWidth);
                if(imageSize[0]<1)return null;
                int destWidth = imageViewWidth;
                if (imageViewWidth > 400) destWidth *= 0.7;//如果imageView太大的话，不需要加载那么大的图片，就缩小一下
                options.inSampleSize = calculateInSampleSize(options,destWidth,destWidth*options.outHeight/options.outWidth);
                options.inPurgeable = true;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                return options;
            }

            @Override
            protected void onPostExecute(final BitmapFactory.Options options) {
                super.onPostExecute(options);
                //在线程终止回调的时候会产生巨大的延迟
                String targetUrl = currentUrls.get(imageView);
                if(!uri.equals(targetUrl)) {
                    Log.i("Alex","这个图片已经过时了haha");
                    return;
                }
                if (options == null) return;
                asynGetBitmap(options,uri,imageView,imageViewWidth,resizeImageView,autoRotate,storeThumbnail,loadCompleteCallback);
            }
        }.executeDependSDK();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            //计算图片高度和我们需要高度的最接近比例值
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            //宽度比例值
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取比例值中的较大值作为inSampleSize
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    /**
     * 一个在子线程里从文件获取bitmap，并存到LRU缓存的方法
     * @param catchedOptions
     * @param uri
     * @param imageView
     * @param autoRotate
     * @param imageCallback
     */
    private void asynGetBitmap(final BitmapFactory.Options catchedOptions, final String uri, final ImageView imageView, final int imageViewWidth, final boolean resizeImageView, final boolean autoRotate, final boolean storeThumbnail, final ImageCallback imageCallback){
        //如果不需要重置imageView的大小，那么底下这部分先不执行
        if (resizeImageView && imageViewWidth > 0) {//如果给出了imageView的宽度，就修改imageView的宽高以自适应图片的宽高
            int imageViewHeight;
            int degree = readPictureDegree(uri);
            if (autoRotate && (degree == 90 || degree == 270)) {//如果原来是竖着的，且需要自动摆正那么宽和高要互换
                imageViewHeight = catchedOptions.outWidth * imageViewWidth / catchedOptions.outHeight;
            } else {
                imageViewHeight = catchedOptions.outHeight * imageViewWidth / catchedOptions.outWidth;
            }
            Log.i("Alex", "准备重设高度" + imageViewHeight);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if (params != null) {//如果是旋转90度的图片，那么宽和高应该互换
                params.height = imageViewHeight;
                imageView.setLayoutParams(params);
            }
        }
        new AlxMultiTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                String targetUrl = currentUrls.get(imageView);
                if(!uri.equals(targetUrl)) {
                    Log.i("Alex","这个图片已经过时了2");//滑动的比较快的时候会在此处中断
                    return null;
                }
                Bitmap bitmap = null;
                //首先获取完整的bitmap存到内存里，此处有可能oom
                bitmap = getBitmapFromFile(uri, catchedOptions);
//                String targetUrl3 = currentUrls.get(imageView);
//                if(!uri.equals(targetUrl3)) {
//                    Log.i("Alex","这个图片已经过时了3");//在此处经常会中断
//                    return null;
//                }
                //获取完bitmap之后，因为已经过了一段时间，可能imageView要显示的图片已经换了，就没有必要执行下面的东西了
                if (autoRotate) {//如果需要自动旋转
                    int degree = readPictureDegree(uri);
                    //获取完角度之后，因为已经过了一段时间，可能imageView要显示的图片已经换了，就没有必要执行下面的东西了
                    if (degree != 0) bitmap = rotateBitmap(bitmap, degree, true);
                }
                if (bitmap == null) bitmap = BitmapFactory.decodeResource(mcontext.getResources(), R.mipmap.pictures_no);//如果出现异常，就用默认的bitmap
                return bitmap;
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                super.onPostExecute(bitmap);
                String targetUrl = currentUrls.get(imageView);
                if(!uri.equals(targetUrl)) {
                    Log.i("Alex","这个图片已经过时了5");//在此处经常会中断
                    return;
                }
                if (bitmap == null) return;
                imageCallback.imageLoaded(bitmap, imageView, uri);

                //显示完图片之后将缩略图缓存到本地
                final Context context = imageView.getContext();
                if(!storeThumbnail)return;
                new AlxMultiTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        if(!imageCache.containsKey(uri))imageCache.put(uri, new SoftReference<Bitmap>(bitmap));//将bitmap存到LRU缓存里
                        storeThumbnail(context,new File(uri).getName(),bitmap);
                        return null;
                    }
                }.executeDependSDK();

            }
        }.executeDependSDK();
    }
    private interface ImageCallback {
        void imageLoaded(Bitmap imageBitmap, ImageView imageView, String uri);
    }


    /**
     * 从本地根据相应的options获取完整的bitmap存到内存里，有可能会出现oom异常
     * @param uri
     * @param options
     * @return
     */
    private static Bitmap getBitmapFromFile(String uri, BitmapFactory.Options options) {
        if(uri==null || uri.length()<4 || options==null)return null;
        try{
            if(!new File(uri).isFile())return null;//如果文件不存在
            Bitmap bitmap = BitmapFactory.decodeFile(uri, options);// 这里还是会出现oom??
            return bitmap;
        }catch (Exception e){
            Log.i("Alex","从图片中获取bitmap出现异常");
        }catch (OutOfMemoryError e) {
            Log.i("Alex","从文件中获取图片 OOM了");
        }
        return null;
    }

    /**
     * 读取一个jpg文件的exif中的旋转信息
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.i("Alex","获取图片旋转角度出现异常");
            return degree;
        }
        Log.i("Alex","本张图片的旋转角度是"+path+"   角度是"+degree);
        return degree;
    }

    /**
     * 旋转一个bitmap，注意这个操作会销毁传入的bitmap，并且会占用源bitmap两倍的内存,所以要把一个已经压缩好的bitmap放进去,如果没有转换成功就返回原来的bitmap
     * @param bitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees, boolean destroySource) {
        if (degrees == 0) return bitmap;
        Log.i("Alex","准备旋转bitmap，"+"   宽度是"+bitmap.getHeight()+"   高度是"+bitmap.getHeight()+"   角度是"+degrees);
        try {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (null != bitmap && destroySource) bitmap.recycle();
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Alex","旋转bitmap出现异常");
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.i("Alex","旋转bitmap出现oom异常");
            return bitmap;
        }
    }

    /**
     *  异步加载本地图片的暴露方法
     * @param uri 图片的本地路径
     * @param imageView
     * @param imageViewWidth 如果想要imageView大小随图片文件自适应全显示的话，需要给一个imageView的目标宽度
     * @param resizeImageView 是否要根据图像的宽高比例重设imageView的宽高比例，也就是让imageView的高度自适应
     * @param autoRotate 是否根据EXIF信息旋转图片
     * @param storeThumbnail 是否保存缩略图，第二次浏览同一张图片会变快
     */
    public void setAsyncBitmapFromSD(String uri, final ImageView imageView, int imageViewWidth, boolean resizeImageView, boolean autoRotate, boolean storeThumbnail) {
        //从LRU缓存里获取bitmap
        if(uri!=null) currentUrls.put(imageView,uri);//把url绑定在imageView上，用来防止显示缓存错误
        else currentUrls.put(imageView,"");
        imageView.setImageDrawable(null);//清空上次的显示
        final Bitmap cacheBitmap = loadBitmapFromSD(uri, imageView,imageViewWidth,resizeImageView,autoRotate,storeThumbnail,

                new ImageCallback() {
                    @Override
                    public void imageLoaded(final Bitmap imageBitmap, final ImageView imageView, String imageUrl) {
                        Log.i("Alex","加载成功的bitmap宽高是"+imageBitmap.getWidth()+" x "+imageBitmap.getHeight());
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(imageBitmap);
                            }
                        });
                    }
                });
        if(cacheBitmap!=null) {
            imageView.setImageBitmap(cacheBitmap);
            Log.i("Alex","缓存的bitmap是"+cacheBitmap.getWidth()+"   ::"+cacheBitmap.getHeight());
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if(resizeImageView && params!=null && imageViewWidth>0 && cacheBitmap!=defbitmap) {//只有当现在缓存里的的bitmap不是默认bitmap的时候才重新修改大小，因为根据默认bitmap重设大小是没有意义的
                int height = cacheBitmap.getHeight()* imageViewWidth / cacheBitmap.getWidth() ;
                Log.i("Alex","准备重设高度haha"+height);
                params.height = height;
                imageView.setLayoutParams(params);
            }
        }else {
            Log.i("Alex","缓存的bitmap为空");
        }
    }

    /**
     * 保存一个缩略图到sd卡，这样在selectPhoto的时候，第二次加载同一张图片就会变快
     * @param bitmap
     * @return
     */
    public static boolean storeThumbnail(Context context, String fileName, Bitmap bitmap){
        if(bitmap==null)return false;
        File file = new File(context.getCacheDir().getAbsolutePath().concat("/"+fileName));
        if(!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        else return true;
        Log.i("Alex","准备存储缩略图");
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(out!=null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
