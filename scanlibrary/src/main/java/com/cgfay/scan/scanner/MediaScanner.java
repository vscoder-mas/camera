package com.cgfay.scan.scanner;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.cgfay.scan.model.AlbumItem;

import java.lang.ref.WeakReference;

/**
 * 相册媒体扫描器
 */
public class MediaScanner implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ALBUM_ARGS = "album_args";
    private static final String CAPTURE_ENABLE_ARGS = "capture_enable_args";

    private WeakReference<Context> mWeakContext;
    private LoaderManager mLoaderManager;
    private MediaScanCallbackListener callbackListener;

    public MediaScanner(FragmentActivity activity) {
        mWeakContext = new WeakReference<Context>(activity);
        mLoaderManager = activity.getSupportLoaderManager();
    }

    public void setCallbackListener(MediaScanCallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mWeakContext.get();
        if (context == null) {
            return null;
        }
        AlbumItem albumItem = args.getParcelable(ALBUM_ARGS);
        if (albumItem == null) {
            return null;
        }
        return MediaCursorLoader.newInstance(context, albumItem, args.getBoolean(CAPTURE_ENABLE_ARGS, false));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mWeakContext.get();
        if (context == null) {
            return;
        }
        if (callbackListener != null) {
            callbackListener.onMediaScanFinish(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (callbackListener != null) {
            callbackListener.onMediaScanReset();
        }
    }

    public void destroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        callbackListener = null;
    }

    /**
     * 扫描目标相册
     *
     * @param target 目标相册
     */
    public void scanAlbum(AlbumItem target) {
        scanAlbum(target, false);
    }

    /**
     * 扫描目标相册
     *
     * @param target        目标相册
     * @param enableCapture 是否允许拍照项
     */
    public void scanAlbum(AlbumItem target, boolean enableCapture) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ALBUM_ARGS, target);
        bundle.putBoolean(CAPTURE_ENABLE_ARGS, enableCapture);
        mLoaderManager.initLoader(LOADER_ID, bundle, this);
    }

    /**
     * 重新扫描相册
     *
     * @param target
     * @param enableCapture
     */
    public void reScanAlbum(AlbumItem target, boolean enableCapture) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ALBUM_ARGS, target);
        bundle.putBoolean(CAPTURE_ENABLE_ARGS, enableCapture);
        mLoaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    /**
     * 媒体扫描回调
     */
    public interface MediaScanCallbackListener {
        void onMediaScanFinish(Cursor cursor);

        void onMediaScanReset();
    }
}
