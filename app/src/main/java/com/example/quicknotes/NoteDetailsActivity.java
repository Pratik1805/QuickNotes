package com.example.quicknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEdtText,contentEdtText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    Button deleteNoteBtn;
    String title,content,docId;
    boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEdtText = findViewById(R.id.notes_title_text);
        contentEdtText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.saveNoteBtn);
        pageTitleTextView = findViewById(R.id.pageTitle);
        deleteNoteBtn = findViewById(R.id.delete_note__btn);


        //getData
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId != null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEdtText.setText(title);
        contentEdtText.setText(content);
        if (isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v -> saveNote()));

        deleteNoteBtn.setOnClickListener((v)-> deleteNoteFromFireBase());
    }
    void saveNote(){
        String noteTitle = titleEdtText.getText().toString();
        String noteContent = contentEdtText.getText().toString();

        if (noteTitle == null || noteTitle.isEmpty()){
            titleEdtText.setError("Title is Required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        saveNoteToFireBase(note);
    }
    void saveNoteToFireBase(Note note){
        DocumentReference documentReference;

        if (isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note added
                    Utility.showToast(NoteDetailsActivity.this,"Added Successfully");
                    finish();
                }
                else{
                    //Note note added
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });
    }
    void deleteNoteFromFireBase(){
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note added
                    Utility.showToast(NoteDetailsActivity.this,"Deleted Successfully");
                    finish();
                }
                else{
                    //Note note added
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }

}