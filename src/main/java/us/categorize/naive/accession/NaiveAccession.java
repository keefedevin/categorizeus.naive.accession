package us.categorize.naive.accession;

import java.util.UUID;

import us.categorize.Config;
import us.categorize.Configuration;
import us.categorize.api.UserStore;
import us.categorize.model.User;
import us.categorize.naive.NaiveMessageStore;
import us.categorize.naive.NaiveUserStore;
import us.categorize.naive.accession.domains.Reddit;
import us.categorize.naive.api.NaiveAuthorizer;

public class NaiveAccession {
	private static final String userName = "reddit-user";
	private static final String pass = "35789fb6e";
	
	public static void main(String[] args) throws Exception{
		Config config = Config.readRelativeConfig();
		Configuration.instance().setUserStore(new NaiveUserStore(config.getDatabaseConnection()));
		Configuration.instance().setMessageStore(new NaiveMessageStore(config.getDatabaseConnection(), Configuration.instance().getUserStore(), config.getFileBase()));
		Configuration.instance().setAuthorizer(new NaiveAuthorizer(Configuration.instance().getUserStore()));
		UserStore userStore = Configuration.instance().getUserStore();
		
		User user = new User();
		user.setUsername(userName);
		user.setPasshash(NaiveUserStore.sha256hash(pass));
		String sessionKey = UUID.randomUUID().toString();
		if(!userStore.establishUserSession(user, sessionKey)) {
			user.setPasshash(NaiveUserStore.sha256hash(NaiveUserStore.sha256hash(pass)));
			Configuration.instance().getUserStore().registerUser(user);
			user.setPasshash(NaiveUserStore.sha256hash(pass));
			if(!userStore.establishUserSession(user, sessionKey)) {
				throw new RuntimeException("Unable to create user session");
			}
		}
		
		
		
		Reddit reddit = new Reddit(user, userStore, Configuration.instance().getMessageStore());
		String after = null;
		
		do {
			after = reddit.readPage("https://www.reddit.com/r/aww/.json?raw_json=1", after);
			Thread.sleep(10000);
		}while(after!=null);
	}

}
