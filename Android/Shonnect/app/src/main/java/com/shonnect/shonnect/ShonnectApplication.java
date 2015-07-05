package com.shonnect.shonnect;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.Parse;

import android.os.Environment;

import java.io.File;

import io.branch.referral.BranchApp;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public class ShonnectApplication extends BranchApp {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        configImageLoader();
        configParse();
    }

    private void configParse() {
        Parse.initialize(this, "sDe87NsGCpLHg1yv2iAF7RKxYa1mXrN8oVqMhVUG", "vR61BMnNYyKTPGmDmVBa59a9GL9va4OQP6kRIm31");
    }

    private void configImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        if (cacheDir == null) {
            cacheDir = Environment.getDownloadCacheDirectory();
        }

        // TODO config image resolution cache for low end devices

        DisplayImageOptions options = getDefaultDisplayOptionsBuilder().build();
        int maxWidth = 800;
        int maxHeight = 480;
        int threadPoolSize = 1;

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(threadPoolSize)
                .memoryCacheExtraOptions(maxWidth, maxHeight)
                .memoryCache(new LRULimitedMemoryCache(3 * 1024 * 1024))
                .diskCacheExtraOptions(maxWidth, maxHeight, null)
                .diskCache(new LimitedAgeDiskCache(cacheDir, 3600 * 24 * 7))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(getApplicationContext()))
                .defaultDisplayImageOptions(options);

        ImageLoaderConfiguration config = builder.build();
        ImageLoader mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    public static DisplayImageOptions.Builder getDefaultDisplayOptionsBuilder() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY);

        return builder;
    }


}
