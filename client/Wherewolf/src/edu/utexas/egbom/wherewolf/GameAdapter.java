package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;

import edu.utexas.egbom.wherewolf.Game;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<Game>{
    public GameAdapter(Context context, ArrayList<Game> game) {
        super(context, 0, game);
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Game game = getItem(position);
        // Check if an existing view is being reused,
        //  otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_game, parent, false);
        }

        // Lookup view for data population
        TextView nameTV = (TextView) convertView.findViewById(R.id.game_name);
        // Populate the data into the template view using the data object
       
        nameTV.setText(game.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
