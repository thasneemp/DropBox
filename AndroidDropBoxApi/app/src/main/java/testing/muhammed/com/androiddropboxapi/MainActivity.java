package testing.muhammed.com.androiddropboxapi;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String ACCESS_TOKEN = "a7S9agd7udAAAAAAAAAAR0THh_GQ6_MOhgSIJVrJlJjhSBHeomtgUMQwcMbyMIbL";
    private DbxRequestConfig.Builder config;
    private DbxClientV2 client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = DbxRequestConfig.newBuilder("dropbox/java-tutorial");
        client = new DbxClientV2(config.build(), ACCESS_TOKEN);
        new MyTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class MyTask extends AsyncTask<Void, Void, DbxDownloader<SharedLinkMetadata>> {

        @Override
        protected DbxDownloader<SharedLinkMetadata> doInBackground(Void... voids) {

            try {
                FullAccount currentAccount = client.users().getCurrentAccount();
                String email = currentAccount.getEmail();
                Log.d("TAG", "doInBackground: " + email);
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Video" + File.separator + "one.mp4");

                FileInputStream fileInputStream = new FileInputStream(file);

                FileMetadata fileMetadata = client.files().uploadBuilder(file.getPath()).withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);

                String url = client.sharing().createSharedLinkWithSettings(fileMetadata.getPathLower()).getUrl();


//                DbxDownloader<SharedLinkMetadata> sharedLinkWithSettings = client.sharing().getSharedLinkFile(fileMetadata.getPathLower());
//
//                String url = sharedLinkWithSettings.getResult().getUrl();


                DbxDownloader<SharedLinkMetadata> sharedLinkFile = client.sharing().getSharedLinkFile("https://www.dropbox.com/s/few8kv88uprzszq/trim.0E4AF61F-3F1B-4676-A2C9-D967F7B6B123.MOV?dl=0");


//                SharedLinkMetadata result = client.sharing().getSharedLinkFileBuilder("https://www.dropbox.com/s/bvwe2trni1zqpkz/trim.115F6714-B639-49D6-9ADD-5918D9BECCDB.MOV?dl=1").start().getResult();
                SharedLinkMetadata result = client.sharing().getSharedLinkFileBuilder(url).start().getResult();


                String name = fileMetadata.getName();
                Log.d("", "doInBackground: " + name);

                return sharedLinkFile;


            } catch (DbxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DbxDownloader<SharedLinkMetadata> fileMetadata) {
            super.onPostExecute(fileMetadata);
            new DownloadFile().execute(fileMetadata);

        }
    }

    private class DownloadFile extends AsyncTask<DbxDownloader<SharedLinkMetadata>, Void, Void> {

        @Override
        protected Void doInBackground(DbxDownloader<SharedLinkMetadata>... metadatas) {
            try {
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);

                File file = new File(path, metadatas[0].getResult().getName());


                try {
                    OutputStream stream = new FileOutputStream(file);

                    SharedLinkMetadata download1 = metadatas[0].download(stream);



                    Log.d("", "doInBackground: ");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (DbxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
