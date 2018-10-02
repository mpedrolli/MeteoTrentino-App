package it.chiarani.meteotrentinoapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import it.chiarani.meteotrentinoapp.R;
import it.chiarani.meteotrentinoapp.xml_parser.XmlDatiOggi;

public class AllerteListAdapter extends RecyclerView.Adapter<AllerteListAdapter.ViewHolder> {

  // #region private fields
  private ArrayList<String> data;
  // #endregion

  public AllerteListAdapter(ArrayList<String> data) {
    this.data = data;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView txt_name;
    Button btn_link;


    public ViewHolder(View v) {
      super(v);
      txt_name = v.findViewById(R.id.item_allerte_txt_name);
      btn_link = v.findViewById(R.id.item_allerte_btn_link);
    }
  }

  @Override
  public AllerteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View singleItemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allerte, parent, false);
    return new ViewHolder(singleItemLayout);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    //Set data to the individual list item
    holder.txt_name.setText(data.get(position).split(";")[0]);

    holder.btn_link.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).split(";")[1]));
        v.getContext().startActivity(browserIntent);
      }
    });
  }

  @Override
  public int getItemCount() {
    //Return the number of items in your list
    return data.size();
  }
}