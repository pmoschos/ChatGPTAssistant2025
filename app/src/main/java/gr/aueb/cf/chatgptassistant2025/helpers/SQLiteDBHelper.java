package gr.aueb.cf.chatgptassistant2025.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import gr.aueb.cf.chatgptassistant2025.models.Post;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "chat_gpt_app.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "posts";
    public static final String POST_ID = "postId";
    public static final String POST_QUESTION = "question";
    public static final String POST_RESPONSE = "response";

    private static final String SQL_CREATE_ENTITIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    POST_ID + " INTEGER PRIMARY KEY, " +
                    POST_QUESTION + " TEXT, " +
                    POST_RESPONSE + " TEXT)";

    public SQLiteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTITIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Post> getAllPosts() {
        ArrayList<Post> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[] {POST_ID, POST_QUESTION, POST_RESPONSE},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(POST_ID));
                String question = cursor.getString(cursor.getColumnIndexOrThrow(POST_QUESTION));
                String response = cursor.getString(cursor.getColumnIndexOrThrow(POST_RESPONSE));
                // create post obj
                list.add(new Post(id, question, response));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public  ArrayList<Post> searchPosts(String query) {
        ArrayList<Post> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + POST_QUESTION + " LIKE ?"
                + " OR " + POST_RESPONSE + " LIKE ?";

        String[] args = new String[] {"%" + query + "%", "%" + query + "%"};
        Cursor cursor = db.rawQuery(sql, args);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(POST_ID));
                String question = cursor.getString(cursor.getColumnIndexOrThrow(POST_QUESTION));
                String response = cursor.getString(cursor.getColumnIndexOrThrow(POST_RESPONSE));
                results.add(new Post(id, question, response));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

    public int deletePost(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = POST_ID + " = ?";
        String[] whereArgs = {String.valueOf(postId)};

        return  db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    // Delete all of the posts
    public void deleteAllPosts() {
        this.getWritableDatabase().delete(TABLE_NAME, null, null);
    }


}
