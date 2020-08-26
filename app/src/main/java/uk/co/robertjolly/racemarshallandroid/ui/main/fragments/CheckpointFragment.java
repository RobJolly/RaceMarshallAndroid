package uk.co.robertjolly.racemarshallandroid.ui.main.fragments;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.R;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.miscClasses.RaceMarshallBluetoothComponent;
import uk.co.robertjolly.racemarshallandroid.ui.main.CheckpointGrabber;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.MainTabsSectionsPagerAdapter;

//TODO Java doc this
public class CheckpointFragment extends Fragment implements CheckpointGrabber {
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
    //TODO Java doc this
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceList.addAll(bluetoothAdapter.getBondedDevices());


        raceMarshallBluetoothComponent = new RaceMarshallBluetoothComponent(grabCheckpoints());
        checkPermissions();

        //Configure bluetooth library from: https://github.com/douglasjunior/AndroidBluetoothLibrary
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
                Log.e("FOUND DEVICE: ", device.getName());
                if (!deviceList.contains(device)) {
                    deviceList.add(device);
                    if (deviceDialog != null) {
                        deviceDialog.dismiss();
                        createAlertDialog();
                    }
                }

            }

            @Override
            public void onStartScan() {
            }

            @Override
            public void onStopScan() {
            }
        });

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

        notifyBuilder = new AlertDialog.Builder(getContext());
        notifyDialog = notifyBuilder.create();

        raceMarshallBluetoothComponent.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
            }
        });


    }

    //TODO Java doc this
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkpoints_fragment,container,false);

        setNewCheckpointButton(view);
        //TODO Fix bug whereby racer numbers are not stored in the JSON file.
        setExportJSONButton(view);
        setDeleteAllButton(view);
        setDeleteCheckpointButton(view);

        final Button transferCheckpoint = view.findViewById(R.id.transferCheckpointButton);

        transferCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.startScan();
                alertBuilder = new AlertDialog.Builder(getContext());
                createAlertDialog();
            }
        });

        final Button receiveCheckpoint = view.findViewById(R.id.receiveCheckpointButton);
        receiveCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDiscoverable();
                raceMarshallBluetoothComponent.startListening();
                notifyBuilder.setMessage("Waiting for checkpoint to be sent..");
                notifyBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       raceMarshallBluetoothComponent.stopListening();
                       raceMarshallBluetoothComponent.setReading(false);
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDialog = notifyBuilder.create();
                        notifyDialog.show();
                    }
                });
            }
        });
        return view;
    }

    private void createAlertDialog() {
        String[] deviceNames = new String[deviceList.size()];
        int count = 0;
        for (BluetoothDevice device : deviceList) {
            deviceNames[count] = device.getName();
            count++;
        }
        alertBuilder.setItems(deviceNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                raceMarshallBluetoothComponent.startConnecting(deviceList.get(i), uuidReceive);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyBuilder.setMessage("Trying to send checkpoint");
                        notifyBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                raceMarshallBluetoothComponent.stopConnecting();
                                raceMarshallBluetoothComponent.stopConnection();
                            }
                        });
                        notifyDialog = notifyBuilder.create();
                        notifyDialog.show();
                    }
                });
                //deviceList.get(i)
            }
        });
        deviceDialog = alertBuilder.create();
        deviceDialog.show();
    }

    private void setDeleteAllButton(View view) {
        final Button deleteAll = view.findViewById(R.id.deleteAllCheckpoints);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder warnUser = new AlertDialog.Builder(getContext());
                String warningString = "This will delete all data. There are currently " + String.valueOf(grabCheckpoints().getNumberUnpassedOrUnreported()) + " unreported or unpassed racers.";
                warnUser.setTitle("Warning:");
                warnUser.setCancelable(true);
                warnUser.setMessage(warningString);
                warnUser.setPositiveButton("Cancel", null);
                warnUser.setNegativeButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        grabCheckpoints().clearCheckpointData();
                        grabCheckpoints().notifyObservers();
                    }
                });
                AlertDialog warnUserDialog = warnUser.create();
                warnUserDialog.show();

            }
        });
    }

    private void setDeleteCheckpointButton(View view) {
        final Button deleteCheckpoint = view.findViewById(R.id.deleteCheckpointButton);
        deleteCheckpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
                dialogBuilder.setTitle("Delete Checkpoint:");
                dialogBuilder.setCancelable(true);
                final ArrayList<Integer> possibilities = grabCheckpoints().getCheckpointNumberList();
                final CharSequence[] checkpointNumberStrings = new CharSequence[possibilities.size()];
                int count = 0;
                for (int item : possibilities) {
                    checkpointNumberStrings[count] = (CharSequence) String.valueOf(item);
                    count++;
                }

                dialogBuilder.setItems(checkpointNumberStrings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder doubleCheckBuilder = new AlertDialog.Builder(getActivity());
                        doubleCheckBuilder.setTitle("Are you sure?");
                        doubleCheckBuilder.setMessage("You are about to delete checkpoint " + checkpointNumberStrings[i] + ". This cannot be reversed. There are " + String.valueOf(grabCheckpoints().getCheckpoint(possibilities.get(i)).getNumberUnreportedAndUnpassedRacers()) + " Unreported or Unpassed racers in this checkpoint");
                        doubleCheckBuilder.setPositiveButton("Cancel", null);
                        final int selectedItem = i;
                        doubleCheckBuilder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                grabCheckpoints().deleteCheckpoint(possibilities.get(selectedItem));
                                grabCheckpoints().notifyObservers();
                            }
                        });
                        doubleCheckBuilder.setCancelable(true);
                        doubleCheckBuilder.create().show();
                    }
                });

                dialogBuilder.create().show();
            }
        });
    }

    private void setExportJSONButton(View view) {
        Button exportJsonButton = view.findViewById(R.id.exportJsonButton);
        exportJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This is code to create an android share event - In this case, the json values of all of the checkpoints are sent.
                //This means that storing all data, through email or whatever other method the user wishes to use, is possible.
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String test = new Gson().toJson(grabCheckpoints());
                sendIntent.putExtra(Intent.EXTRA_TEXT, test); //I need a to-String or JSON creator here
                sendIntent.setType("text/json");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });
    }

    private void setNewCheckpointButton(View view) {
        Button checkpointButton = view.findViewById(R.id.createCheckpointButton);
        checkpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                final View addNewCheckpointView = getLayoutInflater().inflate(R.layout.create_new_checkpoint, null);
                alertBuilder.setView(addNewCheckpointView);
                alertBuilder.setPositiveButton("Add Checkpoint", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                final AlertDialog toShow = alertBuilder.create();
                toShow.show();
                toShow.findViewById(R.id.newCheckpointNumber).requestFocus();

                //Code to bring up keyboard, and dismiss it on dialog close
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                toShow.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                Button createButtonAction = toShow.getButton(DialogInterface.BUTTON_POSITIVE);
                createButtonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO add error checking here
                        TextView checkpointNumberTextBox = addNewCheckpointView.findViewById(R.id.newCheckpointNumber);

                        try {
                            int checkPointNumber = Integer.parseInt(checkpointNumberTextBox.getText().toString());
                            //I need to do some error checking hereCheckpoint
                            if (grabCheckpoints().hasCheckpoint(checkPointNumber)) {
                                final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                                errorMessage.setTitle("Error");
                                errorMessage.setCancelable(true);
                                errorMessage.setMessage("That checkpoint already exists and cannot be created.");
                                errorMessage.setPositiveButton("Okay", null);
                                errorMessage.create().show();
                            } else {
                                Checkpoint createdPoint = new Checkpoint(checkPointNumber,grabCheckpoints().getCheckpoint(grabCheckpoints().getCurrentCheckpointNumber()).getRacers().size());
                                grabCheckpoints().addCheckpoint(createdPoint);
                                grabCheckpoints().notifyObservers();
                                toShow.dismiss();
                            }
                        } catch (Exception e) {
                            final AlertDialog.Builder errorMessage = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
                            errorMessage.setTitle("Error");
                            errorMessage.setCancelable(true);
                            errorMessage.setMessage("Invalid Checkpoint Input. The checkpoint must be a whole integer.");
                            errorMessage.setPositiveButton("Okay", null);
                            errorMessage.create().show();
                        }


                    }
                });
            }
        });
    }

    //TODO Java doc this
    @Override
    public Checkpoints grabCheckpoints() {
        return ((MainTabsSectionsPagerAdapter) Objects.requireNonNull(((ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.mainViewPager)).getAdapter())).getCheckpoints();

    }

    private void makeDiscoverable() {
        Intent makeDiscoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeDiscoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivity(makeDiscoverableIntent);
    }


    private ArrayList<BluetoothDevice> searchBluetoothDevices() {
        return null;
    }

    /**
     * This checks to make sure that bluetooth permissions are adequate for what's needed
     */
    private void checkPermissions() {
        int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 0);
        }

    }

}
