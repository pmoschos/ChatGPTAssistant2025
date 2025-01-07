package gr.aueb.cf.chatgptassistant2025.models;

public class Post {
    private int postId;
    private String question;
    private String response;

    public Post() {}

    public Post(int postId, String question, String response) {
        this.postId = postId;
        this.question = question;
        this.response = response;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
