package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;

import edu.utexas.egbom.wherewolf.Player;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerAdapter extends ArrayAdapter<Player>{
    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Player player = getItem(position);
        // Check if an existing view is being reused,
        //  otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_player, parent, false);
        }

        // Lookup view for data population
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.playerimg);
        TextView nameTV = (TextView) convertView.findViewById(R.id.player_name);
        TextView votesTV = (TextView) convertView.findViewById(R.id.player_votes);
        // Populate the data into the template view using the data object
        if (player.getProfilePic().equals("femalevillager1")){
          // pull image for the female picture. etc
        }
        else
        {
          profileImg.setImageResource(R.drawable.villager3);
        }
        nameTV.setText("     "+player.getName()+"      ");
        votesTV.setText(String.valueOf(player.getNumVotes())); // problem is here.
        
        // Return the completed view to render on screen
        return convertView;
    }
}
