package project.java4.voicerecorder.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import project.java4.voicerecorder.R;


public class RecorderFragment extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recButton;
    private Chronometer recordTimer;
    private TextView recordStatus;

    private String recordFile;
    private String recordPath;

    boolean isRecording = false;

    private MediaRecorder mediaRecorder;


    public RecorderFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recorder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        listBtn = view.findViewById(R.id.recorded_list_btn);
        recButton = view.findViewById(R.id.record_button);
        recordTimer = view.findViewById(R.id.record_timer);
        recordStatus = view.findViewById(R.id.file_name);


        listBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                if(isRecording){
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            navController.navigate(R.id.action_recorder2_to_audioListFragment);
                            isRecording = false;
                        }
                    }).setNegativeButton("Stay" , null).create();

                    alert.setTitle("Audio still recording");
                    alert.setMessage("Are you sure you want to exit recording");
                    alert.show();
                }
                else
                    navController.navigate(R.id.action_recorder2_to_audioListFragment);
            }
        });

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording) {
                    onRecording();
                }
                else {
                    stopRecording();
                }
            }
        });
    }

    private void onRecording(){
        if(checkPermission()) {
            isRecording = true;
            recButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording2));
            recordTimer.setBase(SystemClock.elapsedRealtime());
            recordTimer.start();
            startRecording();
        }
    }

    @SuppressLint("SetTextI18n")
    private void startRecording() {

        recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss" , Locale.ENGLISH);
        Date now = new Date();
        recordFile = "/fileName" + simpleDateFormat.format(now) +".3gp";

        recordStatus.setText("Recording..." + recordFile + "Now");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO} , REQUEST_RECORD_AUDIO_PERMISSION);
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void stopRecording(){
        isRecording = false;
        recButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording));
        recordTimer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        recordStatus.setText("Recording Stopped , File Saved at :" + recordPath);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}