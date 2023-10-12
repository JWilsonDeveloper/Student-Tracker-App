package com.example.studenttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.List;

public class TermActivity extends AppCompatActivity implements TermFragment.ReturnValues {

    TrackerDatabase trackerDatabase;
    RecyclerView recyclerView;
    TermAdapter termAdapter;
    TermFragment fragment;
    MenuItem coursesBtn;
    MenuItem addBtn;
    Term selectedTerm;
    boolean fragmentUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout and recyclerView
        if(savedInstanceState != null && savedInstanceState.containsKey("fragment")){
            if(savedInstanceState.get("fragment").toString() == "1") {
                loadFragMenu();
            }
        }
        setContentView(R.layout.activity_term);
        trackerDatabase = TrackerDatabase.getInstance(this);
        recyclerView = findViewById(R.id.termRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        termAdapter = new TermAdapter(trackerDatabase.getTerms());
        recyclerView.setAdapter(termAdapter);
    }

    private class TermHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Term term;
        private TextView textView;

        public TermHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Term term, int position) {
            this.term = term;
            textView.setText(term.getTitle());
        }

        @Override
        public void onClick(View view) {

            // Mark selected term
            selectedTerm = term;

            // Start TermFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the TermFragment to the fragment container
            fragment = new TermFragment();
            Bundle args = new Bundle();
            args.putInt("id", term.getId());
            args.putString("title", term.getTitle());
            args.putString("startDate", term.getStartDate().toString());
            args.putString("endDate", term.getEndDate().toString());
            fragment.setArguments(args);
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            loadFragMenu();
        }
    }

    private class TermAdapter extends RecyclerView.Adapter<TermHolder> {

        private List<Term> termList;

        public TermAdapter(List<Term> terms) {
            termList = terms;
        }

        @Override
        public TermHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new TermHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TermHolder holder, int position){
            holder.bind(termList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return termList.size();
        }

        public void refreshTerms(List<Term> terms) {
            termList = terms;
        }
    }

    @Override
    public void returnValues(int termId, String termTitle, LocalDate startDate, LocalDate endDate, boolean toDelete) {

        // Delete, add, or update based on termId and toDelete
        if (toDelete) {

            // Delete isn't necessary if term hasn't be added to the database
            if(termId != -1) {

                // Delete term
                try {
                    Term term = trackerDatabase.getTerm(termId);
                    trackerDatabase.deleteTerm(term);
                    List<Term> terms = trackerDatabase.getTerms();
                    termAdapter.refreshTerms(terms);
                    termAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (termId == -1) {

            // Add Term
            Term term = new Term(termId, termTitle, startDate, endDate);

            // Add new term to the database
            trackerDatabase.addTerm(term);
            List<Term> terms = trackerDatabase.getTerms();
            termAdapter.refreshTerms(terms);
            termAdapter.notifyDataSetChanged();
        }
        else if (termId > 0) {

            // Create Term
            Term term = new Term(termId, termTitle, startDate, endDate);

            // Update term in database
            trackerDatabase.updateTerm(term);
            List<Term> terms = trackerDatabase.getTerms();
            termAdapter.refreshTerms(terms);
            termAdapter.notifyDataSetChanged();
        }

        // User goes from TermFragment to TermActivity
        fragment = null;
        selectedTerm = null;
        loadActivityMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        coursesBtn = menu.findItem(R.id.action_courses);
        addBtn = menu.findItem(R.id.action_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (fragmentUp) {
            loadFragMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            // Create TermFragment and add it to the fragment container
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new TermFragment();

            // User goes from TermActivity to TermFragment
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            loadFragMenu();
        }
        else if (item.getItemId() == android.R.id.home) {
            if(fragmentUp) {

                // User goes from TermFragment to TermActivity
                selectedTerm = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(fragment).commit();
                loadActivityMenu();
            }
            else{

                // User goes from TermActivity to HomeActivity
                finish();
            }
        }
        else if (item.getItemId() == R.id.action_courses) {

            // User goes from TermFragment to CourseActivity
            Intent intent = new Intent(this, CourseActivity.class);
            intent.putExtra("termId", selectedTerm.getId());
            startActivity(intent);
        }

        return true;
    }

    public void loadFragMenu(){
        fragmentUp = true;
        addBtn.setVisible(false);
        if(selectedTerm != null){
            coursesBtn.setVisible(true);
        }
    }
    public void loadActivityMenu(){
        fragmentUp = false;
        addBtn.setVisible(true);
        coursesBtn.setVisible(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(fragmentUp) {

            // Maintain term id if device orientation changes
            savedInstanceState.putBoolean("fragmentUp", true);
            if (selectedTerm != null) {
                savedInstanceState.putInt("selectedId", selectedTerm.getId());
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("fragmentUp")){

            // Reload term id and fragment if fragment is up
            fragmentUp = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment = (TermFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            if(savedInstanceState.containsKey("selectedId")) {
                try {
                    selectedTerm = trackerDatabase.getTerm(savedInstanceState.getInt("selectedId"));
                }
                catch (Exception e) {
                    Toast.makeText(this, "Error loading term", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}