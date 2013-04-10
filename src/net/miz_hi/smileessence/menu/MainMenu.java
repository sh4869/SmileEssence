package net.miz_hi.smileessence.menu;

import net.miz_hi.smileessence.command.CommandEditExtraWord;
import net.miz_hi.smileessence.command.CommandEditMenu;
import net.miz_hi.smileessence.command.CommandEditTemplate;
import net.miz_hi.smileessence.command.CommandOpenFavstar;
import net.miz_hi.smileessence.command.CommandOpenSetting;
import net.miz_hi.smileessence.command.CommandReConnect;
import net.miz_hi.smileessence.command.CommandReport;
import net.miz_hi.smileessence.command.CommandTweet;
import net.miz_hi.smileessence.dialog.DialogAdapter;
import android.app.Activity;
import android.app.Dialog;

public class MainMenu extends DialogAdapter
{
	
	private static MainMenu instance;

	private MainMenu(Activity activity)
	{
		super(activity);
	}

	@Override
	public Dialog createMenuDialog(boolean init)
	{
		if (init)
		{
			list.clear();
			list.add(new CommandTweet());
			list.add(new CommandOpenSetting(activity));
			list.add(new CommandReConnect());
			list.add(new CommandEditTemplate(activity));
			list.add(new CommandEditExtraWord(activity));
			list.add(new CommandEditMenu(activity));
			list.add(new CommandOpenFavstar(activity));
			list.add(new CommandReport());
			
			setTitle("���j���[");
		}
		
		return super.createMenuDialog();
	}
	
	public static void init(Activity activity)
	{
		instance = new MainMenu(activity);
	}
	
	public static MainMenu getInstance()
	{
		return instance;
	}
}
