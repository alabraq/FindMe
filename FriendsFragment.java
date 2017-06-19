package com.example.findme;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.Timer;

public class FriendsFragment extends Fragment {

    SearchView svFriends;
    ListView lvFriends, lvSearchFriends;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        FriendsConnection friendsConnection = new FriendsConnection();
        friendsConnection.getUrl(
                getString(R.string.get_friends),
                getString(R.string.search_friends),
                getString(R.string.add_friends));
        friendsConnection.getContextActivity(getContext(), getActivity());
        friendsConnection.getFriends(LoginActivity.USER_ID);

        svFriends = (SearchView)view.findViewById(R.id.search_friends);
        lvFriends = (ListView)view.findViewById(R.id.list_item_friends);
        lvSearchFriends = (ListView)view.findViewById(R.id.list_item_search_friends);

        searchViewListener();

        return view;
    }


    private void searchViewListener() {

        svFriends.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    lvFriends.setVisibility(View.GONE);
                    lvSearchFriends.setVisibility(View.VISIBLE);
                } else {
                    lvFriends.setVisibility(View.VISIBLE);
                    lvSearchFriends.setVisibility(View.GONE);
                }
            }
        });

        svFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.equals("")) {
                    FriendsConnection friendsConnection = new FriendsConnection();
                    friendsConnection.getContextActivity(getContext(), getActivity());
                    friendsConnection.searchFriends(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                return false;
            }
        });

    }

}
