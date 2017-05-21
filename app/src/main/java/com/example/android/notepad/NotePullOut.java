package com.example.android.notepad;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017-05-13.
 */

public class NotePullOut extends Activity {
    public static final String EXPORT_NOTE_ACTION = "com.android.notepad.action.PULL_OUT_NOTE";

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,          // Projection position 0, the note's id
            NotePad.Notes.COLUMN_NAME_TITLE,   // Projection position 1, the note's title
            NotePad.Notes.COLUMN_NAME_NOTE     // Projection position 2, the note's modified date
    };
    private static final int COLUMN_INDEX_TITLE = 1;
    private static final int COLUMN_INDEX_NOTE = 2;

    private static final int SAVE_SUCCESS = 0;
    private static final int SAVE_SDCARD_ERROR = 1;
    private static final int SAVE_FAIL = 2;
    private Cursor mCursor;
    private Uri mUri;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pull_out);
        tv = (TextView) findViewById(R.id.saveFileDir);

        mUri = getIntent().getData();
        mCursor = managedQuery(
                mUri,        // The URI for the note that is to be retrieved.
                PROJECTION,  // The columns to retrieve
                null,        // No selection criteria are used, so no where columns are needed.
                null,        // No where columns are used, so no where values are needed.
                null         // No sort order is needed.
        );
        if (mCursor != null) {

            mCursor.moveToFirst();
            String title = mCursor.getString(COLUMN_INDEX_TITLE).replace(" ", "");//去除标题中的空格
            String noteText = mCursor.getString(COLUMN_INDEX_NOTE);//获取当前note的标题
            int result = exportNote(noteText, title);
            String saveDir = Environment.getExternalStorageDirectory().toString();
            if (result == SAVE_SUCCESS) {
                tv.setText("New file \"" + title + ".txt\" has been saved in " + saveDir + "");
            } else if (result == SAVE_SDCARD_ERROR) {
                tv.setText("SD card error！file \"" + title + ".txt\" saves fail!");
            } else {
                tv.setText("File \"" + title + ".txt\" saves fail!");
            }
        }
    }

    private final int exportNote(String text, String title) {
        int result = SAVE_SUCCESS;
        String filename = title;
        String filecontent = text;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                File file = new File(sdCardDir, filename + ".txt");
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(filecontent.getBytes());
                outStream.close();
            } else {
                result = SAVE_SDCARD_ERROR;
            }

        } catch (Exception e) {
            result = SAVE_FAIL;
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public void onClickOk(View v) {
        finish();
    }
}


