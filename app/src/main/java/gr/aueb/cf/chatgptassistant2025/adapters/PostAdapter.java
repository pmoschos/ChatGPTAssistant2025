package gr.aueb.cf.chatgptassistant2025.adapters;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gr.aueb.cf.chatgptassistant2025.R;
import gr.aueb.cf.chatgptassistant2025.helpers.SQLiteDBHelper;
import gr.aueb.cf.chatgptassistant2025.models.Post;
import gr.aueb.cf.chatgptassistant2025.utils.CustomAlertDialog;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> postArrayList;
    private Context context;
    private SQLiteDBHelper sqLiteDBHelper;

    public PostAdapter(ArrayList<Post> postArrayList, Context context) {
        this.postArrayList = postArrayList;
        this.context = context;
        this.sqLiteDBHelper = new SQLiteDBHelper(context);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postArrayList.get(position);
        holder.questionTV.setText(post.getQuestion());
        holder.responseTV.setText(post.getResponse());

        // TODO: handle delete
        holder.deleteTV.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);

                customAlertDialog.yesPressed(v -> {
                    Post postToDelete = postArrayList.get(pos);
                    int rowsAffected = sqLiteDBHelper.deletePost(postToDelete.getPostId());

                    if (rowsAffected > 0) {
                        postArrayList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, postArrayList.size());
                        Toast.makeText(context, "Item deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error deleting post.", Toast.LENGTH_SHORT).show();
                    }
                    customAlertDialog.dismiss();
                });

                customAlertDialog.noPressed(v -> customAlertDialog.dismiss());
                customAlertDialog.show();
            } else {
                Toast.makeText(context, "Error: Position not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView questionTV;
        private TextView responseTV;
        private TextView deleteTV;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTV = itemView.findViewById(R.id.question_tv);
            responseTV = itemView.findViewById(R.id.response_tv);
            deleteTV = itemView.findViewById(R.id.delete_tv);
        }
    }
}
