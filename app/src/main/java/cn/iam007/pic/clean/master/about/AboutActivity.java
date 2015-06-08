package cn.iam007.pic.clean.master.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;

/**
 * Created by Administrator on 2015/6/8.
 */
public class AboutActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    AboutRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        // init CardView
        mRecyclerView = (RecyclerView) findViewById(R.id.cardListView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AboutRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        initAdapter();
    }

    private void initAdapter() {
        _initAdatperHeader();
        _initAdapterLibraries();
    }

    private void _initAdatperHeader() {
        //get the packageManager to load and read some values :D
        PackageManager pm = getPackageManager();
        //get the packageName
        String packageName = getPackageName();
        //Try to load the applicationInfo
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (Exception ex) {
        }

        //set the Version or hide it
        String versionName = null;
        Integer versionCode = null;
        if (packageInfo != null) {
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        }

        //add this cool thing to the headerView of our listView
        mAdapter.setHeader(versionName, versionCode, null);
        mAdapter.setDescription("this is the app's description, can be html format.");
    }

    private void _initAdapterLibraries() {
        /**
         <?xml version="1.0" encoding="utf-8"?>
         <resources>

         <string name="define_int_NineOldAndroids">year;owner</string>
         <string name="library_NineOldAndroids_author">Jake Wharton</string>
         <string name="library_NineOldAndroids_authorWebsite">http://jakewharton.com/</string>
         <string name="library_NineOldAndroids_libraryName">NineOldAndroids</string>
         <string name="library_NineOldAndroids_libraryDescription">Android library for using the Honeycomb (Android 3.0) animation API on all versions of the
         platform
         back to 1.0!
         </string>
         <string name="library_NineOldAndroids_libraryVersion">2.4.0</string>
         <string name="library_NineOldAndroids_libraryWebsite">http://nineoldandroids.com/</string>
         <string name="library_NineOldAndroids_licenseId">apache_2_0</string>
         <string name="library_NineOldAndroids_isOpenSource">true</string>
         <string name="library_NineOldAndroids_repositoryLink">https://github.com/JakeWharton/NineOldAndroids</string>
         <string name="library_NineOldAndroids_classPath">com.nineoldandroids.view.ViewHelper</string>
         <!-- Custom variables section -->
         <string name="library_NineOldAndroids_owner">Jake Wharton</string>
         <string name="library_NineOldAndroids_year">2014</string>
         </resources>
         */
        String author = "Jake Wharton";
        String authorWebsite = "http://jakewharton.com/";
        String libraryName = "NineOldAndroids";
        String libraryDescription =
                "Android library for using the Honeycomb (Android 3.0) animation API on all versions of the" +
                        " platform back to 1.0!";
        String libraryVersion = "2.4.0";
        String libraryWebsite = "http://nineoldandroids.com";
        boolean isOpenSource = true;
        String repositoryLink = "https://github.com/JakeWharton/NineOldAndroids";

        AboutLibrary library = new AboutLibrary(author, libraryName, libraryDescription);
        library.setAuthor(author);
        library.setAuthorWebsite(authorWebsite);
        library.setLibraryVersion(libraryVersion);
        library.setLibraryWebsite(libraryWebsite);
        library.setOpenSource(isOpenSource);
        library.setRepositoryLink(repositoryLink);

        ArrayList<AboutLibrary> libraries = new ArrayList<>();
        libraries.add(library);
        mAdapter.setLibs(libraries);
    }
}
