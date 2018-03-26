package labstuff.gcu.me.org.scotlandroadtraffic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



/**
 * Created by Andre on 12/03/2018.
 */

public class CurrentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CurrentActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private String url1="http://trafficscotland.org/rss/feeds/currentincidents.aspx";

    private EditText findTitle;
    private Button returnButton, searchTitle, button1;
    ArrayList titleList = new ArrayList();
    private ArrayList<Incidents> incidentList = new ArrayList<Incidents>();
    private ArrayList<Incidents> searchList = new ArrayList<Incidents>();

    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }


    public class Incidents{
        private String title;
        private String description;
        private String urlLink;
        private String location;
        private String author;
        private String comments;
        private Date dateTime;
        private String longitude;
        private String latitude;
        private String datetime;


        //Constructor
        public Incidents(String title, String description, String urlLink, String location, String author, String comments, Date dateTime, String longitude, String latitude, String datetime) {
            this.title = title;
            this.description = description;
            this.urlLink = urlLink;
            this.location = location;
            this.author = author;
            this.comments = comments;
            this.dateTime = dateTime;
            this.latitude = latitude;
            this.longitude = longitude;
            this.datetime = datetime;
        }
        //Getters
        public String getTitle() {return title;}
        public String getDescription() {return description;}
        public String getUrlLink() {return urlLink;}
        public String getLocation() {return location;}
        public String getLatitude() {return latitude;}
        public String getLongitude() {return longitude;}
        public Date getDateTime() {return dateTime;}
        public String getDatetime() {return datetime;}


        @Override
        public String toString()
        {
            return "Incidents [title=" + title + ", description=" + description + ", urlLink=" + urlLink + ", location=" + location + ", author=" + author + ", comments=" + comments + ", dateTime=" + dateTime + "]";
        }
    }


    //Used to setup auto refresh
    Handler handler = new Handler();
    Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            System.out.println("First Run");
            startProgress();
            System.out.println("Second Run");
            handler.postDelayed(timedTask, 120000);
            System.out.println("Third Run");
        }};
    //END OF AUTO REFRESH


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //set the top bar
        getSupportActionBar().setTitle("Current Incidents");


        if(isServicesOK()){
            System.out.println("Google Play Services Working");
        }

        //Parse the XML file
        startProgress();
        handler.post(timedTask);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);
        findTitle = (EditText)findViewById(R.id.findTitle);

        button1 = (Button)findViewById(R.id.button1);
        button1.setVisibility(View.GONE);

        //SEARCH FUNCTION FOR TITLE
        searchTitle = (Button)findViewById(R.id.searchTitle);
        searchTitle.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.v("EditText", findTitle.getText().toString());
                        String searchTitle = findTitle.getText().toString();
                        for (Incidents item : incidentList) {
                            if (item.getTitle().contains(searchTitle)) {
                                System.out.println(item.getTitle());
                                //NEED MORE CODE HERE TO MAKE IT WORK
                                String title = item.getTitle();
                                String description = item.getDescription();
                                String urlLink = item.getUrlLink();
                                String location = item.getLocation();
                                String author = null;
                                String comments = null;
                                String datetime= item.getDatetime();
                                Date dateTime = item.getDateTime();

                                String longitude = item.getLongitude();
                                String latitude = item.getLatitude();
                                searchList.add(new Incidents(title, description, urlLink, location, author, comments, dateTime, longitude, latitude, datetime));


                            }
                            else{
                                System.out.println("DIDN'T WORK AT ON CLICK SEARCH");
                            }
                            startSearchList(searchList);
                        }
                    }
                });


        //create our new array adapter
        ArrayAdapter<Incidents> adapter = new propertyArrayAdapter(this, 0, incidentList);

        //Find list view and bind it with the custom adapter
        ListView listView = (ListView) findViewById(R.id.customListView);
        listView.setAdapter(adapter);


        //add event listener so we can handle click events
        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {

            //on click
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Incidents incident = incidentList.get(position);

                Intent intent = new Intent(CurrentActivity.this, DetailActivity.class);
                intent.putExtra("title", incident.getTitle());
                intent.putExtra("description", incident.getDescription());
                intent.putExtra("urlLink", incident.getUrlLink());
                intent.putExtra("location", incident.getLocation());
                intent.putExtra("longitude", incident.getLongitude());
                intent.putExtra("latitude", incident.getLatitude());
                intent.putExtra("start", incident.getDatetime());

                startActivity(intent);
            }
        };
        //set the listener to the list view
        listView.setOnItemClickListener(adapterViewListener);
    } // End of onCreate


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnButton:
                Intent returnB = new Intent(this, MainActivity.class);
                startActivity(returnB);
                break;

        }
    }


    //custom ArrayAdapater
    class propertyArrayAdapter extends ArrayAdapter<Incidents> {

        private Context context;
        private List<Incidents> IncidentList;

        //constructor, call on creation
        public propertyArrayAdapter(Context context, int resource, ArrayList<Incidents> objects) {
            super(context, resource, objects);
            this.context = context;
            this.IncidentList = objects;
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            Incidents incident = IncidentList.get(position);
            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //conditionally inflate either standard or special template
            View view;
            view = inflater.inflate(R.layout.property_layout, null);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView dateTime = (TextView) view.findViewById(R.id.dateTime);

            //set title and datetime
            title.setText(incident.getTitle());
            dateTime.setText(incident.getDatetime().toString());

            return view;
        }
    }


    public void startSearchList(final ArrayList searchList){
        //create our new array adapter
        ArrayAdapter<Incidents> adapter = new propertyArrayAdapter(this, 0, searchList);

        //Find list view and bind it with the custom adapter
        ListView listView = (ListView) findViewById(R.id.customListView);
        listView.setAdapter(adapter);


        //add event listener so we can handle click events
        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {

            //on click
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Incidents incident = (Incidents) searchList.get(position);

                Intent intent = new Intent(CurrentActivity.this, DetailActivity.class);
                intent.putExtra("title", incident.getTitle());
                intent.putExtra("description", incident.getDescription());
                intent.putExtra("urlLink", incident.getUrlLink());
                intent.putExtra("location", incident.getLocation());
                intent.putExtra("longitude", incident.getLongitude());
                intent.putExtra("latitude", incident.getLatitude());
                intent.putExtra("start", incident.getDatetime());

                startActivity(intent);
            }
        };
        //set the listener to the list view
        listView.setOnItemClickListener(adapterViewListener);
    }

    public void startProgress()
    {
        //Run network access on a separate thread;
        new Thread(new CurrentActivity.Task(url1)).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {
            Log.e("MyTag","in run");

            try {
                URL url = new URL("https://trafficscotland.org/rss/feeds/currentincidents.aspx");
                URLConnection conn = url.openConnection();

                //Get Document Builder
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                //Build Document using input stream
                Document document = builder.parse(conn.getInputStream());

                //Normalize the XML Structure; It's just too important !!
                document.getDocumentElement().normalize();

                //Here comes the root node
                Element root = document.getDocumentElement();
                System.out.println("THIS IS ROOT NODE" + root.getNodeName());

                //Get all items within the XML file
                NodeList nList = document.getElementsByTagName("item");
                System.out.println("============================");

                for (int temp = 0; temp < nList.getLength(); temp++)
                {
                    Node node = nList.item(temp);

                    System.out.println("");    //Just a separator

                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        //Print each employee's detail
                        Element eElement = (Element) node;

                        System.out.println("TEST TITLE : "  + eElement.getElementsByTagName("title").item(0).getTextContent());

                        titleList.add(eElement.getElementsByTagName("title").item(0).getTextContent());


                        String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                        String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                        String urlLink = eElement.getElementsByTagName("link").item(0).getTextContent();
                        String location = eElement.getElementsByTagName("georss:point").item(0).getTextContent();
                        String author = eElement.getElementsByTagName("author").item(0).getTextContent();
                        String comments = eElement.getElementsByTagName("comments").item(0).getTextContent();
                        String datetime= eElement.getElementsByTagName("pubDate").item(0).getTextContent().toString();

                        String longitude = location.substring(0, location.indexOf(" "));
                        String latitude = location.substring(location.lastIndexOf(" ")+1);

                        DateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                        Date dateTime = format.parse(datetime);
                        System.out.println(dateTime);
                        incidentList.add(new Incidents(title, description, urlLink, location, author, comments, dateTime, longitude, latitude, datetime));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CurrentActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CurrentActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}


