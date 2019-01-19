package com.mathprob.android.rollie;

import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.service.voice.VoiceInteractionService;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.media.MediaPlayer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spin1;
    private Button newSess;
    private Button joinSess;
    private Button cli;
    private Button start;
    private Button add;
    private Button remove;
    private Button clear;
    private Button end;
    private TextView num;
    private TextView shown;
    private EditText inviteCode;
    private FrameLayout rollIt;
    private int roll;
    private int high;
    private EditText usr;
    private String username;
    private boolean admin;
    private boolean removeIt = false;
    private String addition = ""; //Format: D4_4~D6_3~ is equivalent to: 4 D4s and 3 D6s
    private int numberOfDie = 0;
    private String invite = "FFFF";

    private int numD6 = 9;

    private JsonObjectRequest plz;
    private RequestQueue req;
    private String URL;


    private TextView dice8[];
    private ImageView blank4s[];
    private ImageView blank8s[];
    private ImageView blank6s[];
    private ImageView blank10xs[];
    private ImageView blank12s[];
    private ImageView blank20s[];
    private RelativeLayout sides[];


    private String stack = "Current stack is:\n";

    private MediaPlayer sound;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();
        sound = MediaPlayer.create(this, R.raw.roll_dice);

        newSess = (Button) findViewById(R.id.new_session);
        joinSess = (Button) findViewById(R.id.join_session);
        cli = (Button) findViewById(R.id.roll);
        add = (Button) findViewById(R.id.add);
        clear = (Button) findViewById(R.id.clc);
        remove = (Button) findViewById(R.id.remove);
        end = (Button) findViewById(R.id.end);
        num = (TextView) findViewById(R.id.number);
        shown = (TextView) findViewById(R.id.display);
        usr = (EditText) findViewById(R.id.usrname);
        inviteCode = (EditText) findViewById(R.id.invite_code);
        start = (Button) findViewById(R.id.start);
        rollIt = (FrameLayout) findViewById(R.id.FrameLayouytRoll);
        sides = new RelativeLayout[numD6];

        blank20s = new ImageView[9];
        blank12s = new ImageView[9];
        blank10xs = new ImageView[9];
        blank8s = new ImageView[9];
        blank6s = new ImageView[9];
        blank4s = new ImageView[9];
        dice8 = new TextView[9];

        req = Volley.newRequestQueue(this);
        URL = "https://smooth-rattlesnake-56.localtunnel.me/";


        num.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public boolean onTouch(View arg0, MotionEvent arg1) {
                num.setCursorVisible(true);
                return false;
            }
        });

        String hold = "";
        int id;
        for (int i = 0; i < 9; i++) {

            hold = "s" + i + "d4";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank4s[i] = (ImageView) findViewById(id);
            blank4s[i].setOnClickListener(this);

            hold = "s" + i + "d6";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank6s[i] = (ImageView) findViewById(id);
            blank6s[i].setOnClickListener(this);

            hold = "s" + i + "d8";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank8s[i] = (ImageView) findViewById(id);
            blank8s[i].setOnClickListener(this);

            hold = "s" + i + "d10X";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank10xs[i] = (ImageView) findViewById(id);
            blank10xs[i].setOnClickListener(this);

            hold = "s" + i + "d12";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank12s[i] = (ImageView) findViewById(id);
            blank12s[i].setOnClickListener(this);

            hold = "s" + i + "d20";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            blank20s[i] = (ImageView) findViewById(id);
            blank20s[i].setOnClickListener(this);

            hold = "s" + i + "_d8_text";
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            dice8[i] = (TextView) findViewById(id);

            hold = "sideS" + i;
            id = this.getResources().getIdentifier(hold, "id", this.getPackageName());
            sides[i] = (RelativeLayout) findViewById(id);
            sides[i].setVisibility(View.INVISIBLE);
        }

        cli.setOnClickListener(this);
        newSess.setOnClickListener(this);
        joinSess.setOnClickListener(this);
        start.setOnClickListener(this);
        add.setOnClickListener(this);
        clear.setOnClickListener(this);
        remove.setOnClickListener(this);
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {
        spin1 = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("D4");
        list.add("D6");
        list.add("D8");
        list.add("D10");
        list.add("D12");
        list.add("D20");
        list.add("D100");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        spin1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_session:
                username = usr.getText().toString();
                rollIt.setVisibility(View.INVISIBLE);
                if (username.equals("")) {
                    Toast.makeText(this, "Invalid Username", Toast.LENGTH_LONG).show();
                } else {
                    usr.setHint(username + " (admin)");
                    usr.setText("");
                    usr.setFocusable(false);
                    usr.setClickable(false);
                    inviteCode.setVisibility(View.VISIBLE);
                    int code = new Random().nextInt(63535) + 2000;
                    String save = Integer.toHexString(code);
                    inviteCode.setText("The invite code is: " + save.toUpperCase());
                    inviteCode.setFocusable(false);
                    inviteCode.setClickable(false);
                    start.setVisibility(View.VISIBLE);
                    admin = true;
                }
                break;

            case R.id.end:
                break;

            case R.id.remove:
                removeIt = !removeIt;
                break;

            case R.id.s0d4:
            case R.id.s0d6:
            case R.id.s0d8:
            case R.id.s0d10X:
            case R.id.s0d12:
            case R.id.s0d20:
                if (removeIt) {
                    sides[0].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s1d4:
            case R.id.s1d6:
            case R.id.s1d8:
            case R.id.s1d10X:
            case R.id.s1d12:
            case R.id.s1d20:
                if (removeIt) {
                    sides[1].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s2d4:
            case R.id.s2d6:
            case R.id.s2d8:
            case R.id.s2d10X:
            case R.id.s2d12:
            case R.id.s2d20:
                if (removeIt) {
                    sides[2].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s3d4:
            case R.id.s3d6:
            case R.id.s3d8:
            case R.id.s3d10X:
            case R.id.s3d12:
            case R.id.s3d20:
                if (removeIt) {
                    sides[3].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s4d4:
            case R.id.s4d6:
            case R.id.s4d8:
            case R.id.s4d10X:
            case R.id.s4d12:
            case R.id.s4d20:
                if (removeIt) {
                    sides[4].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s5d4:
            case R.id.s5d6:
            case R.id.s5d8:
            case R.id.s5d10X:
            case R.id.s5d12:
            case R.id.s5d20:
                if (removeIt) {
                    sides[5].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s6d4:
            case R.id.s6d6:
            case R.id.s6d8:
            case R.id.s6d10X:
            case R.id.s6d12:
            case R.id.s6d20:
                if (removeIt) {
                    sides[6].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s7d4:
            case R.id.s7d6:
            case R.id.s7d8:
            case R.id.s7d10X:
            case R.id.s7d12:
            case R.id.s7d20:
                if (removeIt) {
                    sides[7].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.s8d4:
            case R.id.s8d6:
            case R.id.s8d8:
            case R.id.s8d10X:
            case R.id.s8d12:
            case R.id.s8d20:
                if (removeIt) {
                    sides[8].setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.start:
                invite = String.valueOf(inviteCode.getText());
                if (!admin) {
                    if (invite.equals("")) {
                        inviteCode.setText("");
                        Toast.makeText(this, "Please Enter An Invite Code", Toast.LENGTH_LONG).show();
                    } else if (invite.toUpperCase().equals("FFFF")) {
                        long in = Long.parseLong(invite, 16);
                        start.setVisibility(View.INVISIBLE);
                        diceOn();
                        addition = "D20_4~";

                        newSess.setVisibility(View.INVISIBLE);
                        joinSess.setVisibility(View.INVISIBLE);
                        usr.setVisibility(View.INVISIBLE);
                        inviteCode.setVisibility(View.INVISIBLE);

                        addedRoll();
                    } else {
                        Toast.makeText(this, "Incorrect Invite Code", Toast.LENGTH_LONG).show();
                    }
                } else {
                    inSession();
                    start.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.join_session:
                username = usr.getText().toString();

                if (username.equals("")) {
                    Toast.makeText(this, "Invalid Username", Toast.LENGTH_LONG).show();
                } else {
                    rollIt.setVisibility(View.INVISIBLE);
                    newSess.setVisibility(View.INVISIBLE);
                    joinSess.setVisibility(View.INVISIBLE);
                    start.setVisibility(View.VISIBLE);
                    usr.setVisibility(View.INVISIBLE);
                    inviteCode.setVisibility(View.VISIBLE);
                    admin = false;
                    usr.setHint(username);
                    usr.setText("");
                    usr.setFocusable(false);
                    usr.setClickable(false);
                    inviteCode.setVisibility(View.VISIBLE);
                    inviteCode.setText("");
                    inviteCode.setHint("Enter Invite Code Here ");
                }

                break;

            case R.id.add:
                String slot = num.getText().toString();
                if (slot.equals("") || slot.contains(".") || slot.contains("-") || slot.equals("0")) {
                    Toast.makeText(this, "Please Enter a Whole Number, > 0", Toast.LENGTH_LONG).show();
                } else {
                    int move = Integer.parseInt(slot);
                    if (numberOfDie + move > 9) {
                        Toast.makeText(this, "Please keep the stack to 9 or less dice.", Toast.LENGTH_LONG).show();
                    } else {
                        diceOff();
                        numberOfDie += move;
                        addition += String.valueOf(spin1.getSelectedItem()) + "_" + slot + "~";
                        Toast.makeText(this, "You have added " + slot + " " + String.valueOf(spin1.getSelectedItem()) + " to the stack.", Toast.LENGTH_LONG).show();
                        shown.setVisibility(View.VISIBLE);
                        stack += move + " " + String.valueOf(spin1.getSelectedItem()) + "\n";
                        shown.setText(stack + "There are " + numberOfDie + " dice total.");
                    }
                }
                num.setText("");
                break;

            case R.id.clc:
                addition = "";
                numberOfDie = 0;
                stack = "Current stack is:\n";
                diceOff();
                break;

            case R.id.roll:
                sound.start();
                num.setCursorVisible(false);
                removeIt = false;


                if (!addition.equals("")) {
                    addedRoll();
                    shown.setVisibility(View.INVISIBLE);
                } else {
                    String tvValue = num.getText().toString();
                    if (tvValue.equals("")) {
                        Toast.makeText(this, "Please Enter a Number", Toast.LENGTH_LONG).show();
                    } else {
                        if (String.valueOf(spin1.getSelectedItem()).equals("D4")) {
                            high = 4;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D6")) {
                            high = 6;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D8")) {
                            high = 8;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D10")) {
                            high = 10;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D12")) {
                            high = 12;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D20")) {
                            high = 20;
                        } else if (String.valueOf(spin1.getSelectedItem()).equals("D100")) {
                            high = 100;
                        }

                        List<String> numOfDie = new ArrayList<String>();

                        int num1 = Integer.parseInt(tvValue);

                        diceOff();

                        if (num1 <= numD6) {
                            roll(num1);

                        } else {
                            off();
                            shown.setVisibility(View.VISIBLE);
                            for (int i = 1; i <= num1; i++) {
                                roll = new Random().nextInt(high) + 1; //Random().nextInt((max - min) + 1)) + min;
                                numOfDie.add("Die Number " + i + " is a: " + roll + "\n");
                            }

                            String hold = numOfDie.toString();
                            hold = hold.replace("[", " ");
                            hold = hold.replace("]", "");
                            hold = hold.replace(",", "");
                            shown.setText(hold);
                        }
                    }
                }
                break;
        }
    }

    private void inSession() {
        newSess.setVisibility(View.INVISIBLE);
        joinSess.setVisibility(View.INVISIBLE);
        usr.setVisibility(View.INVISIBLE);

        rollIt.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);

        inviteCode.setVisibility(View.INVISIBLE);

    }

    private void off() {
        for (int i = 0; i < sides.length; i++) {
            sides[i].setVisibility(View.INVISIBLE);
        }
    }

    private void diceOff() {
        for (int i = 0; i < 9; i++) {
            dice8[i].setVisibility(View.INVISIBLE);

            blank4s[i].setVisibility(View.INVISIBLE);
            blank6s[i].setVisibility(View.INVISIBLE);
            blank8s[i].setVisibility(View.INVISIBLE);
            blank10xs[i].setVisibility(View.INVISIBLE);
            blank12s[i].setVisibility(View.INVISIBLE);
            blank20s[i].setVisibility(View.INVISIBLE);
        }
    }

    private void diceOn() {
        for (int i = 0; i < sides.length; i++) {
            sides[i].setVisibility(View.VISIBLE);
        }
    }


    private void addedRoll() {
        diceOff();
        String temp = addition + "z";
        String hold = "";
        int index = 0;
        int num = 0;
        int leftOff = 0;
        int rolledDie;

        while (index < temp.length() - 1) {
            while (temp.charAt(index) != '~') { //d4_4
                hold += temp.charAt(index);
                index++;
            }
            temp.substring(temp.indexOf("~") + 1);
            if (hold.contains("D4")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(4) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank4s[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 4)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    dice8[i].setText(String.valueOf(rolledDie));
                }
            } else if (hold.contains("D6")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(6) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank6s[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 6)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    dice8[i].setText(String.valueOf(rolledDie));
                }
            } else if (hold.contains("D8")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(8) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank8s[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 8)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    dice8[i].setText(String.valueOf(rolledDie));
                }
            } else if (hold.contains("D100")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(100) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank10xs[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 100)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    if (rolledDie < 10)
                        dice8[i].setText(" " + String.valueOf(rolledDie));
                    else
                        dice8[i].setText(String.valueOf(rolledDie));
                }
            } else if (hold.contains("D10")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(10) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank10xs[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 10)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    if (rolledDie < 10) {
                        String zero = " " + String.valueOf(rolledDie);
                        dice8[i].setText(zero);
                    } else
                        dice8[i].setText(String.valueOf(rolledDie));
                }
            } else if (hold.contains("D12")) {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(12) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank12s[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 12)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    if (rolledDie < 10) {
                        String zero = " " + String.valueOf(rolledDie);
                        dice8[i].setText(zero);
                    } else
                        dice8[i].setText(String.valueOf(rolledDie));
                }
            } else {
                hold = hold.substring(hold.indexOf("_") + 1);
                num = Integer.parseInt(hold);
                for (int i = leftOff; i < num + leftOff; i++) {
                    rolledDie = new Random().nextInt(20) + 1;
                    sides[i].setVisibility(View.VISIBLE);
                    dice8[i].setVisibility(View.VISIBLE);
                    blank20s[i].setVisibility(View.VISIBLE);
                    if (rolledDie == 20)
                        dice8[i].setTextColor(Color.YELLOW);
                    else
                        dice8[i].setTextColor(Color.DKGRAY);
                    if (rolledDie < 10) {
                        String zero = " " + String.valueOf(rolledDie);
                        dice8[i].setText(zero);
                    } else
                        dice8[i].setText(String.valueOf(rolledDie));
                }
            }
            leftOff += num;
            hold = "";
            index++;
        }
    }

    private void roll(int num1) {

        shown.setVisibility(View.INVISIBLE);
        sides[0].setVisibility(View.VISIBLE);

        for (int i = 0; i < num1; i++) {
            sides[i].setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < 8; i++) {
            if (high == 4)
                blank4s[i].setVisibility(View.INVISIBLE);
            else if (high == 6)
                blank6s[i].setVisibility(View.INVISIBLE);
            else if (high == 8)
                blank8s[i].setVisibility(View.INVISIBLE);
            else if (high == 10 || high == 100)
                blank10xs[i].setVisibility(View.INVISIBLE);
            else if (high == 12)
                blank12s[i].setVisibility(View.INVISIBLE);
            else
                blank20s[i].setVisibility(View.INVISIBLE);
            dice8[i].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < num1; i++) {
            roll = new Random().nextInt(high) + 1;
            if (roll < 10 && (high == 10 || high == 100 || high == 12 || high == 20)) {
                String zero = " " + String.valueOf(roll);
                dice8[i].setTextColor(Color.DKGRAY);
                dice8[i].setText(zero);
            } else if (roll == high) {
                dice8[i].setTextColor(Color.YELLOW);
                if (high == 100)
                    dice8[i].setText("00");
                else
                    dice8[i].setText(String.valueOf(roll));
            } else {
                dice8[i].setTextColor(Color.DKGRAY);
                dice8[i].setText(String.valueOf(roll));
            }
            dice8[i].setVisibility(View.VISIBLE);
            if (high == 10 || high == 100)
                blank10xs[i].setVisibility(View.VISIBLE);
            else if (high == 8)
                blank8s[i].setVisibility(View.VISIBLE);
            else if (high == 4)
                blank4s[i].setVisibility(View.VISIBLE);
            else if (high == 6)
                blank6s[i].setVisibility(View.VISIBLE);
            else if (high == 12)
                blank12s[i].setVisibility(View.VISIBLE);
            else
                blank20s[i].setVisibility(View.VISIBLE);
        }
    }
}
