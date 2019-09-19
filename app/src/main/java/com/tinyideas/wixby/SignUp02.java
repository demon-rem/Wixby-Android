package com.tinyideas.wixby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SignUp02 extends AppCompatActivity {

    // An integer with a random value. Will be used to request the user for permissions during runtime.
    private static final int PERMISSION_REQUEST_CODE = 15;
    private static ImageView gpsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting a return result for the activity by default. Thus, if the user presses the back button
        // they will go back to the previous activity and not directly thrown out of the app.
        setResult(Activity.RESULT_OK);

        // Making the status bar of the application completely transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String firstName;
        String lastName;
        String password;
        String dateOfBirth;
        String gender;

        // Getting the view stub and inflating it with the required layout.
        ViewStub viewStub = findViewById(R.id.mainViewStub);
        viewStub.setLayoutResource(R.layout.card_view02);
        viewStub.inflate();

        // Trying to get the data that should be sent to this activity from the previous activity.
        // If this data extraction process fails, the user will be informed of the same using a SnackBar
        // and then the app will be closed.
        try {
            Bundle passedData = getIntent().getExtras();
            ArrayList<String> resultArray = passedData.getStringArrayList(getResources().getString(R.string.intentTag));

            // Getting the results one by one from the list. They are to be retrieved in the order
            // they were inserted in the list.
            assert resultArray != null;
            firstName = resultArray.get(0);
            lastName = resultArray.get(1);
            password = resultArray.get(2);
            dateOfBirth = resultArray.get(3);
            gender = resultArray.get(4);

            String text = String.format("FirstName:\t%s\nLastName:\t%s\nPassword:\t%s\nDateOfBirth:\t%s\nGender:\t%s", firstName, lastName, password, dateOfBirth, gender);
            Log.d("DEBUG", text);
        } catch (Exception e) {
            viewStub.setClickable(false);

            // Creating a SnackBar to inform the user of the crash. The SnackBar will also have a button
            // once the user clicks this button, the app will close.
            Snackbar.make(findViewById(R.id.activity_main_layout),
                    "Something went wrong. The app will have to shut down",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // In order to force close the application, using the `finish()` method
                            // to force close the current activity. Before doing that, using the
                            // `setResult()` method to set the result code as cancelled. Once this
                            // activity is closed, the previous activity will get the result in
                            // `startActivityForResult()`. On seeing this code, that activity too will
                            // force close itself. Thus, closing the entire application.
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }
                    }).show();
        }

        // Getting the country spinner from the XML
        final Spinner countrySpinner = findViewById(R.id.countrySpinner);

        // Getting the state spinner and the text view from the XML layout. Both of these are hidden
        // by default. Depending on the country selected by the user, one of the two will be displayed
        // and the other will remain hidden.
        final TextInputEditText stateTextView = findViewById(R.id.stateTextView);
        final Spinner stateSpinner = findViewById(R.id.stateSpinner);

        // Getting the container layout for the state textView and the state spinner. Instead of hiding
        // the individual, these container layouts will be hidden from the screen as required.
        final TextInputLayout stateParentView = findViewById(R.id.stateParentLayout);
        final RelativeLayout stateSpinnerLayout = findViewById(R.id.stateSpinnerLayout);

        // Getting the GPS icon, when the user clicks this icon, the position of the device will be
        // taken using GPS and will be filled into the location text field.
        gpsIcon = findViewById(R.id.gpsIcon);

        // Getting a list of all the country names and storing them in an ArrayList, this list will
        // then be used to populate the country spinner.
        String[] isoCountryCodes = Locale.getISOCountries();
        ArrayList<String> countries = new ArrayList<>();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            String countryName = locale.getDisplayName();
            countries.add(countryName);
        }

        // In order to sort the entries of the ArrayList in alphabetical order, using a custom
        // comparator to do the same.
        Collections.sort(countries, new Comparator<String>() {
            @Override
            public int compare(String data01, String data02) {
                return data01.compareTo(data02);
            }
        });

        // Once the array list containing all countries is prepared, using the same to populate the
        // spinner too. To do so, creating an ArrayAdapter that will populate data from this list and
        // then attaching the adapter to the spinner. `spinner_item` is a simple XML file containing
        // a single text view. This TextView will be used by the adapter to create the list for the spinner
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, countries);
        countryAdapter.setDropDownViewResource(R.layout.spinner_item);
        countrySpinner.setAdapter(countryAdapter);

        // Similarly, populating the state list array too. Starting by creating an ArrayList containing
        // a list of all the states in India. This list will be populated using a string-array that is
        // present in `strings.xml`
        ArrayList<String> states = new ArrayList<>();
        Collections.addAll(states, getResources().getStringArray(R.array.statesList));

        // Using the same method as above to populate data from the list into the spinner.
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                states);
        stateAdapter.setDropDownViewResource(R.layout.spinner_item);
        stateSpinner.setAdapter(stateAdapter);

        // Attaching an item-selected listener to the country spinner. Thus, when the user changes their
        // country this listener will check the new country selected by the user. If the country is India,
        // then the state spinner will be displayed to the user, if not, then the textEdit will be displayed.
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DEBUG", countrySpinner.getSelectedItem().toString());
                String selection = countrySpinner.getSelectedItem().toString();

                // If `selection` is 'India', will display the state spinner as it is populated with
                // just Indian states. If `selection` is something else, will display the state text view.
                if (selection.equalsIgnoreCase("India")) {
                    // Hiding the state text view
                    stateParentView.setVisibility(View.GONE);

                    // Displaying the spinner
                    stateSpinnerLayout.setVisibility(View.VISIBLE);
                } else {
                    // Displaying the text view
                    stateParentView.setVisibility(View.VISIBLE);

                    // Hiding the spinner.
                    stateSpinnerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // The flow-of-control will reach this part if nothing is selected by the user. This
                // will be something that's impossible to do, since the spinner will have a value selected
                // by default. However, in the off-hand chance that this changes in the future Android
                // versions, simply hiding the state spinner and text view from the display in here.
                stateSpinnerLayout.setVisibility(View.GONE);
                stateParentView.setVisibility(View.GONE);
            }
        });

        // Attaching a click listener to the GPS Icon ImageView. When the user clicks this icon, their
        // position is to be taken using the GPS and filled in the location text box.
        gpsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the user clicks on the auto-locate button, the app will fetch the location of
                // the device using the `LocationManager`. In order to use this service, the app needs
                // to have the GPS permission granted. If the app tries to use this service without \
                // the permission, it could lead to a crash. Thus, beginning with checking if the
                // permissions are granted or not.
                if (!checkForPermissions()) {
                    // If the permissions have not been granted, requesting the permissions at runtime.
                    requestPermissions();
                } else {
                    // If the app already has the required permissions, then directly trying to get
                    // the users location using the LocationManager.
                    Toast.makeText(SignUp02.this, "Yipeeeeee", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method will be used to check for the required permissions during runtime. In the older
     * versions of Android, users had to grant all the permission before starting the app. However,
     * as of now, the apps have to request users for permissions during runtime. Thus, this method will
     * be used to check if the app has the required permissions or not. If the app tries to the GPS
     * without the appropriate permissions, then the app will crash.
     *
     * @return A boolean indicating whether the app has the required permissions or not. Will be true
     * if the permissions are granted, false otherwise.
     */
    private boolean checkForPermissions() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * If the user has not granted the required permissions to the app. Then during runtime, the app
     * will request the user to granted the required permissions to the app. This method will be used
     * to request the user for the permissions.
     */
    private void requestPermissions() {

        // Forming a string array of the permissions that are required from the user.
        String[] permissionsRequired = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        // Directly requesting the user for the permissions, using the array created above.
        requestPermissions(permissionsRequired, PERMISSION_REQUEST_CODE);
    }

    /**
     * This method is valid only Android 6 and above. Whenever the application requests the user for
     * permission during runtime, the result of the permission will be returned to the application
     * inside this method. The result can either be ok, if the user granted the permission that was
     * required. Or otherwise, in case the user denied the permission.
     *
     * @param requestCode  An integer. Will be the request code that was used while requesting the permission.
     * @param permissions  A string array containing the permissions that were requested.
     * @param grantResults An integer array. Will contain the same number of entries as `permissions`.
     *                     For each permission in `permission` the corresponding index of this array
     *                     will be the result of that permission, i.e. if the user denied that particular
     *                     permission or accepted it.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking to make sure that the permissions being granted are the ones that the app requested
        // for during runtime.
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If the flow-of-control reaches this part, it means that the method is returning the
                // result for the GPS location permission. Checking if the user has granted the
                // permission or denied it. Since the request was made for two permissions, thus,
                // `grantResults` will have two entries, each indicating whether the corresponding
                // permission was granted by the user or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted. The user was asked for the permissions when they clicked 
                    // on the GPS icon. Thus, now that the permission has been granted, implicitly
                    // performing a click on the same icon.
                    gpsIcon.performClick();
                } else {
                    // Permissions denied.
                    Snackbar.make(findViewById(R.id.activity_main_layout),
                            "Y u do this to me?\n\t\t\t\tI cri\t\t ಥ_ಥ", Snackbar.LENGTH_LONG).show();
                }

                break;
        }
    }
}