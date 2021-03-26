package com.cgfay.scan.scanner;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;

/**
 * 相册扫描器
 */
public class AlbumScanner implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private WeakReference<Context> mWeakContext;
    private LoaderManager mLoaderManager;
    private int mCurrentSelection;
    private boolean mLoadFinished;
    private AlbumScanCallbackListener callbackListener;

    public AlbumScanner(FragmentActivity activity) {
        mWeakContext = new WeakReference<Context>(activity);
        mLoaderManager = activity.getSupportLoaderManager();
    }

    public void setCallbackListener(AlbumScanCallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mWeakContext.get();
        if (context == null) {
            return null;
        }

        mLoadFinished = false;
        return AlbumCursorLoader.newInstance(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mWeakContext.get();
        if (context == null) {
            return;
        }
        if (!mLoadFinished) {
            mLoadFinished = true;
            if (callbackListener != null) {
                callbackListener.onAlbumScanFinish(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mWeakContext.get();
        if (context == null) {
            return;
        }
        if (callbackListener != null) {
            callbackListener.onAlbumScanReset();
        }
    }

    /**
     * 销毁加载器
     */
    public void destroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        callbackListener = null;
    }

    /**
     * 扫描全部相册内容
     */
    public void scanAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    /**
     * 扫描回调
     */
    public interface AlbumScanCallbackListener {
        void onAlbumScanFinish(Cursor cursor);

        void onAlbumScanReset();
    }
}
