package com.binding.model.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by pc on 2017/8/21.
 */

public class FileUtil {
    static final String LOG_TAG = "FileUtils";

    /**
     * 操作成功返回值
     */
    public static final int SUCCESS = 0;

    /**
     * 操作失败返回值
     */
    public static final int FAILED = -1;

    private static final int BUF_SIZE = 32 * 1024; // 32KB

//    public static final int S_IRWXU = 00700;
//    public static final int S_IRUSR = 00400;
//    public static final int S_IWUSR = 00200;
//    public static final int S_IXUSR = 00100;
//
//    public static final int S_IRWXG = 00070;
//    public static final int S_IRGRP = 00040;
//    public static final int S_IWGRP = 00020;
//    public static final int S_IXGRP = 00010;
//
//    public static final int S_IRWXO = 00007;
//    public static final int S_IROTH = 00004;
//    public static final int S_IWOTH = 00002;
//    public static final int S_IXOTH = 00001;

    private static WeakReference<Exception> exReference;

    public static long getFileSize(File file) {
        FileInputStream stream = null;
        long size = 0;
        if (file.exists() && file.isFile())
            try {
                stream = new FileInputStream(file);
                size = stream.getChannel().size();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(stream);
            }
        return size;
    }

    public static void close(Closeable stream) {
        try {
            if (stream != null) stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFileMD5(File file) {
        if (!file.isFile()) return null;
        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            int len;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
    /**
     * 文件类型枚举
     */
    public static enum FileState {
        FState_Dir("I am director!"), // 目录
        FState_File("I am file!"), // 文件
        FState_None("I am activity ghost!"), // 不存在
        FState_Other("I am not human!"); // 其他类型

        private String tag;

        private FileState(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    private FileUtil() {
    }

    /**
     * 获取文件状态
     *
     * @param path
     * @return
     */
    public static FileState fileState(String path) {
        return fileState(new File(path));
    }

    public static FileState fileState(File file) {
        if (!file.exists())
            return FileState.FState_None;

        if (file.isFile())
            return FileState.FState_File;

        if (file.isDirectory())
            return FileState.FState_Dir;

        return FileState.FState_Other;
    }

    public static String createPathDir(String dirPath,String path) {
        StringBuilder builder = new StringBuilder();
        builder.append(dirPath);
        if (path.startsWith(dirPath)) {
            path = path.replace(dirPath + File.separatorChar, "");
        }
        String[] dirs = path.split(File.separator);
        for (String dir : dirs) {
            builder.append(File.separatorChar);
            builder.append(dir);
            if (createDir(new File(builder.toString())) == FAILED) {
                return "";
            }
        }
        return builder.toString();
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static int createDir(String path) {
        int l = path.length();
        if (path.charAt(l - 1) == File.separatorChar) { //如果末尾是 /
            path = path.substring(0, l - 1);
        }
        return createDir(new File(path));
    }

    public static int createDir(File file) {
        if (file.exists()) {
            if (file.isDirectory())
                return SUCCESS;
            if (!file.delete()) return FAILED;// 避免他是一个文件存在
        }
        if (file.mkdirs())
            return SUCCESS;
        return FAILED;
    }

    /**
     * @see {@link #checkParentPath(File)}
     */
    public static void checkParentPath(String path) {
        checkParentPath(new File(path));
    }

    /**
     * 在打开一个文件写数据之前，先检测该文件路径的父目录是否已创建，保证能创建文件
     *
     * @param file
     */
    public static void checkParentPath(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory())
            createDir(parent);
    }


    public static int removeDir(String path) {
        return removeDir(new File(path));
    }

    /**
     * 删除一个文件夹
     * <p>
     * <p>
     * by:yichou 2013-5-7 15:24:41
     * <p>
     *
     * @param dir
     * @return
     */
    public static int removeDir(File dir) {
        if (!dir.exists())
            return SUCCESS;

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory())
                        removeDir(f);
                    else
                        f.delete();
                }
            }
        }

        return dir.delete() ? SUCCESS : FAILED;
    }


    /**
     * 将一缓冲流写入文件
     *
     * @param path     目标文件路径
     * @param is       输入流
     * @param isAppend 是否追加
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int streamToFile(String path, InputStream is, boolean isAppend) {
        return streamToFile(new File(path), is, isAppend);
    }

    public static int streamToFile(File file, InputStream is, boolean isAppend) {
        checkParentPath(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            byte[] buf = new byte[BUF_SIZE];
            int readSize = 0;
            while ((readSize = is.read(buf)) != -1)
                fos.write(buf, 0, readSize);
            fos.flush();
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(fos);
        }
        return FAILED;
    }

    /**
     * 写字节数组到文件
     *
     * @param file     目标文件
     * @param data     字节数组
     * @param offset   偏移 {@code >=0&&<=data.length}
     * @param length   长度 ==0 表示 data.length
     * @param isAppend 是否追加
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int bytesToFile(File file, byte[] data, int offset,
                                  int length, boolean isAppend) {
        checkParentPath(file);

        if (data == null)
            return FAILED;

        if (length <= 0)
            length = data.length;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, isAppend);
            fos.write(data, offset, length);
            fos.flush();

            return SUCCESS;
        } catch (Exception e) {
        } finally {
            try {
                close(fos);
            } catch (Exception e) {
            }
        }

        return FAILED;
    }

    public static int bytesToFile(File file, byte[] data, boolean isAppend) {
        return bytesToFile(file, data, 0, data.length, isAppend);
    }

    public static int bytesToFile(File file, byte[] data) {
        return bytesToFile(file, data, 0, data.length, false);
    }

    public static int stringToFile(File file, String string) {
        return bytesToFile(file, string.getBytes());
    }

    /**
     * @see {@link #bytesToFile(File file, byte[] data, int offset, int length, boolean isAppend)}
     */
    public static int bytesToFile(String path, byte[] data, int offset,
                                  int length, boolean isAppend) {
        return bytesToFile(new File(path), data, offset, length, isAppend);
    }

    /**
     * 读取文件内容到二进制缓冲区
     *
     * @param path   文件路径
     * @param offset 起始位置
     * @param length 读取长度 ，0为全部
     * @return 失败 或 length <=0 返回null，成功返回 字节数组
     */
    public static byte[] fileToBytes(String path, int offset, int length) {
        return fileToBytes(new File(path), offset, length);
    }

    public static byte[] fileToBytes(File file) {
        return fileToBytes(file, 0, 0);
    }

    public static String fileToString(File file) {
        byte[] data = fileToBytes(file);
        return data != null ? new String(data) : null;
    }

    /**
     * 读取文件内容到二进制缓冲区
     *
     * @param file   文件路径
     * @param offset 起始位置
     * @param length 读取长度，==0 为全部
     * @return 失败 或 length < 0 返回null，成功返回 字节数组
     */
    public static byte[] fileToBytes(File file, int offset, int length) {
        if (length < 0 || !file.exists())
            return null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            if (length == 0) length = is.available();
            byte[] outBuf = new byte[length];
            is.read(outBuf, offset, length);
            return outBuf;
        } catch (Exception e) {
        } finally {
            close(is);
        }

        return null;
    }

    /**
     * 复制文件, 对于大的文件, 推荐开启一个线程来复制. 防止长时间阻塞
     *
     * @param dstPath
     * @param srcPath
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int copyTo(String dstPath, String srcPath) {
        return copyTo(new File(dstPath), new File(srcPath));
    }

    public static int copyTo(File dstFile, File srcFile) {
        if (fileState(srcFile) != FileState.FState_File) // 源非文件
            return FAILED;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(srcFile);

            return streamToFile(dstFile, fis, false);
        } catch (Exception e) {
        } finally {
            close(fis);
        }

        return FAILED;
    }

    /**
     * @see {@link #assetToFile(Context context, String assetName, File file)}
     */
    public static int assetToFile(Context context, String assetName, String path) {
        return assetToFile(context, assetName, new File(path));
    }

    /**
     * assets 目录下的文件保存到本地文件
     *
     * @param context
     * @param assetName assets下名字，非根目录需包含路径 activity/b.xxx
     * @param targetFile      目标文件
     * @return 成功 {@link #SUCCESS}； 失败 {@link #FAILED}
     */
    public static int assetToFile(Context context, String assetName, File targetFile) {
        InputStream is = null;
        try {
            is = context.getAssets().open(assetName);
            return streamToFile(targetFile, is, false);
        } catch (Exception e) {
        } finally {
            close(is);
        }

        return FAILED;
    }

    public static int assetToFileIfNotExist(Context context, String assetName, File file) {
        InputStream is = null;
        try {
            is = context.getAssets().open(assetName);
            if (!checkExistBySize(file, is.available())) {
                return streamToFile(file, is, false);
            } else {
                return SUCCESS;
            }
        } catch (Exception e) {
        } finally {
            close(is);
        }

        return FAILED;
    }

    /**
     * 读取 assets 下 name 文件返回字节数组
     *
     * @param context
     * @param name
     * @return 失败返回 Null
     */
    public static byte[] assetToBytes(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getAssets().open(name);
            byte[] buf = new byte[is.available()];
            is.read(buf);

            return buf;
        } catch (Exception e) {
            setLastException(e);
        } finally {
            try {
                close(is);
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * 从 Assets 文件读取文件全部，并转为字符串
     *
     * @param context
     * @param name    文件名
     * @return 读取到的字符串
     * @author Yichou
     * <p>
     * date 2013-4-2 11:30:05
     */
    public static String assetToString(Context context, String name) {
        byte[] data = assetToBytes(context, name);

        return data != null ? new String(data) : null;
    }

    /**
     * 检查 assets 下是否存在某文件
     *
     * @param am
     * @param name
     * @return
     */
    public static boolean assetExist(AssetManager am, String name) {
        InputStream is = null;
        try {
            is = am.open(name);
            return true;
        } catch (IOException e) {
        } finally {
            close(is);
        }

        return false;
    }

    /**
     * @return SD卡是否已挂载
     */
    public static boolean isSDMounted() {
        String sdState = Environment.getExternalStorageState(); // 判断sd卡是否存在
        return sdState.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 2013-9-27 check if the sdcard mounted and can read and wirte, and remain size >
     *
     * @param minRemainMB unit mb
     */
    public static boolean isSDAvailable(int minRemainMB) {
        if (!isSDAvailable())
            return false;

        return (getSDLeftSpace() >= minRemainMB * 1024L * 1024L);
    }

    /**
     * 2013-9-27 check if the sdcard mounted and can read and wirte
     */
    public static boolean isSDAvailable() {
        if (!isSDMounted())
            return false;

        File file = Environment.getExternalStorageDirectory();
        if (!file.canRead() || !file.canWrite())
            return false;

        return true;
    }

    /**
     * @return SD卡剩余容量
     */
    public static long getSDLeftSpace() {
        if (isSDMounted() == false) {
            return 0;
        } else {
            StatFs statfs = new StatFs(
                    Environment.getExternalStorageDirectory() + File.separator);
            return (long) statfs.getAvailableBlocks()
                    * (long) statfs.getBlockSize();
        }
    }

    public static String coverSize(long size) {
        String s = "";
        if (size < 1024)
            s += size + "b";
        else if (size < 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.1fK", size / 1024f);
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.1fM", size / 1024 / 1024f);
        } else {
            s = String.format(Locale.getDefault(), "%.1fG", size / 1024 / 1024 / 1024f);
        }

        return s;
    }

    public static long getROMLeft() {
        File data = Environment.getDataDirectory();

        StatFs sf = new StatFs(data.getAbsolutePath());
        long blockSize = sf.getBlockSize();
        long blockCount = sf.getBlockCount();
        long availCount = sf.getAvailableBlocks();

        Log.i("", "ROM Total:" + coverSize(blockSize * blockCount) + ", Left:"
                + coverSize(availCount * blockSize));

        return availCount * blockSize;
    }

    /**
     * 获取私有目录下的文件夹绝对路径，末尾带 "/"，不创建
     *
     * @param context
     * @param name    文件夹名
     * @return
     */
    public static String getDirPathInPrivate(Context context, String name) {
        return context.getDir(name, Context.MODE_PRIVATE).getAbsolutePath()
                + File.separator;
    }

    /**
     * 或者本应用 so 存放路径
     *
     * @param context
     * @return
     */
    public static String getSoPath(Context context) {
        return context.getApplicationInfo().dataDir + "/lib/";
    }

    public static FileLock tryFileLock(String path) {
        return tryFileLock(new File(path));
    }

    /**
     * 占用某个文件锁
     *
     * @param file
     * @return
     */
    public static FileLock tryFileLock(File file) {
        try {
            checkParentPath(file); // 父目录不存在会导致创建文件锁失败

            FileOutputStream fos = new FileOutputStream(file);
            FileLock fl = fos.getChannel().tryLock();
            if (fl.isValid()) {
                Log.i(LOG_TAG, "tryFileLock " + file + " SUC!");
                return fl;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryFileLock " + file + " FAIL! " + e.getMessage());
        }

        return null;
    }

    public static void freeFileLock(FileLock fl, File file) {
        if (file != null)
            file.delete();

        if (fl == null || !fl.isValid())
            return;

        try {
            fl.release();
            Log.i(LOG_TAG, "freeFileLock " + file + " SUC!");
        } catch (IOException e) {
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory()) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 删除空目录
     * <p>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    public static void deleteAllFiles(String path){
        deleteAllFiles(new File(path));
    }

    public static boolean deleteAllFiles(File file) {
        if(!file.exists())return false;
        if(file.isFile())return file.delete();
        if(file.isDirectory()){
            for(File f: file.listFiles()){
                deleteAllFiles(f);
            }
            return file.delete();
        }
        return false;
    }

    /**
     * 获取SD卡的根目录，末尾带\
     *
     * @return
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 2013-10-8 by yichou
     * <p>
     * 检查一个文件本地是否存在，通过名称，长度，如果2者都符合，返回 true，否则返回 false
     *
     * @param file
     * @param size
     */
    public static boolean checkExistBySize(File file, long size) {
        if (!file.exists() || !file.isFile() || (file.length() != size))
            return false;

        return true;
    }

    //public static native int setPermissions(String file, int mode, int uid, int gid);
    public static int setPermissions(String file, int mode) {
        return setPermissions(file, mode, -1, -1);
    }

    private static final Class<?>[] SIG_SET_PERMISSION =
            new Class<?>[]{String.class, int.class, int.class, int.class};

    public static int setPermissions(String file, int mode, int uid, int gid) {
        try {
            Class<?> clazz = Class.forName("android.os.FileUtils");
            Method method = clazz.getDeclaredMethod("setPermissions", SIG_SET_PERMISSION);
            method.setAccessible(true);
            return (Integer) method.invoke(null, file, mode, uid, gid);
        } catch (Exception e) {
        }

        return -1;
    }

    /**
     * 把 sd卡上 src 目录 链接到 私有目录 dst
     * <p>
     * <p>例：createLink("/mnt/sdcard/freesky", "/data/data/com.test/app_links/free")
     * 之后 /data/data/com.test/app_links/free -> /mnt/sdcard/freesky
     *
     * @param src 源目录，在SD卡上
     * @param dst 目标路径完整
     * @return
     */
    public static boolean createLink(String src, String dst) {
        try {
            String command = String.format("ln -s %s %s", src, dst);
            Runtime runtime = Runtime.getRuntime();
            Process ps = runtime.exec(command);
            InputStream in = ps.getInputStream();

            int c;
            while ((c = in.read()) != -1) {
                System.out.print(c);// 如果你不需要看输出，这行可以注销掉
            }

            in.close();
            ps.waitFor();

            return true;
        } catch (Exception e) {
        }

        return false;
    }

//    /**
//     * 读取输入流全部内容到字节数组
//     *
//     * @param is 输入流，传入者关闭
//     *
//     * @return 成功 数组，失败 null
//     *
//     * 2014-9-26
//     */
//    public static ByteArrayBuffer streamToByteArray(InputStream is) {
//        try {
//            byte[] buf = new byte[256];
//            int read = 0;
//            ByteArrayBuffer array = new ByteArrayBuffer(1024);
//
//            while ((read = is.read(buf)) != -1) {
//                array.append(buf, 0, read);
//            }
//
//            return array;
//        } catch (Exception e) {
//            setLastException(e);
//        }
//
//        return null;
//    }
//    /**
//     * 读取输入流全部，转为字符串
//     *
//     * @param is 输入流，传入者关闭
//     *
//     * @return 成功 字串，失败 null
//     *
//     * 2014-9-26
//     */
//    public static String streamToString(InputStream is) {
//        ByteArrayBuffer buffer = streamToByteArray(is);
//        if(buffer != null)
//            return new String(buffer.buffer(), 0 , buffer.length());
//
//        return null;
//    }

    public static void printLastException() {
        Exception e = getLastException();
        if (e != null)
            e.printStackTrace();
    }

    private static void setLastException(Exception e) {
        exReference = new WeakReference<Exception>(e);
    }

    public static Exception getLastException() {
        return exReference != null ? exReference.get() : null;
    }


    public static void copy(String path, String newpath) throws IOException {
        File filePath = new File(path);
        DataInputStream read;
        DataOutputStream write;
        if (filePath.isDirectory()) {
            File[] list = filePath.listFiles();
            for (int i = 0; i < list.length; i++) {
                String newPath = path + File.separator + list[i].getName();
                String newCopyPath = newpath + File.separator + list[i].getName();
                copy(newPath, newCopyPath);
            }
        } else if (filePath.isFile()) {
            read = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(path)));
            write = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(newpath)));
            byte[] buf = new byte[1024 * 512];
            while (read.read(buf) != -1) {
                write.write(buf);
            }
            read.close();
            write.close();
        } else {
            System.out.println("请输入正确的文件名或路径名");
        }
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        if (cursor != null) cursor.close();
        return res;
    }

    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 含子目录的文件压缩
     *
     * @throws Exception
     */
    public static boolean upZipFile(String zipFile, String folderPath) {
        ZipFile zfile = null;
        try {
            zfile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            try {
                is = new BufferedInputStream(zfile.getInputStream(ze));
            } catch (IOException e) {
                return false;
            }
            int readLen = 0;
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            zfile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir
     *            指定根目录
     * @param absFileName
     *            相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        absFileName = absFileName.replace("\\", "/");
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }




    /**
     * 获取缓存大小
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     * @param context
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
