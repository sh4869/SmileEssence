package net.miz_hi.smileessence.command;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.data.StatusModel;
import net.miz_hi.smileessence.view.TweetViewManager;

public class StatusCommandUnOffRetweet extends StatusCommand implements IHideable
{

	public StatusCommandUnOffRetweet(StatusModel model)
	{
		super(model);
	}

	@Override
	public String getName()
	{
		return "�����RT";
	}

	@Override
	public void workOnUiThread()
	{
		String text = " RT @" + status.screenName + ": " + status.text;
		TweetViewManager manager = TweetViewManager.getInstance();
		manager.setText(text);
		manager.setCursor(0);
		manager.open();
	}

	@Override
	public boolean getDefaultVisibility()
	{
		return Client.getPermission().canUnOffRetweet() && !status.user.isProtected;
	}
}