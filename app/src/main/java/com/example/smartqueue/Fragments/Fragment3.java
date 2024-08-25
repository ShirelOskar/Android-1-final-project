// Fragment3.java

package com.example.smartqueue.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartqueue.Appointment;
import com.example.smartqueue.AppointmentAdapter;
import com.example.smartqueue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragment3 extends Fragment {

    private FirebaseAuth mAuth;
    private EditText appointmentDate, appointmentTime;
    private Spinner hairDesignSpinner;
    private Calendar calendar;
    private DatabaseReference mDatabase;
    private RecyclerView appointmentRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        calendar = Calendar.getInstance();

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        appointmentDate = view.findViewById(R.id.appointmentDate);
        appointmentTime = view.findViewById(R.id.appointmentTime);
        hairDesignSpinner = view.findViewById(R.id.hairDesignSpinner);
        Button bookAppointmentButton = view.findViewById(R.id.bookAppointmentButton);
        appointmentRecyclerView = view.findViewById(R.id.appointmentRecyclerView);

        // Initialize RecyclerView
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList,
                new AppointmentAdapter.OnAppointmentDeleteListener() {
                    @Override
                    public void onDelete(Appointment appointment) {
                        showDeleteConfirmationDialog(appointment);
                    }
                },
                new AppointmentAdapter.OnAppointmentEditListener() {
                    @Override
                    public void onEdit(Appointment appointment) {
                        editAppointment(appointment);
                    }
                },
                true  // Set to true to show the edit button
        );
        appointmentRecyclerView.setAdapter(appointmentAdapter);

        // Set up DatePicker for appointmentDate
        appointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up TimePicker for appointmentTime
        appointmentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Set up Book Appointment button
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookAppointment();
            }
        });

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Navigation.findNavController(view).navigate(R.id.action_fragment3_to_fragment1);
            }
        });

        // Load appointments from Firebase
        loadAppointments();

        return view;
    }

    private void editAppointment(Appointment appointment) {
    }

    private void showDatePickerDialog() {
        // Set the Locale to the desired language
        Locale locale = new Locale("en");
        Locale.setDefault(locale);

        // Update configuration
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Create a new DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), null,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Set the OnDateSetListener
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Check if the selected date is disabled (e.g., Monday or Saturday)
                if (isDateDisabled(selectedDate)) {
                    Toast.makeText(getContext(), "Selected date is not available for appointments", Toast.LENGTH_SHORT).show();
                    // Reopen the DatePicker dialog
                    showDatePickerDialog();
                } else {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    appointmentDate.setText(dateFormat.format(calendar.getTime()));
                }

            }
        });

        datePickerDialog.show();
    }



    private boolean isDateDisabled(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        Log.d("dayOfWeek", "dayOfWeek: " + dayOfWeek);
        return dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.SATURDAY;
    }

    private void showTimePickerDialog() {
        final String[] times = {
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Time");

        builder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appointmentTime.setText(times[which]);
            }
        });

        builder.show();
    }

    private void bookAppointment() {
        String date = appointmentDate.getText().toString();
        String time = appointmentTime.getText().toString();
        String hairDesign = hairDesignSpinner.getSelectedItem().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail(); // Get the user's email
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            DatabaseReference appointmentsRef = mDatabase.child("appointments").child(date);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String firstName = dataSnapshot.child("firstname").getValue(String.class);
                    String lastName = dataSnapshot.child("lastname").getValue(String.class);
                    String phoneNum = dataSnapshot.child("phone").getValue(String.class);
                    String fullName = firstName + " " + lastName;

                    // Check if the user already has an appointment on the selected date
                    appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dateSnapshot) {
                            boolean alreadyHasAppointment = false;

                            for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                                Appointment existingAppointment = timeSnapshot.getValue(Appointment.class);
                                if (existingAppointment != null && existingAppointment.getUserId().equals(userId)) {
                                    alreadyHasAppointment = true;
                                    break;
                                }
                            }

                            if (alreadyHasAppointment && !"admin@gmail.com".equals(userEmail)) {
                                Toast.makeText(getContext(), "You already have an appointment on this date", Toast.LENGTH_SHORT).show();
                            } else {
                                // Proceed to check slot availability and book the appointment
                                DatabaseReference slotRef = mDatabase.child("appointments").child(date).child(time);
                                slotRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String status = snapshot.child("status").getValue(String.class);
                                            if (status != null && "available".equals(status)) {
                                                bookSlot(slotRef, userId, date, time, hairDesign, fullName, phoneNum);
                                            } else {
                                                Toast.makeText(getContext(), "Selected time slot is already taken", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            bookSlot(slotRef, userId, date, time, hairDesign, fullName, phoneNum);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Failed to check slot availability: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load appointments.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching data", databaseError.toException());
                    Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void bookSlot(DatabaseReference slotRef, String userId, String date, String time, String hairDesign, String fullName, String phone) {
        // Generate a unique ID for the appointment
        String appointmentId = slotRef.push().getKey();
        if (appointmentId == null) {
            Toast.makeText(getContext(), "Failed to generate appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the ID to ensure it's generated correctly
        Log.d("Appointment ID", "Generated Appointment ID: " + appointmentId);

        // Create a new appointment object with the required fields
        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("id", appointmentId);
        appointmentData.put("date", date);
        appointmentData.put("time", time);
        appointmentData.put("hairDesign", hairDesign);
        appointmentData.put("userId", userId);
        appointmentData.put("status", "taken"); // Optional, if you want to save status
        appointmentData.put("fullName", fullName);
        appointmentData.put("phone", phone);

        // Save the appointment data to the Firebase database
        slotRef.setValue(appointmentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Appointment booked", Toast.LENGTH_SHORT).show();
                    appointmentDate.setText("");
                    appointmentTime.setText("");
                    loadAppointments();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to book appointment", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAppointments() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DatabaseReference appointmentsRef = mDatabase.child("appointments");

            appointmentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    appointmentList.clear();

                    // Iterate through all dates
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        // Iterate through all times under each date
                        for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                            Appointment appointment = timeSnapshot.getValue(Appointment.class);

                            if (appointment != null) {
                                String appointmentUserId = appointment.getUserId();

                                if (appointmentUserId != null && appointmentUserId.equals(userId)) {
                                    appointmentList.add(appointment);
                                } else {
                                    Log.w("Appointment", "User ID is null or does not match");
                                }
                            } else {
                                Log.w("Appointment", "Appointment object is null");
                            }
                        }
                    }

                    appointmentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }
//    private void loadAppointments() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//            DatabaseReference userAppointmentsRef = mDatabase.child("users").child(userId).child("appointments");
//
//            userAppointmentsRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    appointmentList.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Appointment appointment = snapshot.getValue(Appointment.class);
//                        if (appointment != null) {
//                            appointmentList.add(appointment);
//                        }
//                    }
//                    appointmentAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle possible errors.
//                }
//            });
//        }
//    }

    private void showDeleteConfirmationDialog(Appointment appointment) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAppointment(appointment);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAppointment(Appointment appointment) {
        if (appointment == null) {
            Toast.makeText(getContext(), "Appointment is null", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = appointment.getDate();
        String time = appointment.getTime();

        if (date == null || time == null) {
            Toast.makeText(getContext(), "Invalid appointment data", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference appointmentRef = mDatabase.child("appointments").child(date).child(time);

        appointmentRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Appointment deleted", Toast.LENGTH_SHORT).show();
                    loadAppointments(); // Refresh the appointment list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                });
    }

}
