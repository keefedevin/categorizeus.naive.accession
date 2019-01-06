package us.categorize.naive.accession;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Config {
	public static final int DEFAULT_PAGE_SIZE = 10, DEFAULT_PAGE_ON = 0;
	private String clearSql, createSql, dbHost, dbPort, dbName, dbUser, dbPass, staticDir, indexSql, seedSql, fileBase;
	private double maxThumbWidth, maxThumbHeight;
	private long maxUploadSize = -1;
	private int port;
	private String attachmentURLPrefix, connectString;
	private String driverName = "org.postgresql.Driver";

	public static Config readRelativeConfig() throws Exception {
		Properties properties = new Properties();
		InputStream input = Config.class.getResourceAsStream("/categorizeus.properties");
		properties.load(input);
		StringWriter writer = new StringWriter();
		properties.list(new PrintWriter(writer));
		System.out.println("Properties File Read As ");
		System.out.println(writer.getBuffer().toString());
		Config config = new Config(properties);
		return config;
	}

	public Config(Properties properties) {
		dbName = properties.getProperty("DB_NAME");
		dbHost = properties.getProperty("DB_HOST");
		dbPort = properties.getProperty("DB_PORT");
		dbUser = properties.getProperty("DB_USER");
		dbPass = properties.getProperty("DB_PASS");
		fileBase = properties.getProperty("FILE_BASE");
		attachmentURLPrefix = properties.getProperty("ATTACHMENT_URL_PREFIX");
		connectString = "jdbc:postgresql:" + "/" + "/" + dbHost + ":" + dbPort + "/" + dbName;
		maxUploadSize = Long.parseLong(properties.getProperty("MAX_UPLOAD_SIZE"));
		maxThumbWidth = Double.parseDouble(properties.getProperty("MAX_THUMB_WIDTH"));
		maxThumbHeight = Double.parseDouble(properties.getProperty("MAX_THUMB_HEIGHT"));
		port = Integer.parseInt(properties.getProperty("PORT"));
		staticDir = properties.getProperty("STATIC_DIR");
	}

	public Connection getDatabaseConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
		System.out.println("Postgres Driver Loaded");
		System.out.println("Connecting " + getConnectString() + "," + getDbUser() + "," + getDbPass());
		Connection conn = DriverManager.getConnection(getConnectString(), getDbUser(), getDbPass());
		return conn;
	}

	public String getClearSql() {
		return clearSql;
	}

	public void setClearSql(String clearSql) {
		this.clearSql = clearSql;
	}

	public String getCreateSql() {
		return createSql;
	}

	public void setCreateSql(String createSql) {
		this.createSql = createSql;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public String getIndexSql() {
		return indexSql;
	}

	public void setIndexSql(String indexSql) {
		this.indexSql = indexSql;
	}

	public String getSeedSql() {
		return seedSql;
	}

	public void setSeedSql(String seedSql) {
		this.seedSql = seedSql;
	}

	public String getFileBase() {
		return fileBase;
	}

	public void setFileBase(String fileBase) {
		this.fileBase = fileBase;
	}

	public double getMaxThumbWidth() {
		return maxThumbWidth;
	}

	public void setMaxThumbWidth(double maxThumbWidth) {
		this.maxThumbWidth = maxThumbWidth;
	}

	public double getMaxThumbHeight() {
		return maxThumbHeight;
	}

	public void setMaxThumbHeight(double maxThumbHeight) {
		this.maxThumbHeight = maxThumbHeight;
	}

	public long getMaxUploadSize() {
		return maxUploadSize;
	}

	public void setMaxUploadSize(long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAttachmentURLPrefix() {
		return attachmentURLPrefix;
	}

	public void setAttachmentURLPrefix(String attachmentURLPrefix) {
		this.attachmentURLPrefix = attachmentURLPrefix;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

}
