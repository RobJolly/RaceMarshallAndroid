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
import uk.co.robertjolly.racemarshallandroid.data.DisplayFilterManager;
import uk.co.robertjolly.racemarshallandroid.data.TimesFilterManager;
import uk.co.robertjolly.racemarshallandroid.miscClasses.RaceMarshallBluetoothComponent;
import uk.co.robertjolly.racemarshallandroid.miscClasses.SaveAndLoadManager;
import uk.co.robertjolly.racemarshallandroid.miscClasses.Vibrate;
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
        raceMarshallBluetoothComponent.addObserver((observable, o) -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> { //this deals with the logic of what to tell the user after they've sent or received a checkpoint
            grabCheckpoints().notifyObservers();
            AlertDialog.Builder notifyBuilder = new AlertDialog.Builder(getContext());
            notifyDialog.dismiss();
            if (raceMarshallBluetoothComponent.isReading()) {
                raceMarshallBluetoothComponent.setReading(false);
                if (raceMarshallBluetoothComponent.isFailed() || raceMarshallBluetoothComponent.getReceivedCheckpoint() == null) {
                    notifyBuilder.setMessage("Failed to receive checkpoint");
                } else {
                    if (grabCheckpoints().hasCheckpoint(raceMarshallBluetoothComponent.getReceivedCheckpoint().getCheckPointNumber())) {
                        notifyBuilder.setMessage("You've received a checkpoint that you already have, Number: " + raceMarshallBluetoothComponent.getReceivedCheckpoint().getCheckPointNumber() + ". Do you want to override your version of the checkpoint?");
                        notifyBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        notifyBuilder.setNegativeButton("Override Checkpoint", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                grabCheckpoints().deleteCheckpoint(raceMarshallBluetoothComponent.getReceivedCheckpoint().getCheckPointNumber());
                                grabCheckpoints().addCheckpoint(raceMarshallBluetoothComponent.getReceivedCheckpoint());
                                grabCheckpoints().notifyObservers();
                            }
                        });
                    } else {
                        //Got the checkpoint fine, tell the user.
                        notifyBuilder.setMessage("Received Checkpoint " + raceMarshallBluetoothComponent.getReceivedCheckpoint().getCheckPointNumber());
                        grabCheckpoints().addCheckpoint(Objects.requireNonNull(raceMarshallBluetoothComponent.getReceivedCheckpoint()));
                        grabCheckpoints().notifyObservers();
                    }
                }
            } else if (raceMarshallBluetoothComponent.isWriting()) {
                raceMarshallBluetoothComponent.setWriting(false);
                if (raceMarshallBluetoothComponent.isFailed()) {
                    notifyBuilder.setMessage("Failed to send checkpoint");
                } else {
                    notifyBuilder.setMessage("Sent Checkpoint");
                }
            }
            notifyDialog = notifyBuilder.create();
            notifyDialog.show();
            new Vibrate().pulse(getContext());
        }));
    }

    /**
     * This is the setup function for config and service.
     * These two are from a library that i'm using to scan for devices via bluetooth.
     */
    private void setupBluetoothServiceAndConfig() {

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
                        createTransferAlertDialog();
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
        setAboutButton(view);
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
            //TODO Ideally, we wouldn't require the user to re-click after any of these. See if we can grab user responses to these dialogs. Maybe broadcast receivers?
            if (bluetoothAdapter == null) { //user doesn't have bluetooth adapter
                notifyBuilder = new AlertDialog.Builder(getContext());
                notifyBuilder.setMessage("Sorry, this feature requires bluetooth, which your phone doesn't appear to have.");
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            } else if (!bluetoothAdapter.isEnabled()){ //bluetooth isn't enabled, ask to re-enable.
                notifyBuilder = new AlertDialog.Builder(getContext());
                notifyBuilder.setMessage("To use this feature, bluetooth must be enabled. Click Enable to enable bluetooth, or Cancel to keep it disabled.");
                notifyBuilder.setPositiveButton("Enable", (dialogInterface, i) -> {
                    bluetoothAdapter.enable();
                    //receiveCheckpoint.performClick();
                });
                notifyBuilder.setNegativeButton("Cancel", null);
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            } else if (checkPermissions()){ //user hasn't enabled the permissions. Ask them to fix that.
                notifyBuilder.setMessage("Sorry, you've not activated the location permission. Without that, this can't work. If the popup to enable it didn't appear," +
                        " please go to Settings -> Apps -> RaceMarshallAndroid to manually re-enable it. Otherwise, try again.");
                notifyBuilder.show();
            } else { //user has everything enabled correctly. Start listening for a checkpoint.
                makeDiscoverable();
                raceMarshallBluetoothComponent.startListening();
                notifyBuilder = new AlertDialog.Builder(getContext());
                notifyBuilder.setMessage(getString(R.string.waiting_send));
                notifyBuilder.setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    //Will dismiss on its own, doesn't need help.
                });
                notifyBuilder.setOnDismissListener(dialogInterface -> {
                    raceMarshallBluetoothComponent.stopListening();
                    raceMarshallBluetoothComponent.stopConnection();
                });

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    notifyDialog = notifyBuilder.create();
                    notifyDialog.show();
                });
            }
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
            //TODO Ideally, we wouldn't require the user to re-click after any of these. See if we can grab user responses to these dialogs. Maybe broadcast receivers?
            if (bluetoothAdapter == null) { //user doesn't have bluetooth adapter
                notifyBuilder = new AlertDialog.Builder(getContext());
                notifyBuilder.setMessage(getString(R.string.no_bluetooth));
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            } else if (!bluetoothAdapter.isEnabled()){ //bluetooth isn't enabled, ask to re-enable.
                notifyBuilder = new AlertDialog.Builder(getContext());
                notifyBuilder.setMessage(getString(R.string.ask_bluetooth_enable));
                notifyBuilder.setPositiveButton(getString(R.string.enable), (dialogInterface, i) -> {
                    bluetoothAdapter.enable();
                    //receiveCheckpoint.performClick();
                });
                notifyBuilder.setNegativeButton(getString(R.string.cancel), null);
                notifyDialog = notifyBuilder.create();
                notifyDialog.show();
            } else if (checkPermissions()){ //user hasn't enabled the permissions. Ask them to fix that.
                notifyBuilder.setMessage(getString(R.string.location_permission_missing));
                notifyBuilder.show();
            } else { //user has everything enabled correctly. Start listening for a checkpoint.
                service.startScan();
                alertBuilder = new AlertDialog.Builder(getContext());
                createTransferAlertDialog();
            }
        });
    }

    /**
     * This sets the actions of the setTransferCheckpointButton.
     * Starts scanning for a device and allows the user to pick which device to transmit data to
     * (and which checkpoint to send).
     */
    private void createTransferAlertDialog() {
        //TODO Make this a recycle view
        /*
         *  As it's less complicated at this stage, i'm using a dialog. This is not ideal, as this dialog has to be
         *  un and re-made everytime we find a new device. This would be impractical in crowded environments and is rather
         *  irritating to the user.
         */
        String[] deviceNames = new String[deviceList.size()];
        int countDevices = 0;
        for (BluetoothDevice device : deviceList) {
            deviceNames[countDevices] = device.getName();
            countDevices++;
        }
        alertBuilder.setItems(deviceNames, (dialogInterface, i) -> {
            service.stopScan(); //stops re-freshing the list while user is selecting checkpoint.
            int selectedDevice = i;

            AlertDialog.Builder askCheckpoint = new AlertDialog.Builder(getContext()); //this asks the user for the checkpoint info.
            final ArrayList<Integer> possibilities = grabCheckpoints().getCheckpointNumberList();
            CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
            int countCheckpoints = 0;
            for (int item : possibilities) {
                checkpointNumberStrings[countCheckpoints] = String.valueOf(item);
                countCheckpoints++;
            }
            askCheckpoint.setTitle(getString(R.string.select_send_checkpoint));
            askCheckpoint.setItems(checkpointNumberStrings, new DialogInterface.OnClickListener() { //transfers checkpoint on selection of an item.
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    raceMarshallBluetoothComponent.startConnecting(deviceList.get(selectedDevice), uuidReceive, possibilities.get(i));
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        notifyBuilder.setMessage(getString(R.string.trying_get_checkpoint));
                        notifyBuilder.setPositiveButton(getString(R.string.cancel), (dialogInterface1, i1) -> {
                            raceMarshallBluetoothComponent.stopConnecting();
                            raceMarshallBluetoothComponent.stopConnection();
                        });
                        notifyDialog = notifyBuilder.create();
                        notifyDialog.show();
                    });
                }
            });
            askCheckpoint.create().show();
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
            String warningString = getString(R.string.delete_data_warning1) + " " + grabCheckpoints().getNumberUnpassedOrUnreported() + " " + getString(R.string.delete_data_warning2);
            warnUser.setTitle(getString(R.string.warning));
            warnUser.setCancelable(true);
            warnUser.setMessage(warningString);
            warnUser.setPositiveButton(R.string.cancel, null);
            warnUser.setNegativeButton(getString(R.string.delete_all), (dialogInterface, i) -> {
                grabSaveAndLoadManager().deleteDisplayFilterSave(); //deletes display filters file.
                grabDisplayFilters().setFilterList(grabDisplayFilters().implementRacerFilters(grabSaveAndLoadManager()));  //deletes display filters file.
                grabSaveAndLoadManager().deleteTimesFilterSave(); //deletes times filters file.
                grabTimesFilterManager().setFilterList(grabTimesFilterManager().implementRacerFilters(grabSaveAndLoadManager())); //deletes display filters file.
                grabCheckpoints().clearCheckpointData(); //clears checkpoints
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
            dialogBuilder.setTitle(getString(R.string.delete_checkpoint_dots));
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
                doubleCheckBuilder.setTitle(getString(R.string.are_you_sure));
                doubleCheckBuilder.setMessage(getString(R.string.delete_checkpoint_warning_1)+ checkpointNumberStrings[i] + getString(R.string.delete_checkpoint_warning2) + grabCheckpoints().getCheckpoint(possibilities.get(i)).getNumberUnreportedAndUnpassedRacers() + getString(R.string.delete_checkpoint_warning_3));
                doubleCheckBuilder.setPositiveButton(getString(R.string.cancel), null);
                final int selectedItem = i;
                doubleCheckBuilder.setNegativeButton(getString(R.string.confirm), (dialogInterface1, i1) -> {
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
            String toExport = new Gson().toJson(grabCheckpoints());
            sendIntent.putExtra(Intent.EXTRA_TEXT, toExport); //I need a to-String or JSON creator here
            sendIntent.setType("text/json");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    private void setAboutButton(View view) {
        Button aboutButton = view.findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aboutNoticeBuilder = new AlertDialog.Builder(getContext());
                View aboutView = getLayoutInflater().inflate(R.layout.about_page, null);
                aboutNoticeBuilder.setView(aboutView);
                aboutNoticeBuilder.create().show();
            }
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
            alertBuilder.setPositiveButton(getString(R.string.add_checkpoint), (dialogInterface, i) -> {
                //Purposely blank due to limitations that exist before the button is shown to user
                //This is set later
            });

            alertBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
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
                TextView checkpointNumberTextBox = addNewCheckpointView.findViewById(R.id.newCheckpointNumber);
                try {
                    int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                    //errors to the user if the checkpoint already exists
                    if (grabCheckpoints().hasCheckpoint(checkPointNumber)) {
                        final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                        errorMessage.setTitle(getString(R.string.error));
                        errorMessage.setCancelable(true);
                        errorMessage.setMessage(getString(R.string.checkpoint_exists));
                        errorMessage.setPositiveButton(getString(R.string.okay), null);
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
                    errorMessage.setTitle(getString(R.string.error));
                    errorMessage.setCancelable(true);
                    errorMessage.setMessage(getString(R.string.invalid_checkpoint_input));
                    errorMessage.setPositiveButton(R.string.okay, null);
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

    public DisplayFilterManager grabDisplayFilters() {
        //This is a bit unsafe, but easy way of doing it.
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabDisplayFilterManager();
    }

    public SaveAndLoadManager grabSaveAndLoadManager() {
        //This is a bit unsafe, but easy way of doing it.
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getSaveAndLoadManager();
    }

    public TimesFilterManager grabTimesFilterManager() {
        //This is a bit unsafe, but easy way of doing it.
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).grabTimesFilterManager();
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
    private boolean checkPermissions() {
        int permissionCheck = Objects.requireNonNull(getActivity()).checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 0);
            return false;
        }
        return true;
    }
}
