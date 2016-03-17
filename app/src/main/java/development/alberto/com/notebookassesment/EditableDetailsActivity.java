package development.alberto.com.notebookassesment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.Bind;
import butterknife.ButterKnife;
import development.alberto.com.notebookassesment.Model.Agenda;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by alber on 16/03/2016.
 */
public class EditableDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    @Bind(R.id.editTitle) EditText editTitle;
    @Bind(R.id.editNoteBody)EditText editBody;
    @Bind(R.id.tvDate)TextView tvDate;
    @Bind(R.id.save) ImageView txtSave;
    private ArrayList<Agenda> agendaList;
    private Agenda agenda;
    private Toolbar toolbar;
    private String currentDate;
    //db
    private Realm realm;
    private RealmConfiguration realmConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_layout);
        ButterKnife.bind(this);
        //set up toolbar
            setupToolbar();
        //set up the views...
            txtSave.setVisibility(View.INVISIBLE);
        //Setup model--2nd panel..
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            currentDate = String.valueOf(dateFormat.format(date));
            tvDate.setText(currentDate);

        //2 editText to listen when we are changing content on it...

        editTitle.addTextChangedListener(new TextWatcher() { //listen to when the editTitle is changing..
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                txtSave.setVisibility(View.INVISIBLE);
                editTitle.setHintTextColor(getResources().getColor(R.color.cardview_dark_background));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtSave.setVisibility(View.VISIBLE);
            }
        });

        editBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                txtSave.setVisibility(View.INVISIBLE);
                editBody.setHintTextColor(getResources().getColor(R.color.cardview_dark_background));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtSave.setVisibility(View.VISIBLE);
            }
        });
        //db realm config

        //realmConfiguration
        realm();
//        clearDB(realm);
        try {
            realm.beginTransaction();
                agenda = new Agenda();
                agenda = realm.createObject(Agenda.class);

        }catch( RealmPrimaryKeyConstraintException e){
            e.printStackTrace();
        }finally{
            realm.commitTransaction();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    //Realm methods..
    public void realm() {

        realmConfiguration = new RealmConfiguration.Builder(this).build();
        try {
            //Use of Realm realm for Android
            realm = Realm.getInstance(this);

        } catch (RealmMigrationNeededException e) {
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                Realm.getInstance(realmConfiguration);
            } catch (Exception ex) {
                throw ex;
                //No Realm file to remove.
            }

        }

    }

    private void insertIntoAgenda(final Agenda agenda, final String title, final String body) {
        try {
//            realm.beginTransaction();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) { //do the beginTransaction, cancel in case of error and commit automatically.
                        int pos = realm.where(Agenda.class).max("agendaCode").intValue() + 1;
                        if (pos >= 0) {
                            agenda.setAgendaCode(pos);
                            agenda.setTitle(title);
                            agenda.setNote(body);
                            agenda.setLastDate(currentDate);
                        }
                        realm.copyToRealm(agenda);

                }
            });
        }catch (Exception e ){
            Log.e("Realm Error", "error" + e);
        } finally {
//            realm.commitTransaction();
        }
    }

    public  void clearDB(Realm realm) { //Clear the db data
        realm.beginTransaction();
        realm.allObjects(Agenda.class).clear(); //specify the class you want to clear
        realm.commitTransaction();
    }
    /***
     * Toolbar set up
     */

    private void setupToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        ImageView image = (ImageView) findViewById(R.id.icon);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
                //we have to add all that the user has entered and send it to the MainActivity...
                String title = editTitle.getText().toString();
                String body = editBody.getText().toString();
                if (!(title.isEmpty() || body.isEmpty())) {
                    //we create the model and
                    //insert to the model in the db
                    insertIntoAgenda(agenda, title, body);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    Snackbar.make(v, "Enter a title or note please...", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }
}
