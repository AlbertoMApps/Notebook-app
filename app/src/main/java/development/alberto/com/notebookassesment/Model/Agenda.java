package development.alberto.com.notebookassesment.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by alber on 15/03/2016.
 */
public class Agenda extends RealmObject {
    @PrimaryKey
    private int agendaCode;
    private String title;
    private String note;
    private String lastDate;

    public Agenda (){
    }

//    public Agenda(int agendaCode, String title, String note, String lastDate) {
//
//        this.agendaCode = agendaCode;
//        this.lastDate = lastDate;
//        this.note = note;
//        this.title = title;
//    }


    public int getAgendaCode() {
        return agendaCode;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setAgendaCode(int agendaCode) {
        this.agendaCode = agendaCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }
}
