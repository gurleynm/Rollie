package com.mathprob.android.rollie;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Spinner spin1;
    private Button cli;
    private TextView num;
    private  TextView shown;
    private int roll;
    private int high;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addItemsOnSpinner2();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
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
        spin1 = (Spinner) findViewById(R.id.spinner);
        spin1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spin1 = (Spinner) findViewById(R.id.spinner);
        cli = (Button) findViewById(R.id.button);
        num = (TextView) findViewById(R.id.number);
        shown = (TextView) findViewById(R.id.display);
        cli.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

                List<String>  numOfDie = new ArrayList<String>();
                String tvValue = num.getText().toString();

                int num1 = Integer.parseInt(tvValue);

                for(int i = 1; i <= num1; i++) {
                    roll = new Random().nextInt(high) + 1; //Random().nextInt((max - min) + 1)) + min;
                    numOfDie.add("Die Number " + i + " is a: " + roll + "\n");
                }

                String hold = numOfDie.toString();
                hold = hold.replace("["," ");
                hold = hold.replace("]","");
                hold = hold.replace(",","");
                shown.setText(hold);
            }

        });
    }

}

