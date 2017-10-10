package testing.muhammed.com.androiddropboxapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    final static private String APP_KEY = "hgfhskc0zu6zeqy";
    final static private String APP_SECRET = "az12xf4vciavl90";
    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        boolean linked = session.isLinked();
        mDBApi = new DropboxAPI<>(session);


        // MyActivity below should be your activity class name
//        if (AppStorage.getStringData(this, "ACCESS_TOKEN").equalsIgnoreCase("")) {
        mDBApi.getSession().startOAuth2Authentication(this);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();


                AppStorage.insertStringData(this, "ACCESS_TOKEN", accessToken);

                new MyTask().execute();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DropboxAPI.Entry response = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "recordmaster" + File.separator + "test.mp4");
                FileInputStream inputStream = new FileInputStream(file);


                response = mDBApi.putFile("/test.mp4", inputStream,
                        file.length(), null, null);
            } catch (DropboxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
            return null;
        }
    }
}
