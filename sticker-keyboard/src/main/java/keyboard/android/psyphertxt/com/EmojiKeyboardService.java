package keyboard.android.psyphertxt.com;

import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import keyboard.android.psyphertxt.com.adapter.BaseEmojiAdapter;
import keyboard.android.psyphertxt.com.view.EmojiKeyboardView;

public class EmojiKeyboardService extends InputMethodService {

    private static final String TAG = EmojiKeyboardService.class.getSimpleName();
    private EmojiKeyboardView emojiKeyboardView;
    public static final String MIME_TYPE_PNG = "image/png";

    private InputConnection inputConnection;
    public static View mInputView;
    private InputMethodManager previousInputMethodManager;
    private IBinder iBinder;
    private static final String AUTHORITY = "keyboard.android.psyphertxt.com.gkeyboard.inputcontent";

    private static Context staticApplicationContext;

    public static Context getStaticApplicationContext() {
        return staticApplicationContext;
    }

    public EmojiKeyboardService() {
        super();

        if (Build.VERSION.SDK_INT >= 17) {
            enableHardwareAcceleration();
        }
    }

    @Override
    public void onBindInput() {
        super.onBindInput();
        try {
            if(staticApplicationContext != null)
            deleteStickerEmojisOnStorage(staticApplicationContext);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateInputView() {

        staticApplicationContext = getApplicationContext();

        previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        iBinder = this.getWindow().getWindow().getAttributes().token;

        BaseEmojiAdapter.info = getCurrentInputEditorInfo();
        emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater()
                .inflate(R.layout.emoji_keyboard_layout, null);
        mInputView = emojiKeyboardView.getView();
        return mInputView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        if(!isCommitContentSupported(attribute, MIME_TYPE_PNG)){
            return;
        }
        super.onStartInputView(attribute, restarting);
        inputConnection = getCurrentInputConnection();
    }

    public void sendText(String text) {
        inputConnection.commitText(text, 1);
    }

    public void sendDownKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_UP,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }
    public void sendDownAndUpKeyEvent(int keyEventCode, int flags){
        sendDownKeyEvent(keyEventCode, flags);
        sendUpKeyEvent(keyEventCode, flags);
    }


    public void switchToPreviousInputMethod() {

        try {
            previousInputMethodManager.switchToLastInputMethod(iBinder);
        } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
            Context context = getApplicationContext();
            CharSequence text = "Unfortunately input method switching isn't supported in your version of Android! You will have to do it manually :(";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    public static void commitPNGImage(Uri contentUri, String imageDescription, EditorInfo info, InputConnection connection) {
        InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(imageDescription, new String[]{"image/png"}), null);
        InputConnection inputConnection = connection;
        EditorInfo editorInfo = info;
        int flags = 0;
        if (android.os.Build.VERSION.SDK_INT >= 25) {
            flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        }
        InputConnectionCompat.commitContent(
                inputConnection, editorInfo, inputContentInfo, flags, null);
    }

    public static void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull Uri uri, EditorInfo editorInfo, Context context, InputConnection inputConnection) {

        final Uri contentUri = uri;

        // As you as an IME author are most likely to have to implement your own content provider
        // to support CommitContent API, it is important to have a clear spec about what
        // applications are going to be allowed to access the content that your are going to share.
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            // On API 25 and later devices, as an analogy of Intent.FLAG_GRANT_READ_URI_PERMISSION,
            // you can specify InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION to give
            // a temporary read access to the recipient application without exporting your content
            // provider.
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        } else {
            // On API 24 and prior devices, we cannot rely on
            // InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION. You as an IME author
            // need to decide what access control is needed (or not needed) for content URIs that
            // you are going to expose. This sample uses Context.grantUriPermission(), but you can
            // implement your own mechanism that satisfies your own requirements.
            flag = 0;
            try {
                // TODO: Use revokeUriPermission to revoke as needed.
                context.grantUriPermission(
                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e){
                Log.e(TAG, "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(description, new String[]{mimeType}),
                null /* linkUrl */);
        InputConnectionCompat.commitContent(inputConnection, editorInfo, inputContentInfoCompat,
                flag, null);
    }

    private boolean isCommitContentSupported(
            @Nullable EditorInfo editorInfo, @NonNull String mimeType) {
        if (editorInfo == null) {
            return false;
        }

        final InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        final String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }


    private void deleteStickerEmojisOnStorage(Context context) {
        ArrayList<String> list = Utility.getArrayListString("filePaths", context);
        if (list != null){
            if(!list.isEmpty()) {
                for (String string : list){
                    Uri uri = Uri.parse(string);
                    if(string.startsWith("content://")){
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.delete(uri, null, null);
                    }else {
                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            Log.d(TAG, "in file exist");
                            if (file.delete()) {
                                Log.d(TAG, "in file delete");
                            }
                        }
                    }
                }
            }
        }
    }
}
