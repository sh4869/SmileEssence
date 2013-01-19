package net.miz_hi.warotter.listener;

import android.app.Activity;
import net.miz_hi.warotter.core.EventHandlerActivity;
import net.miz_hi.warotter.model.EventListAdapter;
import net.miz_hi.warotter.model.StatusListAdapter;
import net.miz_hi.warotter.model.StatusModel;
import net.miz_hi.warotter.model.StatusStore;
import net.miz_hi.warotter.viewmodel.MainActivityViewModel;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class WarotterUserStreamListener implements UserStreamListener
{
	private StatusListAdapter homeListAdapter;
	private StatusListAdapter mentionsListAdapter;
	private EventListAdapter eventListAdapter;

	public WarotterUserStreamListener()
	{
	}
	
	public void setHomeListAdapter(StatusListAdapter adapter)
	{
		this.homeListAdapter = adapter;
	}
	
	public void setMentionsListAdapter(StatusListAdapter adapter)
	{
		this.mentionsListAdapter = adapter;
	}
	
	public void setEventListAdapter(EventListAdapter adapter)
	{
		this.eventListAdapter = adapter;
	}

	@Override
	public void onDeletionNotice(final StatusDeletionNotice arg0)
	{
		Status status = StatusStore.get(arg0.getStatusId());
		if(status == null)
		{
			return;
		}
		else
		{
			final StatusModel model = StatusModel.createInstance(status);
			homeListAdapter.getActivity().runOnUiThread(new Runnable()
			{
				public void run()
				{
					homeListAdapter.remove(model);
				}
			});
			mentionsListAdapter.getActivity().runOnUiThread(new Runnable()
			{
				public void run()
				{
					mentionsListAdapter.remove(model);
				}
			});
			StatusStore.remove(status.getId());
		}
	}

	@Override
	public void onScrubGeo(long arg0, long arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStallWarning(StallWarning arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatus(final Status arg0)
	{
		StatusStore.put(arg0);
		if (arg0.isRetweet())
		{
			StatusStore.put(arg0.getRetweetedStatus());
		}
		final StatusModel model = StatusModel.createInstance(arg0);
		if(StatusStore.isReply(arg0.isRetweet() ? arg0.getRetweetedStatus().getId() : arg0.getId()))
		{
			mentionsListAdapter.getActivity().runOnUiThread(new Runnable()
			{
				public void run()
				{
					mentionsListAdapter.addFirst(model);
				}
			});
		}
		homeListAdapter.getActivity().runOnUiThread(new Runnable()
		{
			public void run()
			{
				homeListAdapter.addFirst(model);
			}
		});
	}

	@Override
	public void onTrackLimitationNotice(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onException(Exception arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlock(User arg0, User arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeletionNotice(long arg0, long arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectMessage(DirectMessage arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onFollow(User arg0, User arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onFriendList(long[] arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnblock(User arg0, User arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListCreation(User arg0, UserList arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserProfileUpdate(User arg0)
	{
		// TODO Auto-generated method stub

	}

}