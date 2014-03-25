/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 lacolaco.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.lacolaco.smileessence.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import net.lacolaco.smileessence.R;
import net.lacolaco.smileessence.preference.PreferenceHelper;
import net.lacolaco.smileessence.property.PropertyHelper;
import net.lacolaco.smileessence.resource.ResourceHelper;
import net.lacolaco.smileessence.twitter.TwitterApi;
import net.lacolaco.smileessence.view.adapter.TextFragment;
import net.lacolaco.smileessence.viewmodel.PageListAdapter;

import java.io.IOException;

public class MainActivity extends Activity
{

    private static final String STATE_PAGE = "page";
    private ResourceHelper resourceHelper;
    private PreferenceHelper preferenceHelper;
    private PropertyHelper propertyHelper;
    private ViewPager viewPager;
    private PageListAdapter pagerAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        initializeViewPager(savedInstanceState);
        try
        {
            setupHelpers();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            finish();
        }
    }

    private void initializeViewPager(Bundle savedInstanceState)
    {
        ActionBar bar = getActionBar();
        bar.setTitle(null);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        pagerAdapter = new PageListAdapter(this, viewPager);
        Bundle args = new Bundle();
        args.putString(TextFragment.ARG_TEXT, "test");
        pagerAdapter.addTabWithoutNotify("Post", TextFragment.class, args);
        pagerAdapter.addTabWithoutNotify("Home", TextFragment.class, args);
        pagerAdapter.addTabWithoutNotify("Mentions", TextFragment.class, args);
        pagerAdapter.addTabWithoutNotify("Messages", TextFragment.class, args);
        pagerAdapter.addTabWithoutNotify("History", TextFragment.class, args);
        pagerAdapter.notifyDataSetChanged();
        if(savedInstanceState != null)
        {
            bar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_PAGE, 1));
        }
        else
        {
            bar.setSelectedNavigationItem(1); //Home
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //account check
        TwitterApi api = getLastUsedAccount();
        if(api == null)
        {
            //Authorize
        }
        else
        {
            //Login and initialize
        }
    }

    private TwitterApi getLastUsedAccount()
    {
        String token = propertyHelper.getValue("oauth.accessToken");
        String secret = propertyHelper.getValue("oauth.accessSecret");
        if(TextUtils.isEmpty(token) || TextUtils.isEmpty(secret))
        {
            return null;
        }
        else
        {
            return new TwitterApi(token, secret);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onStop()
    {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private String getVersion()
    {
        return resourceHelper.getString(R.string.app_version);
    }

    private String getLastLaunchVersion()
    {
        return propertyHelper.getValue("app.version");
    }

    public boolean IsFirstLaunchByVersion()
    {
        return !getVersion().contentEquals(getLastLaunchVersion());
    }

    private void setupHelpers() throws IOException
    {
        resourceHelper = new ResourceHelper(this);
        preferenceHelper = new PreferenceHelper(this);
        propertyHelper = new PropertyHelper(this.getAssets(), "app.properties");
    }

    public ResourceHelper getResourceHelper()
    {
        return resourceHelper;
    }

    public PreferenceHelper getPreferenceHelper()
    {
        return preferenceHelper;
    }

    public PropertyHelper getPropertyHelper()
    {
        return propertyHelper;
    }

    public ViewPager getViewPager()
    {
        return viewPager;
    }

    public PagerAdapter getPagerAdapter()
    {
        return pagerAdapter;
    }
}
