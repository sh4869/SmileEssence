package net.miz_hi.smileessence.data;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.async.AsyncIconGetter;
import net.miz_hi.smileessence.async.MyExecutor;
import net.miz_hi.smileessence.core.UiHandler;
import net.miz_hi.smileessence.util.CountUpInteger;
import net.miz_hi.smileessence.util.LogHelper;
import net.miz_hi.smileessence.util.StringUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;

public class IconCaches
{

	private static ConcurrentHashMap<Long, Icon> iconCache = new ConcurrentHashMap<Long, Icon>();
	private static ConcurrentHashMap<Long, Future<Bitmap>> futureMap = new ConcurrentHashMap<Long, Future<Bitmap>>();
	private static File cacheDir = Client.getApplication().getExternalCacheDir();
	private static Bitmap emptyIcon;
	private static CountUpInteger counter = new CountUpInteger(5);

	public static Icon getIcon(long id)
	{
		return iconCache.get(id);
	}
	
	public static void checkIconCache(final UserModel user)
	{
		final String fileName = genIconName(user);
		final File latestIconFile = Client.getApplicationFile(fileName);
		/*
		 * URL����A�C�R�����X�V����Ă��邩�ǂ����̊m�F
		 */
		boolean needsCacheUpdate = true;
		if (iconCache.containsKey(user.userId))
		{
			needsCacheUpdate = !iconCache.get(user.userId).fileName.equals(fileName);
		}
		else
		{
			needsCacheUpdate = !latestIconFile.exists();
		}

		/*
		 * �X�V���s�v
		 */
		if (!needsCacheUpdate)
		{
			if (!iconCache.containsKey(user.userId) && !futureMap.containsKey(user.userId))
			{
				Options opt = new Options();
				opt.inPurgeable = true; // GC�\�ɂ���
				Bitmap bm = BitmapFactory.decodeFile(latestIconFile.getPath(), opt);
				Icon icon = new Icon(bm, fileName);
				IconCaches.putIconToMap(user.userId, icon);	
			}
		}
		else
		{
			futureMap.put(user.userId, MyExecutor.submit(new AsyncIconGetter(user)));
		}
	}

	public synchronized static void setIconBitmapToView(final UserModel user, final ImageView viewIcon)
	{
		if(futureMap.containsKey(user.userId))
		{
			viewIcon.setImageBitmap(getEmptyIcon());
			final Future<Bitmap> f = futureMap.remove(user.userId);
			MyExecutor.execute(new Runnable()
			{
				
				@Override
				public void run()
				{
					try
					{
						final Bitmap bm = f.get();
						new UiHandler()
						{
							
							@Override
							public void run()
							{			
								if(viewIcon.getTag() == (Long)user.userId)
								{
									viewIcon.setImageBitmap(bm);
									viewIcon.invalidate();
								}
							}
						}.post();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}					
				}
			});
		}
		else if (iconCache.containsKey(user.userId))
		{
			Icon icon = iconCache.get(user.userId);
			viewIcon.setImageBitmap(icon.use());
		}
		else
		{
			viewIcon.setImageBitmap(getEmptyIcon());
			MyExecutor.execute(new Runnable()
			{
				
				@Override
				public void run()
				{
					checkIconCache(user);
				}
			});
		}
	}

	public static String genIconName(UserModel user)
	{
		return String.format("%1$s_%2$s", user.userId, StringUtils.parseUrlToFileName(user.iconUrl));
	}

	public static void clearCache()
	{
		iconCache.clear();
	}

	public static void putIconToMap(long id, Icon icon)
	{

		if (iconCache.size() > 500 && counter.isOver())
		{

			LinkedList<Map.Entry> entries = new LinkedList<Map.Entry>(iconCache.entrySet());
			Collections.sort(entries, new Comparator()
			{

				@Override
				public int compare(Object o1, Object o2)
				{
					Map.Entry e1 = (Map.Entry) o1;
					Map.Entry e2 = (Map.Entry) o2;
					return ((Icon) e1.getValue()).compareTo((Icon) e2.getValue());
				}
			});

			Icon removed = iconCache.remove(entries.poll().getKey());
			removed.bitmap.recycle();
			counter.reset();
		}

		iconCache.put(id, icon);
		counter.countUp();
	}

	public static Bitmap getEmptyIcon()
	{
		Options opt = new Options();
		opt.inPurgeable = true; // GC�\�ɂ���
		Bitmap bm = BitmapFactory.decodeResource(Client.getResource(), R.drawable.icon_reflesh, opt);
		return bm;
	}

	public static class Icon implements Comparable<Icon>
	{
		private Bitmap bitmap;
		public String fileName;
		public int count = 0;

		public Icon(Bitmap bitmap, String fileName)
		{
			this.bitmap = bitmap;
			this.fileName = fileName;
		}

		public Bitmap use()
		{
			count++;
			return bitmap;
		}

		@Override
		public int compareTo(Icon another)
		{
			return this.count > another.count ? -1 : (this.count == another.count ? 0 : 1);
		}

	}
}