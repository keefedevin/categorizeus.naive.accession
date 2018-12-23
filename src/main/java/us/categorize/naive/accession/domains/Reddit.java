package us.categorize.naive.accession.domains;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.categorize.api.MessageStore;
import us.categorize.api.UserStore;
import us.categorize.model.Attachment;
import us.categorize.model.Message;
import us.categorize.model.User;

//quick prototype, extracting interfaces later
public class Reddit {
	private ObjectMapper mapper = new ObjectMapper();
	
	private User user;
	private UserStore userStore;
	private MessageStore messageStore;
	private long delay = 1000;
	private CloseableHttpClient client;
	private static String userAgentString = "us.categorize.naive.accession";
	
	public Reddit(User user, UserStore userStore, MessageStore messageStore) {
		this.user = user;
		this.userStore = userStore;
		this.messageStore = messageStore;
		client = HttpClients.custom().setUserAgent(userAgentString).build();
		

	}
	
	public String readPage(String base, String after) {
		String url = base + (after==null?"":"&after="+after);
		System.out.println(url);
		HttpGet httpget = new HttpGet(url);
		String lastSeen = null;
		try {
			CloseableHttpResponse response = client.execute(httpget);
			try {
			    HttpEntity entity = response.getEntity();
			    if (entity != null) {
			    	ObjectNode node = (ObjectNode) mapper.readTree(entity.getContent());
			    	JsonNode kindNode = node.get("kind");
			    	if(kindNode==null || !"Listing".equals(kindNode.asText())) {
			    		throw new RuntimeException(mapper.writeValueAsString(node));
			    	}
			    	ObjectNode data = (ObjectNode) node.get("data");
			    	ArrayNode children = (ArrayNode) data.get("children");
			    	for(JsonNode entry : children) {
			    		ObjectNode entryMeta = (ObjectNode) entry;
			    		ObjectNode entryO = (ObjectNode) entryMeta.get("data");
			    		String name = entryO.get("name").asText();
			    		String link = entryO.get("permalink").asText();
			    		String img = entryO.get("url").asText();
			    		String title = entryO.get("title").asText();
			    		String subreddit = entryO.get("subreddit").asText();
			    		System.out.println(name + " " + img + " " + title);
			    		if(!img.endsWith("jpg")) continue;
			    		Message message = new Message();
			    		message.setTitle(title);
			    		message.setBody(name + " " + link);
			    		message.setPostedBy(user.getId());
			    		message = messageStore.createMessage(message);
			    		title = title + " " + subreddit;
			    		String tags[] = title.split(" ");
			    		Set<String> added = new HashSet<String>();
			    		for(String tag : tags) {
			    			tag = tag.toLowerCase();
			    			if(!added.contains(tag)) {
			    				messageStore.addMessageTag(message.getId(), tag, user);
			    				added.add(tag);
			    			}
			    		}
			    		lastSeen = name;
			    		addAttachment(message, img);
			    		System.out.println("Added " + name);
			    		Thread.sleep(delay);
			    	}
			    }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			    response.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastSeen;
	}

	private void addAttachment(Message message, String img) {
		String fname = img.substring(img.lastIndexOf("/"));
		Attachment attachment = new Attachment();
		attachment.setFilename(img);
		attachment.setMessageId(message.getId());
		HttpGet httpget = new HttpGet(img);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpget);
		    HttpEntity entity = response.getEntity();
		    messageStore.createAttachment(attachment, entity.getContent());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(response!=null)
					response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
}