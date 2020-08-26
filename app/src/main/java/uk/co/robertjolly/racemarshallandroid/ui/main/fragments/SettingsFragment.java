package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

//From https://github.com/douglasjunior/AndroidBluetoothLibrary. MIT License.
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

//From https://github.com/google/gson. Apache 2.0 license.
import com.google.gson.Gson;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.miscClasses.RaceMarshallBluetoothComponent;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.MainTabsSectionsPagerAdapter;

/**
 * This is the fragment concerned with 'settings' and Misc. Activities.
 * Such as: Transfer of checkpoints, creation of new checkpoints, deletion of all data and the
 * exporting of data.
 */
public class SettingsFragment extends Fragment implements CheckpointGrabber {
    private BluetoothConfiguration config = new BluetoothConfiguration();
    private BluetoothService service;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private AlertDialog deviceDialog;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog notifyDialog;
    private RaceMarshallBluetoothComponent raceMarshallBluetoothComponent;
    private UUID uuidReceive = UUID.fromString("baeed2bb-a07c-486c-a57b-ad6d1c4d5de3");
    private AlertDialog.Builder notifyBuilder;

    /**
     * This is the function that is called on the creation of the Fragment to initialise the fragment.
     * @param savedInstanceState The saved data from the previous instance of this fragment, if any has been saved.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceList.addAll(bluetoothAdapter.getBondedDevices());

        //this is the bit that handles the bluetooth functionality of the device.
        raceMarshallBluetoothComponent = new RaceMarshallBluetoothComponent(grabCheckpoints());
        checkPermissions();
        setupBluetoothServiceAndConfig();

        notifyBuilder = new AlertDialog.Builder(getContext());
        notifyDialog = notifyBuilder.create();

        //this is run when a new checkpoint has been added or a checkpoint has been transmitted.
        raceMarshallBluetoothComponent.addObserver((observable, o) -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            grabCheckpoints().notifyObservers();
            AlertDialog.Builder notifyBuilder = new AlertDialog.Builder(getContext());
            notifyDialog.dismiss();
            if (raceMarshallBluetoothComponent.isReading()) {
                raceMarshallBluetoothComponent.setReading(false);
                notifyBuilder.setMessage("Received Checkpoint");

            } else if (raceMarshallBluetoothComponent.isWriting()) {
                raceMarshallBluetoothComponent.setWriting(false);
                notifyBuilder.setMessage("Sent Checkpoint");
            }
            notifyDialog = notifyBuilder.create();
            notifyDialog.show();
        }));
    }

    /**
     * This is the setup function for config and service.
     * These two are from a library that i'm using to scan for devices via bluetooth.
     */
    private void setupBluetoothServiceAndConfig() {
        //TODO add code ensuring that there is bluetooth here.

        /* Configure bluetooth library from: https://github.com/douglasjunior/AndroidBluetoothLibrary
         * I'm using this as an easy way to find bluetooth devices. Because doing it manually is a pain.
         * /It saves a lot of work. The code outside of onDeviceDiscovered is not my own, but provided in
         the library github project.
         */
        config.context = getContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "Your App Name";
        config.callListenersInMainThread = true;
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required
        BluetoothService.init(config);
        service = BluetoothService.getDefaultInstance();
        service.setOnScanCallback(new BluetoothService.OnBluetoothScanCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
                if (!deviceList.contains(device)) {
                    deviceList.add(device);
                    if (deviceDialog != null) {
                        deviceDialog.dismiss();
                        createAlertDialog();
                    }
                }

            }

            /**
             * Required by the library.
             * Ran on the start of the scan.
             */
            @Override
            public void onStartScan() {
            }

            /**
             * Required by the library.
             * Ran on the start of the end of the scan.
             */
            @Override
            public void onStopScan() {
            }
        });

        /*
         * This is default behaviour.
         */
        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] buffer, int length) {
                Log.e("READ", "READING DATA");
            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
                if (status == BluetoothStatus.CONNECTED) {
                    BluetoothWriter writer = new BluetoothWriter(service);
                    writer.write("test");
                }
            }

            @Override
            public void onDeviceName(String deviceName) {
            }

            @Override
            public void onToast(String message) {
            }

            @Override
            public void onDataWrite(byte[] buffer) {
            }
        });
    }

    /**
     * This is ran before it is shown to the user. This concerns itself with setting actions of
     * various buttons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkpoints_fragment,container,false);

        setNewCheckpointButton(view);
        setExportJSONButton(view);
        setDeleteAllButton(view);
        setDeleteCheckpointButton(view);
        setTransferCheckpointButton(view);
        setReceiveCheckpointButton(view);
        return view;
    }

    /**
     * This sets the actions for the receive checkpoint button.
     * This makes it wait to be contacted by another device, to receive a checkpoint.
     * @param view The view in which R.id.receiveCheckpointButton exists.
     */
    private void setReceiveCheckpointButton(View view) {
        final Button receiveCheckpoint = view.findViewById(R.id.receiveCheckpointButton);
        receiveCheckpoint.setOnClickListener(view1 -> {
            makeDiscoverable();
            raceMarshallBluetoothComponent.startListening();
            notifyBuilder.setMessage("Waiting for checkpoint to be sent..");
            notifyBuilder.setPositiveButton("Cancel", (dialogInterface, i) -> {
                raceMarshallBluetoothComponent.stopListening();
                raceMarshallBluetoothComponent.setReading(false);
            });

            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            });
        });
    }

    /**
     * This sets the actions of the setTransferCheckpointButton.
     * Starts scanning for a device and allows the user to pick which device to transmit data to
     * (and which checkpoint to send)
     * @param view The view in which R.id.transferCheckpointButton exists.
     */
    private void setTransferCheckpointButton(View view) {
        final Button transferCheckpoint = view.findViewById(R.id.transferCheckpointButton);

        transferCheckpoint.setOnClickListener(view12 -> {
            service.startScan();
            alertBuilder = new AlertDialog.Builder(getContext());
            createAlertDialog();
        });
    }

    /**
     * This sets the actions of the setTransferCheckpointButton.
     * Starts scanning for a device and allows the user to pick which device to transmit data to
     * (and which checkpoint to send).
     */
    private void createAlertDialog() {
        //TODO Make this a recycle view
        /*
         *  As it's less complicated at this stage, i'm using a dialog. This is not ideal, as this dialog has to be
         *  un and re-made everytime we find a new device. This would be impractical in crowded enviroments and is rather
         *  irritating to the user.
         */

        String[] deviceNames = new String[deviceList.size()];
        int count = 0;
        for (BluetoothDevice device : deviceList) {
            deviceNames[count] = device.getName();
            count++;
        }
        alertBuilder.setItems(deviceNames, (dialogInterface, i) -> {
            raceMarshallBluetoothComponent.startConnecting(deviceList.get(i), uuidReceive);
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                notifyBuilder.setMessage("Trying to send checkpoint. If this takes more than a few seconds, it has likely failed, and should be cancelled.");
                notifyBuilder.setPositiveButton("Cancel", (dialogInterface1, i1) -> {
                    raceMarshallBluetoothComponent.stopConnecting();
                    raceMarshallBluetoothComponent.stopConnection();
                });
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            });

        });
        deviceDialog = alertBuilder.create();
        deviceDialog.show();
    }

    /**
     * This sets the actions of the deleteAllButton.
     * The purpose of this button is to delete all checkpoints upon user request.
     * @param view The view in which R.id.deleteAllCheckpoints is contained.
     */
    private void setDeleteAllButton(View view) {
        final Button deleteAll = view.findViewById(R.id.deleteAllCheckpoints);
        deleteAll.setOnClickListener(view1 -> {
            //This warning is to make sure that the user intended to delete everything.
            final AlertDialog.Builder warnUser = new AlertDialog.Builder(getContext());
            String warningString = "This will delete all data. There are currently " + grabCheckpoints().getNumberUnpassedOrUnreported() + " unreported or unpassed racers.";
            warnUser.setTitle("Warning:");
            warnUser.setCancelable(true);
            warnUser.setMessage(warningString);
            warnUser.setPositiveButton("Cancel", null);
            warnUser.setNegativeButton("Delete All", (dialogInterface, i) -> {
                grabCheckpoints().clearCheckpointData();
                grabCheckpoints().notifyObservers();
            });
            AlertDialog warnUserDialog = warnUser.create();
            warnUserDialog.show();
        });
    }

    /**
     * This sets the actions of the deleteCheckpointButton.
     * The purpose of this button is to delete a single checkpoints upon user request.
     * @param view The view in which R.id.deleteCheckpointButton is contained.
     */
    private void setDeleteCheckpointButton(View view) {
        final Button deleteCheckpoint = view.findViewById(R.id.deleteCheckpointButton);
        deleteCheckpoint.setOnClickListener(view1 -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
            dialogBuilder.setTitle("Delete Checkpoint:");
            dialogBuilder.setCancelable(true);
            //gets a list of checkpoints
            final ArrayList<Integer> possibilities = grabCheckpoints().getCheckpointNumberList();
            final CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
            int count = 0;
            for (int item : possibilities) {
                checkpointNumberStrings[count] = String.valueOf(item);
                count++;
            }

            //adds another dialog to ensure that the user intended to delete the checkpoint.
            dialogBuilder.setItems(checkpointNumberStrings, (dialogInterface, i) -> {
                final AlertDialog.Builder doubleCheckBuilder = new AlertDialog.Builder(getActivity());
                doubleCheckBuilder.setTitle("Are you sure?");
                doubleCheckBuilder.setMessage("You are about to delete checkpoint " + checkpointNumberStrings[i] + ". This cannot be reversed. There are " + grabCheckpoints().getCheckpoint(possibilities.get(i)).getNumberUnreportedAndUnpassedRacers() + " Unreported or Unpassed racers in this checkpoint");
                doubleCheckBuilder.setPositiveButton("Cancel", null);
                final int selectedItem = i;
                doubleCheckBuilder.setNegativeButton("Confirm", (dialogInterface1, i1) -> {
                    grabCheckpoints().deleteCheckpoint(possibilities.get(selectedItem));
                    grabCheckpoints().notifyObservers();
                });
                doubleCheckBuilder.setCancelable(true);
                doubleCheckBuilder.create().show();
            });

            dialogBuilder.create().show();
        });
    }

    /**
     * This sets the actions of the export to json button. The purpose of this button is to allow
     * the user to share all data stored in the app via the android share feature, in JSON format.
     * @param view The view which contains R.id.exportJsonButton
     */
    private void setExportJSONButton(View view) {
        Button exportJsonButton = view.findViewById(R.id.exportJsonButton);
        exportJsonButton.setOnClickListener(view1 -> {
            //This is code to create an android share event - In this case, the json values of all of the checkpoints are sent.
            //This means that storing all data, through email or whatever other method the user wishes to use, is possible.
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String test = new Gson().toJson(grabCheckpoints());
            sendIntent.putExtra(Intent.EXTRA_TEXT, test); //I need a to-String or JSON creator here
            sendIntent.setType("text/json");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    /**
     * This sets the actions of the set new checkpoint button. The purpose of this button is to allow
     * the user to create a new checkpoint
     * @param view
     */
    private void setNewCheckpointButton(View view) {
        Button checkpointButton = view.findViewById(R.id.createCheckpointButton);
        checkpointButton.setOnClickListener(view12 -> {
            //TODO Add logic to ensure the keyboard is closed upon closure of this dialog.
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            final View addNewCheckpointView = getLayoutInflater().inflate(R.layout.create_new_checkpoint, null);
            alertBuilder.setView(addNewCheckpointView);
            alertBuilder.setPositiveButton("Add Checkpoint", (dialogInterface, i) -> {
                //Purposely blank due to limitations that exist before the button is shown to user
                //This is set later
            });

            alertBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                //cancel doesn't need to do anything.
            });


            final AlertDialog toShow = alertBuilder.create();
            toShow.show();
            toShow.findViewById(R.id.newCheckpointNumber).requestFocus();

            //commented out for now due to causing errors.
            /*
            //Code to bring up keyboard, and dismiss it on dialog close
            InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            toShow.setOnDismissListener(dialogInterface -> {
                InputMethodManager inputMethodManager1 = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager1.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            });*/

            //creates the actions for the addNewCheckpoint button dialog.
            Button createButtonAction = toShow.getButton(DialogInterface.BUTTON_POSITIVE);
            createButtonAction.setOnClickListener(view1 -> {
                //TODO add error checking here
                TextView checkpointNumberTextBox = addNewCheckpointView.findViewById(R.id.newCheckpointNumber);

                try {
                    int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                    //errors to the user if the checkpoint already exists
                    if (grabCheckpoints().hasCheckpoint(checkPointNumber)) {
                        final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                        errorMessage.setTitle("Error");
                        errorMessage.setCancelable(true);
                        errorMessage.setMessage("That checkpoint already exists and cannot be created.");
                        errorMessage.setPositiveButton("Okay", null);
                        errorMessage.create().show();
                    } else { //adds the checkpoint and notifies observers
                        Checkpoint createdPoint = new Checkpoint(checkPointNumber, grabCheckpoints().getCheckpoint(grabCheckpoints().getCurrentCheckpointNumber()).getRacers().size());
                        grabCheckpoints().addCheckpoint(createdPoint);
                        grabCheckpoints().notifyObservers();
                        toShow.dismiss();
                    }
                } catch (Exception e) {
                    //error if the user has input invalid options for the checkpoint
                    final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                    errorMessage.setTitle("Error");
                    errorMessage.setCancelable(true);
                    errorMessage.setMessage("Invalid Checkpoint Input. The checkpoint must be a whole integer.");
                    errorMessage.setPositiveButton("Okay", null);
                    errorMessage.create().show();
                }
            });
        });
    }

    /**
     * This function grabs the checkpoints from the MainTabsSectionsPagerAdapter, within the activity
     * @return The grabbed checkpoints
     */
    @Override
    public Checkpoints grabCheckpoints() {
        //This is a bit unsafe, but easy way of doing it.
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getCheckpoints();

    }

    /**
     * This makes the phone request being discoverable via bluetooth for 120 seconds.
     */
    private void makeDiscoverable() {
        Intent makeDiscoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeDiscoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivity(makeDiscoverableIntent);
    }

    /**
     * This checks to make sure that bluetooth permissions are adequate for what's needed and
     * requests them if they're not.
     */
    private void checkPermissions() {
        int permissionCheck = Objects.requireNonNull(getActivity()).checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 0);
        }
    }
}
