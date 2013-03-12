package net.miz_hi.smileessence.util;

import java.util.LinkedList;
import java.util.List;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.auth.Account;
import net.miz_hi.smileessence.view.MainActivity;
import twitter4j.Paging;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager
{
	public static final String MESSAGE_TWEET_SUCCESS = "投稿しました";
	public static final String MESSAGE_TWEET_DEPLICATE = "投稿失敗しました";
	public static final String MESSAGE_RETWEET_SUCCESS = "リツイートしました";
	public static final String MESSAGE_RETWEET_DEPLICATE = "リツイート失敗しました";
	public static final String MESSAGE_FAVORITE_SUCCESS = "お気に入りに追加しました";
	public static final String MESSAGE_FAVORITE_DEPLICATE = "お気に入りの追加に失敗しました";
	public static final String MESSAGE_TWEET_LIMIT = "規制されています";
	public static final String MESSAGE_SOMETHING_ERROR = "何かがおかしいです";
	private static final String ERROR_STATUS_DUPLICATE = "Status is a duplicate.";
	private static final String ERROR_STATUS_LIMIT = "User is over daily status update limit.";
	private static Twitter _twitter;
	private static boolean _isStatusUpdateLimit = false;
	private static Account _lastAccount;

	private static ConfigurationBuilder generateConfig(Account account)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(account.getConsumerKey());
		cb.setOAuthConsumerSecret(account.getConsumerSecret());
		cb.setOAuthAccessToken(account.getAccessToken());
		cb.setOAuthAccessTokenSecret(account.getAccessTokenSecret());
		return cb;
	}

	public static Twitter getTwitter(Account account)
	{
		if (_lastAccount == null || !account.equals(_lastAccount) || _twitter == null)
		{
			_twitter = new TwitterFactory(generateConfig(account).build()).getInstance();
			_lastAccount = account;
		}
		return _twitter;
	}

	public static TwitterStream getTwitterStream(Account account)
	{
		ConfigurationBuilder cb = generateConfig(account);
		cb.setUserStreamRepliesAllEnabled(false);
		return new TwitterStreamFactory(cb.build()).getInstance();
	}
	
	public static boolean canConnect()
	{
		try
		{
			String screenName = getTwitter(Client.getMainAccount()).getScreenName();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static boolean isStatusUpdateLimit()
	{
		return _isStatusUpdateLimit;
	}

	public static boolean tweet(Account account, String str)
	{
		try
		{
			getTwitter(account).updateStatus(str);
			_isStatusUpdateLimit = false;
			return true;
		}
		catch (TwitterException e)
		{
			int code = e.getStatusCode();
			String message = e.getErrorMessage();
			if (code == 403)
			{
				if (message.equals(ERROR_STATUS_LIMIT))
				{
					_isStatusUpdateLimit = true;
				}
			}
		}
		return false;
	}

	public static boolean tweet(Account account, String str, long l)
	{
		try
		{
			StatusUpdate update = new StatusUpdate(str);
			update.setInReplyToStatusId(l);
			getTwitter(account).updateStatus(update);
			_isStatusUpdateLimit = false;
			return true;
		}
		catch (TwitterException e)
		{
			int code = e.getStatusCode();
			String message = e.getErrorMessage();
			if (code == 403)
			{
				if (message.equals(ERROR_STATUS_LIMIT))
				{
					_isStatusUpdateLimit = true;
				}
			}
		}
		return false;
	}

	public static boolean tweet(Account account, StatusUpdate update)
	{
		try
		{
			getTwitter(account).updateStatus(update);
			_isStatusUpdateLimit = false;
			return true;
		}
		catch (TwitterException e)
		{
			int code = e.getStatusCode();
			String message = e.getErrorMessage();
			if (code == 403)
			{
				if (message.equals(ERROR_STATUS_LIMIT))
				{
					_isStatusUpdateLimit = true;
				}
			}
		}
		return false;
	}

	public static boolean retweet(Account account, long statusId)
	{
		try
		{
			getTwitter(account).retweetStatus(statusId);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean favorite(Account account, long statusId)
	{
		try
		{
			getTwitter(account).createFavorite(statusId);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isOauthed(Account account)
	{
		return StringUtils.isNullOrEmpty(account.getAccessToken());
	}

	public static boolean isFollowing(Account account, long id)
	{
		if (id < 0)
		{
			return false;
		}
		try
		{
			Relationship relation = getTwitter(account).showFriendship(getTwitter(account).getId(), id);
			return relation.isSourceFollowingTarget();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isFollowed(Account account, long id)
	{
		if (id < 0)
		{
			return false;
		}
		try
		{
			Relationship relation = getTwitter(account).showFriendship(getTwitter(account).getId(), id);
			return relation.isSourceFollowedByTarget();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static Status getStatus(Account account, long id)
	{
		Status status = null;
		try
		{
			status = getTwitter(account).showStatus(id);
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return status;
	}

	public static List<Status> getOldTimeline(Account account, Paging page)
	{
		LinkedList<Status> statuses = new LinkedList<Status>();
		try
		{
			ResponseList<Status> resp;
			if(page == null)
			{
				resp = getTwitter(account).getHomeTimeline();
			}
			else
			{
				resp = getTwitter(account).getHomeTimeline(page);
			}

			for (Status st : resp)
			{
				statuses.offer(st);
			}
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return statuses;
	}

	public static List<Status> getOldMentions(Account account, Paging page)
	{
		LinkedList<Status> statuses = new LinkedList<Status>();
		try
		{
			ResponseList<Status> mentions = getTwitter(account).getMentionsTimeline(page);
			for (Status st : mentions)
			{
				statuses.offer(st);
			}
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return statuses;
	}

	public static List<Status> getUserTimeline(Account account, long userId, Paging page)
	{
		LinkedList<Status> statuses = new LinkedList<Status>();
		try
		{
			ResponseList<Status> resp = getTwitter(account).getUserTimeline(userId, page);

			for (Status st : resp)
			{
				statuses.offer(st);
			}
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return statuses;
	}

	public static User getUser(Account account, Object obj)
	{
		try
		{
			User user = null;
			if (obj instanceof String)
			{
				user = getTwitter(account).showUser((String) obj);
			}
			else if (obj instanceof Long)
			{
				user = getTwitter(account).showUser(((Long) obj).longValue());
			}
			return user;
		}
		catch (TwitterException e)
		{
			return null;
		}
	}

	public static boolean follow(Account account, String name)
	{
		try
		{
			getTwitter(account).createFriendship(name);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean remove(Account account, String name)
	{
		try
		{
			getTwitter(account).destroyFriendship(name);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean block(Account account, String name)
	{
		try
		{
			getTwitter(account).createBlock(name);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean spam(Account account, String name)
	{
		try
		{
			getTwitter(account).reportSpam(name);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}