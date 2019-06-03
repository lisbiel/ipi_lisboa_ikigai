package br.com.lisboa.ikigai;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InsertFragment extends DialogFragment {
    private EditText insertText;
    private TextView sugeLabel;
    private GridView gridSugestions;
    private GridView addedGrid;
    private Button addButton;
    private String inserting;

    private String typing;
    DatabaseReference mDatabase;

    private ArrayList<String> sugestions;

    ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> added;

    ArrayAdapter<String> adapter;


    private
    MainActivityFragment getDoodleFragment(){
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//builder para o diálogo
        AlertDialog.Builder builder =
                new
                        AlertDialog.Builder(getActivity());
//infla a view do fragment
        View insertDialogView =
                getActivity().
                        getLayoutInflater().
                        inflate(R.layout.
                                        fragment_insert
                                ,
                                null
                        );
        builder.setView(insertDialogView);
        builder.setTitle((getContext().getString(
                R.string.insert,
                inserting
        )));

        addedGrid =
                insertDialogView.findViewById(R.id.addedGrid);
        insertText =
                insertDialogView.findViewById(R.id.insertTex);
        addButton =
                insertDialogView.findViewById(R.id.addButton);
        sugeLabel =
                insertDialogView.findViewById(R.id.sugeLabel);
        gridSugestions =
                insertDialogView.findViewById(R.id.gridSugestions);

        sugestions = new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(typing).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            sugestions.add(ds.getValue(AddedStrings.class).getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, sugestions);
        adapter.notifyDataSetChanged();

        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, added);
        arrayAdapter.notifyDataSetChanged();
        addedGrid.setAdapter(arrayAdapter);

        gridSugestions.setAdapter(adapter);

        gridSugestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String word= parent.getItemAtPosition(position).toString();
                    if(getDoodleFragment().getDoodleView().addWord(word))
                        arrayAdapter.notifyDataSetChanged();
            }
        });

        addButton.setOnClickListener( (v) -> {
            if (insertText.toString() != "") {
                String word = String.valueOf(insertText.getText());
                if(getDoodleFragment().getDoodleView().addWord(word))
                    arrayAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                arrayAdapter.notifyDataSetChanged();
                if(!sugestions.contains(word))
                    mDatabase.child(typing).push().setValue(new AddedStrings(word));
            }
            insertText.setText("");
                });

//configura um botão com o texto e o observador
        builder.setPositiveButton(
                R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
//devolve o dialogo construído pelo builder
        return builder.create();
    }

    @Override
    public void
    onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();
        if
        (fragment != null){
//o diálogo está sendo exibido
            fragment.setDialogOnScreen(true);
        }
    }
    @Override
    public void
    onDetach() { super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();
        if (fragment != null)
//o diálogo não está mais na tela
            fragment.setDialogOnScreen(false);
    }

    public void setArray(ArrayList<String> added) {
        this.added = added;
    }

    public void setInserting(String inserting) {
        this.inserting = inserting;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}
