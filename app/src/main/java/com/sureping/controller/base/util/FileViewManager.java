package com.sureping.controller.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.binding.model.util.FileUtil;
import com.sureping.controller.BuildConfig;
import com.sureping.controller.base.cycle.BaseActivity;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FileViewManager extends BaseActivity {
    private static Consumer<File> consumer;
    private static @FileType
    String type;
    private static File photoFile;
    int REQUEST_CAMERA = 0;
    int REQUEST_PHOTO = 1;
    int REQUEST_PHOTO_KITKAT = 2;
    public final static String image = "image/*";//选择图片
    public final static String audio = "audio/*"; //选择音频
    public final static String video = "video/*"; //选择视频 （mp4 3gp 是android支持的视频格式）
    public final static String video_image = "video/*;image/*";//同时选择视频和图片
    public final static String all = "*/*";//无类型限制
    public final static String takePhoto = "none";

    @Override
    protected int getViewLayout() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Disposable subscribe = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(
                        aBoolean -> {
                            if (!aBoolean) {
                                toast("权限申请失败，请手动打开");
                                return;
                            } else {
                                if (!takePhoto.equals(type)) {
                                    openFileManager(type);
                                } else {
                                    photoFile = takePhoto();
                                }
                            }
                        }
                );
    }

    public void openFileManager(@FileType String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    public static void selectFile(Context context, Consumer<File> consumer, @FileType String type) {
        Intent intent = new Intent(context, FileViewManager.class);
        context.startActivity(intent);
        FileViewManager.consumer = consumer;
        FileViewManager.type = type;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                consumer.accept(photoFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            if (consumer != null && uri != null) {
                try {
                    String path = UriTofilePath.getFilePathByUri(this, uri);
                    consumer.accept(new File(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }

    private static class UriTofilePath {
        public static String getFilePathByUri(Context context, Uri uri) {
            String path = null;
            // 以 file:// 开头的
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                path = uri.getPath();
                return path;
            }
            // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (columnIndex > -1) {
                            path = cursor.getString(columnIndex);
                        }
                    }
                    cursor.close();
                }
                return path;
            }
            // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        // ExternalStorageProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            path = Environment.getExternalStorageDirectory() + "/" + split[1];
                            return path;
                        }
                    } else if (isDownloadsDocument(uri)) {
                        // DownloadsProvider
                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
                        path = getDataColumn(context, contentUri, null, null);
                        return path;
                    } else if (isMediaDocument(uri)) {
                        // MediaProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        path = getDataColumn(context, contentUri, selection, selectionArgs);
                        return path;
                    }
                }
            }
            path = FileUtil.getRealPathFromURI(context, uri);
            if (TextUtils.isEmpty(path)) {
                path = FileUtil.getImageAbsolutePath(context, uri);
            }
            return path;
        }

        private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }

        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        private static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FileViewManager.image,
            FileViewManager.audio,
            FileViewManager.video,
            FileViewManager.video_image,
            FileViewManager.all,
            FileViewManager.takePhoto
    })
    public @interface FileType {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoFile = null;
        consumer = null;
        type = null;
    }

    public  File takePhoto() {
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/company/" + System.currentTimeMillis() + ".jpg");
        Disposable disposable = rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (!imageFile.getParentFile().exists()) {
                        imageFile.getParentFile().mkdirs();
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".filterProvider", imageFile));
                    this.startActivityForResult(intent, REQUEST_CAMERA);
                });
        return imageFile;
    }
}
