package development.alberto.com.notebookassesment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shamanland.fab.ShowHideOnScroll;

import java.util.ArrayList;

import butterknife.Bind;
import development.alberto.com.notebookassesment.Adapter.RecyclerViewAdapter;
import development.alberto.com.notebookassesment.Interface.SendDataDelete;
import development.alberto.com.notebookassesment.Model.Agenda;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 *
 * Exercise explanation:
     1- List screen
     List of notes show for each: title, first line of the note body and last modification date.
     A floating “add” button towards the top of the list (that disappears when the list scrolls down). The floating button must disappear in a smooth manner (open to developer choice)

     2- Detail screen
     Details are made of 2 editable fields + 1 non-editable field:
     The title text field has a title + hint (editable field)
     The body has a title + hint and occupies the remaining part of the screen. For the sake of this exercise, the text is limited to 200 characters (editable field).
     The date & time of the last modification of the note (non editable)
     A “Save” item is displayed in action bar when either the title or body changes.
     A “Delete” item is displayed in the action bar when opening a note. Upon click on “delete”, a confirmation dialog is displayed to ask for confirmation. Upon confirmation, when deletion is completed, back to the list
 */

public class MainActivity extends AppCompatActivity implements SendDataDelete {

    private static final String TAG = "TAG";
    private android.support.v7.widget.Toolbar toolbar;
    @Bind(R.id.list) RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FloatingActionButton btnAdd;
    private ArrayList<Agenda> agendaArrayList;
    private EditableDetailsActivity editableDetailsActivity;
    @Bind(R.id.delete) ImageView imgDelete;
    //db
    private Realm realm;
    private RealmConfiguration realmConfiguration;
    private ArrayList<Agenda> allAgendaData;
    private int position;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Let's first set up toolbar
        setupToolbar();
        //Initialize Views
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setVisibility(View.INVISIBLE);
        imgDelete = (ImageView) findViewById(R.id.delete);
        imgDelete.setVisibility(View.INVISIBLE);

        //set up Recycler view to show the model...
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //set up realm conf to get acces to the db
        realm();
        //Checking if the data is null
        if (getAllAgendaData()!=null) {
            agendaArrayList = getAllAgendaData();
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter = new RecyclerViewAdapter(agendaArrayList, R.layout.list_item, getBaseContext(), MainActivity.this);

        } else {
            Toast.makeText(getApplicationContext(), "There is no data to display", Toast.LENGTH_SHORT).show();
        }
        recyclerView.setOnTouchListener(new ShowHideOnScroll(btnAdd));
        recyclerView.setAdapter(recyclerViewAdapter);
        //Floating Action Button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Esto es una prueba", Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(view.getContext(), EditableDetailsActivity.class));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    //realm data methods

    public ArrayList<Agenda> getAllAgendaData() {
        RealmQuery<Agenda> query = realm.where(Agenda.class);
        RealmResults<Agenda> result = query.findAll();
        ArrayList<Agenda> arrayList = new ArrayList<>();

        for (Agenda a : result) {
            Log.d("Data retreived: code", a.getAgendaCode() + ",  note: " + a.getNote());
            arrayList.add(a);
        }
        return arrayList;
    }

    public ArrayList<String> getActualRow(int position, String ActualTitle){
        ArrayList<String> arrayRow = new ArrayList<>();
        try {
            realm.beginTransaction();
            RealmQuery<Agenda> query = realm.where(Agenda.class);
            RealmResults<Agenda> result = query.findAll();

            String title = null, note = null, date = null;

            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getTitle().equals(ActualTitle)) {
                    title = result.get(i).getTitle();
                    note  = result.get(i).getNote();
                    date  = result.get(i).getLastDate();
                }
            }
            arrayRow.add(title);
            arrayRow.add(note);
            arrayRow.add(date);

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            realm.commitTransaction();
        }
        return arrayRow;
    }

    public void deleteRow(int position, String titleToDelete){

    try {
        realm.beginTransaction();
        RealmQuery<Agenda> query = realm.where(Agenda.class);
        RealmResults<Agenda> result = query.findAll();

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getTitle()==(titleToDelete)) {
                result.get(i).removeFromRealm();
                Log.d(" data deleted", result.get(i).getTitle());
            }
        }
    }catch(Exception e){
        e.printStackTrace();
    } finally {
        realm.commitTransaction();
    }
    }

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

    /***
     * Toolbar set up
     */

    private void setupToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        ImageView image = (ImageView) findViewById(R.id.icon);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked"); //click on the delete button
                //Snackbar.make(v, "Esto es una prueba" + getPosition(), Snackbar.LENGTH_LONG).show();
                //delete method of realm
                //deleteRow(position, getTitleToDelete());
                alertDialog();
            }
        });
    }

    public void alertDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to delete this item ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRow(position, getTitleToDelete());
                        //refresh the data
                        realm.refresh();
                        agendaArrayList = getAllAgendaData();
                        recyclerViewAdapter = new RecyclerViewAdapter(agendaArrayList, R.layout.list_item, getBaseContext(), MainActivity.this);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void recreate() {
        super.recreate();
        realm.refresh();
        imgDelete = (ImageView) findViewById(R.id.delete);
    }

    @Override
    public void sendPostition(int position, String title, String note, String date) {
        //set visibility of the button and set the postion clicked..// STOPSHIP: 16/03/2016
        imgDelete.setVisibility(View.VISIBLE);
       // Toast.makeText(getApplicationContext(), "position: " + position, Toast.LENGTH_LONG).show();
        setPositionItem(position);
        setTitleToDelete(title);
        //ArrayList<String> arrayList = this.getActualRow(position, title);
        Toast.makeText(getBaseContext(), " Title:  " + title +  " Note:  " + note +" Date:  "+date, Toast.LENGTH_SHORT).show();
    }

    private void setTitleToDelete(String title) {
        this.title = title;
    }
    private String getTitleToDelete() {
       return this.title ;
    }

    public void setPositionItem(int position) {
        this.position = position;
    }
    public int getPosition (){
        return this.position;
    }
}//end class
