package project.java4.voicerecorder.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import project.java4.voicerecorder.FileAdapter;
import project.java4.voicerecorder.R;

public class AudioListFragment extends Fragment implements FileAdapter.OnItemListClicked {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<File> allFiles;
    private RecyclerView fileList;
    private RecyclerView.LayoutManager layoutManager;
    private FileAdapter adapter;
    DividerItemDecoration dividerItemDecoration ;
    private File fileToPlay;

    private ImageButton playBtn;
    private ImageButton leftRewind;
    private ImageButton rightRewind;
    private TextView playerHeader;
    private TextView playerFileName;

    private SeekBar playerSeekBar;
    private Handler handler;
    private Runnable updateSeekBar;

    private MediaPlayer player = null;
    private boolean isPlaying = false;
    private boolean isFinished = false;
    private int currentPosition;

    public AudioListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        File directory = new File(path);
        allFiles = new ArrayList(Arrays.asList(directory.listFiles()));

        fileList = view.findViewById(R.id.audio_list);
        playBtn = view.findViewById(R.id.play_button);
        leftRewind = view.findViewById(R.id.left_rewind);
        rightRewind = view.findViewById(R.id.right_rewind);
        playerFileName = view.findViewById(R.id.textView);
        playerHeader = view.findViewById(R.id.player_header_title);
        player = new MediaPlayer();

        playerSeekBar = view.findViewById(R.id.time_bar);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                isFinished = true;
                playerHeader.setText("Finished");
            }
        });

        adapter = new FileAdapter(allFiles , this);
        layoutManager = new LinearLayoutManager(getContext());

        fileList.setHasFixedSize(true);
        fileList.setLayoutManager(layoutManager);
        fileList.setAdapter(adapter);
        dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        fileList.addItemDecoration(dividerItemDecoration);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFinished) {
                    if (isPlaying) {
                        if (fileToPlay != null) {
                            pauseAudio();
                        }
                    } else {
                        if (fileToPlay != null) {
                            resumeAudio();
                        }
                    }
                }else {
                    playAudio(fileToPlay);
                    isFinished = false;
                }
            }
        });

        leftRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition - 1 >= 0) {
                    rightRewind.setEnabled(true);
                    stopAudio();
                    playAudio(allFiles.get(currentPosition - 1));
                    currentPosition -= 1;
                }else {
                    leftRewind.setEnabled(false);
                }
            }
        });

        rightRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition + 1 < allFiles.size()) {
                    leftRewind.setEnabled(true);
                    stopAudio();
                    playAudio(allFiles.get(currentPosition + 1));
                    currentPosition += 1;
                }else {
                    rightRewind.setEnabled(false);
                }
            }
        });


        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!isFinished) {
                    if (fileToPlay != null) {
                        int progress = playerSeekBar.getProgress();
                        player.seekTo(progress);
                        resumeAudio();
                    }
                }
                else {
                    playAudio(fileToPlay);
                    int progress = playerSeekBar.getProgress();
                    player.seekTo(progress);
                    isFinished = false;
                }
            }
        });


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if( newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void OnItemCLicked(File file, int position) {
        Log.d("play log" , "file playing: " + file.getName());
        fileToPlay = file;
        currentPosition = position;
        if(!isPlaying){
            if(fileToPlay != null) {
                playAudio(fileToPlay);
            }
        }
        else {
            if(fileToPlay != null) {
                stopAudio();
                playAudio(fileToPlay);
            }
        }
    }

    @Override
    public void OnItemLongCLicked(File file, int position) {
        if(file.exists()) {
            if(isPlaying){
                stopAudio();
            }
            file.delete();
        }
    }

    private void stopAudio() {
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        playerFileName.setText(playerFileName.getText());
        playerHeader.setText(playerHeader.getText());
        isPlaying = false;
        player.stop();
        handler.removeCallbacks(updateSeekBar);
    }

    private void playAudio(File fileToPlay) {
        try {
            player.reset();
            player.setDataSource(fileToPlay.getAbsolutePath());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = true;
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        playerFileName.setText(fileToPlay.getName());
        playerHeader.setText("Playing");

        playerSeekBar.setMax(player.getDuration());

        handler = new Handler();
        updateRunnable();
        handler.postDelayed(updateSeekBar, 0);
    }

    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setProgress(player.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };
    }


    private void pauseAudio(){
        player.pause();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        isPlaying = false;
        handler.removeCallbacks(updateSeekBar);
    }

    private void resumeAudio(){
        player.start();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        isPlaying = true;
        updateRunnable();
        handler.postDelayed(updateSeekBar, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null){
            if(player.isPlaying()){
                stopAudio();
            }
            player = null;
        }
    }
}