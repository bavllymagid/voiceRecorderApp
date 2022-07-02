package project.java4.voicerecorder.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

import project.java4.voicerecorder.FileAdapter;
import project.java4.voicerecorder.R;

public class AudioListFragment extends Fragment implements FileAdapter.OnItemListClicked {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private File[] allFiles;
    private RecyclerView fileList;
    private RecyclerView.LayoutManager layoutManager;
    private FileAdapter adapter;
    private File fileToPlay;

    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFileName;

    MediaPlayer player = null;
    boolean isPlaying = false;

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
        allFiles = directory.listFiles();

        fileList = view.findViewById(R.id.audio_list);
        playBtn = view.findViewById(R.id.play_button);
        playerFileName = view.findViewById(R.id.textView);
        playerHeader = view.findViewById(R.id.player_header_title);
        player = new MediaPlayer();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        adapter = new FileAdapter(allFiles , this);
        layoutManager = new LinearLayoutManager(getContext());

        fileList.setHasFixedSize(true);
        fileList.setLayoutManager(layoutManager);
        fileList.setAdapter(adapter);

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
        if(!isPlaying){
            fileToPlay = file;
            playAudio(fileToPlay);
            isPlaying = true;
        }
        else {
            stopAudio();
            isPlaying = false;
            playAudio(fileToPlay);
            isPlaying = true;
        }
    }

    private void stopAudio() {
//        player.release();
//        player.stop();
//        player = null;

        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        playerFileName.setText(playerFileName.getText());
        playerHeader.setText(playerHeader.getText());
    }

    private void playAudio(File fileToPlay) {
        try {
            player.setDataSource(fileToPlay.getAbsolutePath());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, null));
        playerFileName.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null){
            player.release();
            player.stop();
            player = null;
        }
    }
}