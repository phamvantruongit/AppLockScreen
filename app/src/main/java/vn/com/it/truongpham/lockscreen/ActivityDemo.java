package vn.com.it.truongpham.lockscreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.paperdb.Paper;
import vn.com.it.truongpham.volley.R;


public class ActivityDemo extends android.app.Activity {
    PatternLockView patternLockView;
    private String save_pattern_key = "pattern_code";
    String final_pattern = "patternLockView";

    private DatabaseReference databaseReference;
    String device_id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final String deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;


        final String myVersion = android.os.Build.VERSION.RELEASE;
        final int sdkVersion = android.os.Build.VERSION.SDK_INT;

        //final DatabaseReference[] mDatabase = new DatabaseReference[1];


        Paper.init(this);
        final String save_pattern = Paper.book().read(save_pattern_key);
        if (save_pattern != null && !save_pattern.equals("null")) {
            start(save_pattern);

        } else {
            setContentView(R.layout.activity_main);
            patternLockView = findViewById(R.id.patternLockView);
            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(patternLockView, pattern);
                }

                @Override
                public void onCleared() {

                }
            });
            findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!final_pattern.equals("patternLockView")) {

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(device_id);
                        Map<String, String> data = new HashMap<>();
                        data.put("device_id", myVersion + " " + sdkVersion);
                        data.put("device_name", deviceName);
                        data.put("key_pass", final_pattern);
                        databaseReference.setValue(data);
                        Paper.book().write(save_pattern_key, final_pattern);
                        Paper.init(ActivityDemo.this);
                        final String save_pattern = Paper.book().read(save_pattern_key);
                        start(save_pattern);
                    }
                }
            });
        }


    }

    private void start(final String save_pattern) {
        setContentView(R.layout.parent_screen);
        patternLockView = findViewById(R.id.patternLockView);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(final List<PatternLockView.Dot> pattern) {

                final_pattern = PatternLockUtils.patternToString(patternLockView, pattern);
                if (final_pattern.equals(save_pattern)) {
                    startActivity(new Intent(ActivityDemo.this, ActivityDetail.class));
                    finish();
                } else {
                    Toast.makeText(ActivityDemo.this, "Incorrect password ", Toast.LENGTH_SHORT).show();
                    Button btnPass = findViewById(R.id.btnPass);
                    btnPass.setVisibility(View.VISIBLE);
                    btnPass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Dialog dialog = new Dialog(ActivityDemo.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.show();
                            if (dialog != null) {
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            }
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(device_id);

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    User user = dataSnapshot.getValue(User.class);

                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("update_users").child(device_id);
                                    Map<String, String> data = new HashMap<>();
                                    data.put("device_id", device_id);
                                    data.put("email", "phamvantruongit@gmail.com");
                                    data.put("key_pass",user.key_pass);
                                    databaseReference.setValue(data);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    });
                }
            }

            @Override
            public void onCleared() {

            }
        });

    }
}
