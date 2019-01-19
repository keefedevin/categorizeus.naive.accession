package us.categorize.naive.accession;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import us.categorize.CategorizeUs;
import us.categorize.api.UserStore;
import us.categorize.model.User;
import us.categorize.naive.NaiveUserStore;
import us.categorize.naive.accession.domains.Reddit;
import us.categorize.naive.app.Config;
import us.categorize.naive.app.NaiveApp;

public class NaiveAccession {
	private static final String userName = "reddit-user";
	private static final String pass = "35789fb6e";
	private  static final String tags[] = new String[] {
			"photography"
	};
	public static void main(String[] args) throws Exception{
		Properties properties = new Properties();
		InputStream input = NaiveApp.class.getResourceAsStream("/categorizeus.properties");
		properties.load(input);
		//note overrideProperties and toLocal in Config, just load another properties files and override with these as desired
		Config categorizeUsConfig = new Config(properties);
		categorizeUsConfig.configureCategorizeUs();
		
		Configuration accessConfig = new Configuration();
		accessConfig.setAddDuplicateAttachments(true);
		UserStore userStore = CategorizeUs.instance().getUserStore();
		
		User user = new User();
		user.setUsername(userName);
		user.setPasshash(NaiveUserStore.sha256hash(pass));
		user.setName("Reddit Accession");
		user.setGivenName("Reddit");
		user.setFamilyName("Accession");
		user.setAuthorized(true);
		user.setEmail("kroeders@gmail.com");
		String sessionKey = UUID.randomUUID().toString();
		if(!userStore.validateUser(user)) {
			user.setPasshash(NaiveUserStore.sha256hash(NaiveUserStore.sha256hash(pass)));
			CategorizeUs.instance().getUserStore().registerUser(user);
			user.setPasshash(NaiveUserStore.sha256hash(pass));
			if(!userStore.establishUserSession(user, sessionKey)) {
				throw new RuntimeException("Unable to create user session");
			}
		}
		userStore.establishUserSession(user, sessionKey);
		

		for(String tag : tags) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Reddit reddit = new Reddit(accessConfig, user, userStore, CategorizeUs.instance().getMessageStore());
					String after = null;
					do {
						//note keyboard click with focus = tag, need to keep tags by color and keep them on when selected
						after = reddit.readPage("https://www.reddit.com/r/"+tag+"/.json?raw_json=1", after);
						if(after==null) {
							try {
								Thread.sleep(20000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try {
							long delay = 10000l + (long) (Math.random() * 60000);
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}while(true);
					
				}
			}).start();			
		}
	}

}
