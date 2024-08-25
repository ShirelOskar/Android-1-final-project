package com.example.smartqueue.Fragments;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.Manifest;
import com.example.smartqueue.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Locale;


public class Fragment1 extends Fragment  {
    private FirebaseAuth mAuth;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText emailT ;
    EditText passT ;
    private static final String TAG = "Fragment1";


    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void login(View view){


        String email = emailT.getText().toString();
        String password = passT.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( getActivity(),new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(email.equals("admin@gmail.com")){
                                Toast.makeText(getContext(), "login ok", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.action_fragment1_to_fragment4);
                            }
                            else {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getContext(), "login ok", Toast.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.action_fragment1_to_fragment3);
                            }
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            // Toast.makeText(getContext(), "login failed", Toast.LENGTH_LONG).show();

                            // Sign-up failed
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Invalid email or password
                                Toast.makeText(getContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already exists
                                Toast.makeText(getContext(), "User already exists.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthException e) {
                                // Firebase Auth error
                                Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // General error
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_1, container, false);


        Button loginBtn = view.findViewById(R.id.button1);
        Button reg1Btn = view.findViewById(R.id.button3);
        emailT = view.findViewById(R.id.email);
        passT = view.findViewById(R.id.password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(view);

            }
        });
        reg1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_fragment1_to_fragment2);
            }
        });

        return view;

    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        // Register for any necessary broadcasts or initialize non-UI components
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume any operations that should continue when the fragment is visible
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

        // Pause any operations that should not continue when the fragment is not visible

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        // Stop any operations that should not continue when the fragment is not visible
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        // Clean up resources related to the fragment's view
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        // Clean up any remaining resources
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Detach from activity, clean up references
    }



}

