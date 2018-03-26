package labstuff.gcu.me.org.scotlandroadtraffic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button currentButton, plannedButton;


    List titleList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentButton = (Button)findViewById(R.id.currentButton);
        currentButton.setOnClickListener(this);

        plannedButton = (Button)findViewById(R.id.plannedButton);
        plannedButton.setOnClickListener(this);

        currentButton.setOnClickListener(this);
        plannedButton.setOnClickListener(this);

    } // End of onCreate

    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.currentButton:
                Intent callCurrent = new Intent(this, CurrentActivity.class);
                startActivity(callCurrent);
                break;


            case R.id.plannedButton:
                Intent callPlanned = new Intent(this, PlannedActivity.class);
                startActivity(callPlanned);
                break;
        }
    }



}
