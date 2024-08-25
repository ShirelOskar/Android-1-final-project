package com.example.smartqueue.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.smartqueue.Person;
import com.example.smartqueue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fragment2 extends Fragment {
    private FirebaseAuth mAuth;
    private static final String TAG = "Fragment2";
    EditText emailT;
    EditText passT;
    EditText firstname ;
    EditText lastname ;
    EditText phone ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_2, container, false);
        Button reg2Btn = view.findViewById(R.id.button2);
        emailT = view.findViewById(R.id.mailReg);
        passT = view.findViewById(R.id.passReg);
        firstname = view.findViewById(R.id.firstNameReg);
        lastname = view.findViewById(R.id.lastNameReg);
        phone = view.findViewById(R.id.phoneReg);

        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("[a-zA-Z]*")) {
                    // If the text does not match the allowed pattern, remove the last character
                    firstname.setText(s.toString().replaceAll("[^a-zA-Z]", ""));
                    firstname.setSelection(firstname.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("[a-zA-Z]*")) {
                    // If the text does not match the allowed pattern, remove the last character
                    lastname.setText(s.toString().replaceAll("[^a-zA-Z]", ""));
                    lastname.setSelection(lastname.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        reg2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg(view);

            }
        });
        return view;

    }

    public void reg(View view) {
        emailT = view.findViewById(R.id.mailReg);
        passT = view.findViewById(R.id.passReg);
        firstname = view.findViewById(R.id.firstNameReg);
        lastname = view.findViewById(R.id.lastNameReg);
        phone = view.findViewById(R.id.phoneReg);

        String email = emailT.getText().toString();
        String password = passT.getText().toString();
        if (!email.isEmpty() && !password.isEmpty() && firstname != null && lastname != null && phone != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                addData(view);
                                Toast.makeText(getActivity(), "register ok", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.action_fragment2_to_fragment3);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), "register failed", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "please type all the details", Toast.LENGTH_LONG).show();

        }
    }


    public void addData(View view) {


        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        Person p = new Person(emailT.getText().toString(), phone.getText().toString(), firstname.getText().toString(), lastname.getText().toString(), passT.getText().toString());

        myRef.setValue(p);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        if (emailT != null) {
            emailT.setText("");
        }
        if (passT != null) {
            passT.setText("");
        }
        if (phone != null) {
            phone.setText("");
        }
        if (firstname!= null) {
            firstname.setText("");
        }

        if (lastname != null) {
            lastname.setText("");
        }
        // Pause any operations that should not continue when the fragment is not visible

    }

}