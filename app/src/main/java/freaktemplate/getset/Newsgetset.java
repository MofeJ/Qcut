package freaktemplate.getset;

public class Newsgetset {
	private String news_content;
    private String news_title;
    private String news_url;
    private String news_image;
    private String created_at;
    private String updated_at;

	public String getNews_content() {
		return news_content;
	}

	public void setNews_content(String news_content) {
		this.news_content = news_content;
	}

	public String getNews_title() {
		return news_title;
	}

	public void setNews_title(String news_title) {
		this.news_title = news_title;
	}

	public String getNews_url() {
		return news_url;
	}

	public void setNews_url(String news_url) {
		this.news_url = news_url;
	}

	public String getNews_image() {
		return news_image;
	}

	public void setNews_image(String news_image) {
		this.news_image = news_image;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
}
