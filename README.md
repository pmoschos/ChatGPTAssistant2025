# ChatGPT Assistant 2025 (Android Application)

![Total Views](https://views.whatilearened.today/views/github/pmoschos/ChatGPTAssistant2025.svg) ![GitHub last commit](https://img.shields.io/github/last-commit/pmoschos/ChatGPTAssistant2025) ![License](https://img.shields.io/badge/license-MIT-green.svg)

## 🤖 Overview

ChatGPT Assistant 2025 is an Android application designed to provide an intuitive chat experience powered by OpenAI's GPT model. Users can input their queries, and the app fetches intelligent responses using OpenAI's ChatGPT API. This app is also equipped with a local SQLite database to store and manage past conversations for easy reference.

---

## ✨ Key Features

- **AI-Powered Conversations**: Communicate with OpenAI's ChatGPT API for engaging and informative interactions.
- **Search Functionality**: Quickly find past conversations stored locally using an SQLite database.
- **Custom Toolbar**: Includes a personalized, user-friendly toolbar with a centered app title.
- **Progress Indicators**: Displays a progress bar while waiting for API responses.
- **Data Persistence**: Save all interactions locally for offline access.
- **Robust Error Handling**: Manage API failures and user input validations efficiently.

---

## 📱 Screenshots

![image](https://github.com/user-attachments/assets/6fb84b95-1d1b-4fcf-9c51-0aed7483d724)

---

## 📚 Libraries Used

### Core Libraries:
- **AndroidX Libraries**:
  ```gradle
  implementation 'androidx.appcompat:appcompat:1.6.1'
  implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
  ```
- **Volley**: For API communication.
  ```gradle
  implementation 'com.android.volley:volley:1.2.1'
  ```
- **SQLite**: For local database storage and management.

---

## 🔧 Technical Requirements

- **Android Studio**: Version 4.1 or later.
- **Java JDK**: Version 11.
- **Min SDK**: API level 28 (Android 9.0).
- **Target SDK**: API level 34.
- **OpenAI API Key**: Required to access ChatGPT functionality.

---

## 🚀 Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/username/ChatGPT-Assistant-2025.git
   ```

2. **Open in Android Studio**:
   - Open the project in Android Studio.
   - Sync Gradle files to install dependencies.

3. **Add API Key**:
   - Register on [OpenAI](https://openai.com/) to generate an API key.
   - Add your API key in `ChatGPTSecret.java`:
     ```java
     public static final String MY_KEY = "YOUR_API_KEY";
     ```

4. **Build and Run**:
   - Connect your Android device or emulator.
   - Click `Run` to build and launch the app.

---

## 🔍 User Interface Features

### 1. **Custom Toolbar**:
   - Includes a center-aligned title for better visual clarity.

### 2. **Input and Response Management**:
   - Users type their queries in an editable text field.
   - API responses are displayed in a dynamic RecyclerView.

### 3. **Database Integration**:
   - SQLite is used for persistent storage of queries and responses.
   - View past interactions via a searchable interface.

### 4. **Progress Feedback**:
   - A circular progress bar indicates when API data is being fetched.

---

## 💻 Example Code Snippets

### API Call Example:
```java
private void callApi(String question, final ApiResponseCallback callback) {
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://api.openai.com/v1/chat/completions";

    JSONObject requestBodyJson = new JSONObject();
    try {
        requestBodyJson.put("model", "gpt-3.5-turbo");
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant"));
        messagesArray.put(new JSONObject().put("role", "user").put("content", question));
        requestBodyJson.put("messages", messagesArray);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBodyJson,
        response -> {
            try {
                String textResponse = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                callback.onResponseReceived(textResponse);
            } catch (JSONException e) {
                callback.onResponseReceived(null);
            }
        },
        error -> callback.onResponseReceived(null)
    ) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + ChatGPTSecret.MY_KEY);
            return headers;
        }
    };

    queue.add(jsonObjectRequest);
}
```

### SQLite Integration Example:
```java
private void addPostToDatabase(String question, String response) {
    SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(SQLiteDBHelper.POST_QUESTION, question);
    values.put(SQLiteDBHelper.POST_RESPONSE, response);

    long newRowId = db.insert(SQLiteDBHelper.TABLE_NAME, null, values);
    if (newRowId != -1) {
        postArrayList.add(new Post((int)newRowId, question, response));
        postAdapter.notifyItemInserted(postArrayList.size() - 1);
        recyclerView.scrollToPosition(postArrayList.size() - 1);
    }
}
```

---

## 📢 Stay Updated

Be sure to ⭐ this repository to stay updated with new examples and enhancements!

## 📄 License
🔐 This project is protected under the [MIT License](https://mit-license.org/).


## Contact 📧
Panagiotis Moschos - pan.moschos86@gmail.com


---
<h1 align=center>Happy Coding 👨‍💻 </h1>

<p align="center">
  Made with ❤️ by 
  <a href="https://www.linkedin.com/in/panagiotis-moschos" target="_blank">
  Panagiotis Moschos</a> (https://github.com/pmoschos)
</p>
