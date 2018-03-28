package labstuff.gcu.me.org.scotlandroadtraffic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.DatePicker;
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

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Andre Queen on 12/03/2018.
 * Matric Number ; S1635221
 */
public class PlannedActivity extends AppCompatActivity implements View.OnClickListener {
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

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

    private static final String TAG = "CurrentActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private String url1="http://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    private EditText findTitle, findDate;
    private Button returnButton, searchTitle, searchDate;
    ArrayList titleList = new ArrayList();
    private ArrayList<Incidents> incidentList = new ArrayList<Incidents>();
    private ArrayList<Incidents> searchList = new ArrayList<Incidents>();



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
        private Date startTime;
        private Date endTime;
        private String start;
        private String end;
        private Integer length;

        //Constructor
        public Incidents(String title, String description, String urlLink, String location, String author, String comments, Date dateTime, String longitude, String latitude, String datetime, Date startTime, Date endTime, String start, String end, Integer length) {
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
            this.startTime = startTime;
            this.endTime = endTime;
            this.start = start;
            this.end = end;
            this.length = length;
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
        public Date getStartTime() {return startTime;}
        public Date getEndTime() {return endTime;}
        public String getStart() {return start;}
        public String getEnd() {return end;}
        public int getLength() {return length;}


        @Override
        public String toString()
        {
            return "Incidents [title=" + title + ", description=" + description + ", urlLink=" + urlLink + ", location=" + location + ", author=" + author + ", comments=" + comments + ", dateTime=" + dateTime + "]";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);


        getSupportActionBar().setTitle("Planned Roadworks (S1635221)");

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
                                String title = item.getTitle();
                                String description = item.getDescription();
                                String urlLink = item.getUrlLink();
                                String location = item.getLocation();
                                String author = null;
                                String comments = null;
                                String datetime= item.getDatetime();
                                Date dateTime = item.getDateTime();
                                Date startTime = item.getStartTime();
                                Date endTime = item.getEndTime();
                                String start = item.getStart();
                                String end = item.getEnd();
                                String longitude = item.getLongitude();
                                String latitude = item.getLatitude();
                                Integer length = item.getLength();
                                searchList.add(new Incidents(title, description, urlLink, location, author, comments, dateTime, longitude, latitude, datetime, startTime, endTime, start, end, length));
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

                Intent intent = new Intent(PlannedActivity.this, DetailActivity.class);
                intent.putExtra("title", incident.getTitle());
                intent.putExtra("description", incident.getDescription());
                intent.putExtra("urlLink", incident.getUrlLink());
                intent.putExtra("location", incident.getLocation());
                intent.putExtra("longitude", incident.getLongitude());
                intent.putExtra("latitude", incident.getLatitude());
                intent.putExtra("datetime", incident.getDatetime());
                intent.putExtra("start", incident.getStart());
                intent.putExtra("end", incident.getEnd());

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
            String title1 = incident.getTitle();

            if (incident.getLength() == 1 || incident.getLength() == 2 || incident.getLength() == 3){
                title.setBackgroundColor(Color.GREEN);
            } else if (incident.getLength() == 4 || incident.getLength() == 5){
                title.setBackgroundColor(Color.BLUE);
            } else if (incident.getLength() > 5){
                title.setBackgroundColor(Color.RED);
            }
            title.setText(title1);
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

                Intent intent = new Intent(PlannedActivity.this, DetailActivity.class);
                intent.putExtra("title", incident.getTitle());
                intent.putExtra("description", incident.getDescription());
                intent.putExtra("urlLink", incident.getUrlLink());
                intent.putExtra("location", incident.getLocation());
                intent.putExtra("longitude", incident.getLongitude());
                intent.putExtra("latitude", incident.getLatitude());
                intent.putExtra("datetime", incident.getDatetime());
                intent.putExtra("start", incident.getStart());
                intent.putExtra("end", incident.getEnd());

                startActivity(intent);
            }
        };
        //set the listener to the list view
        listView.setOnItemClickListener(adapterViewListener);
    }

    public void startProgress()
    {
        //Run network access on a separate thread;
        new Thread(new PlannedActivity.Task(url1)).start();
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
                URL url = new URL("http://trafficscotland.org/rss/feeds/plannedroadworks.aspx");
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
                        String desc = eElement.getElementsByTagName("description").item(0).getTextContent();
                        String urlLink = eElement.getElementsByTagName("link").item(0).getTextContent();
                        String location = eElement.getElementsByTagName("georss:point").item(0).getTextContent();
                        String author = eElement.getElementsByTagName("author").item(0).getTextContent();
                        String comments = eElement.getElementsByTagName("comments").item(0).getTextContent();
                        String datetime= eElement.getElementsByTagName("pubDate").item(0).getTextContent().toString();

                        String start = desc.substring(desc.indexOf(":") +1, desc.indexOf("<"));
                        String first = desc.substring(desc.indexOf(":", desc.indexOf(":", desc.indexOf(":")+1)+1));
                        String end = first.substring(first.indexOf(":")+1, first.indexOf("<"));

                        String description = first.substring(first.indexOf(">")+1);

                        String longitude = location.substring(0, location.indexOf(" "));
                        String latitude = location.substring(location.lastIndexOf(" ")+1);

                        DateFormat formatv2 = new SimpleDateFormat(" EE, dd MMM yyyy - HH:mm", Locale.ENGLISH);
                        Date startTime = formatv2.parse(start);
                        Date endTime = formatv2.parse(end);

                        //Calculate the difference between start and end dates in days
                        Long dateLength = endTime.getTime() - startTime.getTime();
                        long diffDays = dateLength / (24 * 60 * 60 * 1000);
                        Integer length = (int)diffDays;

                        System.out.println("LENGTH OF DIFFERENCE " + diffDays);


                        DateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                        Date dateTime = format.parse(datetime);


                        System.out.println("THIS IS datetime "+datetime);

                        incidentList.add(new Incidents(title, description, urlLink, location, author, comments, dateTime, longitude, latitude, datetime, startTime, endTime, start, end, length));
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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PlannedActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PlannedActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }





    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);

                }
            };

    private void showDate(int year, int month, int day) {
        int year1 = year;
        int month1 = month;
        int day1 = day;

        String sDate1 = ""+ day1 +" 0"+month1+" "+year1;
        try {
            Date date1=new SimpleDateFormat("dd MM yyyy").parse(sDate1);
            DateFormat date2=new SimpleDateFormat("E, dd MMM yyyy");
            String date3 = date2.format(date1);
            System.out.println("TEST FORMAT DATE3 " + date3);

            for (Incidents item : incidentList) {
                if (item.getDatetime().contains(date3)) {
                    System.out.println(item.getTitle());
                    String title = item.getTitle();
                    String description = item.getDescription();
                    String urlLink = item.getUrlLink();
                    String location = item.getLocation();
                    String author = null;
                    String comments = null;
                    String datetime= item.getDatetime();
                    Date dateTime = item.getDateTime();
                    Date startTime = item.getStartTime();
                    Date endTime = item.getEndTime();
                    String start = item.getStart();
                    String end = item.getEnd();
                    String longitude = item.getLongitude();
                    String latitude = item.getLatitude();
                    Integer length = item.getLength();
                    searchList.add(new Incidents(title, description, urlLink, location, author, comments, dateTime, longitude, latitude, datetime, startTime, endTime, start, end, length));
                }
                else{
                    System.out.println("DIDN'T WORK AT ON CLICK SEARCH");
                }
                startSearchList(searchList);
            }
        } catch (ParseException e) {
            System.out.println("SHOW DATE DIDNT WORK");
        }


    }


}


