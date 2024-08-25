// AdminFragment.java

package com.example.smartqueue.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartqueue.Appointment;
import com.example.smartqueue.AppointmentAdapter;
import com.example.smartqueue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Fragment4 extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView adminAppointmentRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference mDatabase;
    private EditText adminDatePicker;
    private Button loadAppointmentsButton;

    private Button AddNewAppointmentButton;
    private Calendar calendar;

    private Button resetDateButton;

    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_4, container, false);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        resetDateButton = view.findViewById(R.id.resetDateButton);

        resetDateButton.setOnClickListener(v -> {
            adminDatePicker.setText(""); // Clear the date picker
            loadAllAppointments(); // Reload all appointments
        });

        // Initialize UI elements
        adminAppointmentRecyclerView = view.findViewById(R.id.adminAppointmentRecyclerView);
        adminDatePicker = view.findViewById(R.id.adminDatePicker);
        loadAppointmentsButton = view.findViewById(R.id.loadAppointmentsButton);
        AddNewAppointmentButton = view.findViewById(R.id.addNewAppointmentButton);

        calendar = Calendar.getInstance();

        // Initialize RecyclerView
        adminAppointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList, this::showDeleteConfirmationDialog, this::editAppointment,false);
        adminAppointmentRecyclerView.setAdapter(appointmentAdapter);

        // Load all appointments initially
        loadAllAppointments();

        // Set up DatePicker for adminDatePicker
        adminDatePicker.setOnClickListener(v -> showDatePickerDialog());

        // Load appointments for the selected date when the button is clicked
        loadAppointmentsButton.setOnClickListener(v -> loadAppointmentsForSelectedDate()
        );

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Navigation.findNavController(view).navigate(R.id.action_fragment4_to_fragment1);
            }
        });

        AddNewAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_fragment4_to_fragment3);
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        // Set the Locale to the desired language
        Locale locale = new Locale("en");
        Locale.setDefault(locale);

        // Update configuration
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            adminDatePicker.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void loadAllAppointments() {
        mDatabase.child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                        Appointment appointment = timeSnapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            appointmentList.add(appointment);
                        }
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointmentsForSelectedDate() {
        String selectedDate = adminDatePicker.getText().toString();

        if (selectedDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("appointments").child(selectedDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = timeSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(Appointment appointment) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAppointment(appointment))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAppointment(Appointment appointment) {
        if (appointment.getDate() == null || appointment.getTime() == null) {
            Log.e("DeleteAppointment", "Date or Time is null");
            Toast.makeText(getContext(), "Invalid appointment details", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("appointments").child(appointment.getDate()).child(appointment.getTime()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Appointment deleted", Toast.LENGTH_SHORT).show();
                    // Reload either all appointments or the filtered appointments
                    String selectedDate = adminDatePicker.getText().toString();
                    if (selectedDate.isEmpty()) {
                        loadAllAppointments();
                    } else {
                        loadAppointmentsForSelectedDate();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete appointment", Toast.LENGTH_SHORT).show());
    }

    private void editAppointment(Appointment appointment) {
        // Convert the appointment date string to a Date object
        String appointmentDate = appointment.getDate();
        if (appointmentDate == null) {
            Toast.makeText(getContext(), "Invalid date format.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date;
        try {
            date = dateFormat.parse(appointmentDate);
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error parsing date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show TimePickerDialog to select a new time
        showTimePickerDialog((newTime) -> {
            // Use the existing date and the new time to check availability
            checkAppointmentAvailability(appointmentDate, newTime, appointment);
        });
    }

    // Function to check appointment availability
    private void checkAppointmentAvailability(String appointmentDate, String newTime, Appointment appointment) {
        mDatabase.child("appointments").child(appointmentDate).child(newTime).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Appointment time is already booked
                    Toast.makeText(getContext(), "The selected time is already booked. Please choose another time.", Toast.LENGTH_SHORT).show();
                } else {
                    // Appointment time is available, proceed with updating
                    String oldDate = appointment.getDate();
                    String oldTime = appointment.getTime();

                    if (!oldTime.equals(newTime)) {
                        // Remove the old appointment
                        mDatabase.child("appointments").child(oldDate).child(oldTime).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // Update the appointment with the new time
                                    appointment.setTime(newTime);

                                    // Save the updated appointment to the new time node in Firebase
                                    mDatabase.child("appointments").child(oldDate).child(newTime).setValue(appointment)
                                            .addOnSuccessListener(aVoid2 -> Toast.makeText(getContext(), "Appointment updated", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update appointment", Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to remove old appointment", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to check appointment availability", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Define an interface for the callback
    interface OnTimeSelectedListener {
        void onTimeSelected(String newTime);
    }


    private void showTimePickerDialog(OnTimeSelectedListener listener) {
        final String[] times = {
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Time");

        builder.setItems(times, (dialog, which) -> listener.onTimeSelected(times[which]));

        builder.show();
    }




}
