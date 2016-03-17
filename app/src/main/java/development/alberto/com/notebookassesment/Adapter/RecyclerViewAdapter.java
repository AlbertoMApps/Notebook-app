package development.alberto.com.notebookassesment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import development.alberto.com.notebookassesment.MainActivity;
import development.alberto.com.notebookassesment.Model.Agenda;
import development.alberto.com.notebookassesment.R;
import development.alberto.com.notebookassesment.Interface.SendDataDelete;

/**
 * Created by alber on 15/03/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Agenda> agendaArrayList;
    private int layout;
    private Context context;
    private MainActivity main;

    public RecyclerViewAdapter(ArrayList<Agenda> agendaList, int list_item, Context mainActivity, MainActivity main) {
        this.agendaArrayList = agendaList;
        this.layout = list_item;
        this.context = mainActivity;
        this.main = main;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(context).inflate(layout, parent, false);
        return new ViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.tv_title.setText(agendaArrayList.get(position).getTitle());
            holder.tv_note.setText(agendaArrayList.get(position).getNote());
            holder.tv_lastDate.setText(agendaArrayList.get(position).getLastDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
                //MainActivity main = (MainActivity) context;
                SendDataDelete sendDataDelete = main; //polymofism
                sendDataDelete.sendPostition(position, agendaArrayList.get(position).getTitle(), agendaArrayList.get(position).getNote(), agendaArrayList.get(position).getLastDate());
            }
        });

    }

    @Override
    public int getItemCount() {
        return agendaArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_note, tv_lastDate;
        public ViewHolder(View itemLayout) {
            super(itemLayout);
            tv_title = (TextView) itemLayout.findViewById(R.id.tv_title);
            tv_note = (TextView) itemLayout.findViewById(R.id.tv_note);
            tv_lastDate = (TextView) itemLayout.findViewById(R.id.tv_lastDate);
        }
    }
}
