package com.example.smartqueue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentDeleteListener deleteListener;
    private OnAppointmentEditListener editListener;

    private boolean isFragment3; // Flag to indicate if it's Fragment3

    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentDeleteListener deleteListener,OnAppointmentEditListener editListener, boolean isFragment3 ) {
        this.appointments = appointments;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
        this.isFragment3 = isFragment3;

    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.dateTextView.setText("Date: " + appointment.getDate());
        holder.timeTextView.setText("Time: " + appointment.getTime());
        holder.hairDesignTextView.setText("Hair Design: " + appointment.getHairDesign());
        holder.fullNameTextView.setText("Full Name: " + appointment.getFullName());
        holder.phoneTextView.setText("Phone: " + appointment.getPhone());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(appointment);
            }
        });
        // Set visibility based on whether it's Fragment3
        if (isFragment3) {
            holder.editButton.setVisibility(View.GONE); // Hide the ImageButton in Fragment3
        } else {
            holder.editButton.setVisibility(View.VISIBLE);// Show the ImageButton in other fragments
            holder.editButton.setOnClickListener(v -> editListener.onEdit(appointment));
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView timeTextView;
        TextView hairDesignTextView;
        TextView fullNameTextView;

        TextView phoneTextView;
        ImageButton deleteButton;
        ImageButton editButton;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.appointmentDateTextView);
            timeTextView = itemView.findViewById(R.id.appointmentTimeTextView);
            hairDesignTextView = itemView.findViewById(R.id.hairDesignTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public interface OnAppointmentDeleteListener {
        void onDelete(Appointment appointment);
}
    public interface OnAppointmentEditListener {
        void onEdit(Appointment appointment);
    }
}