package uk.co.robertjolly.racemarshallandroid.miscClasses;

//Open-source android libraries: https://source.android.com/. Apache 2.0.
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

//General/default java libraries: https://docs.oracle.com/javase/7/docs/api/index.html
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;

//Projects own classes.
import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;

/**
 * This class is responsible for managing bluetooth connections between devices, and the sending of data between them
 * Parts of this class have been constructed with help of a youtube tutorial, found here: https://www.youtube.com/watch?v=Fz_GT7VGGaQ
 */
public class RaceMarshallBluetoothComponent extends Observable {
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private listeningThread listeningThread;
    private connectionCreatorThread connectionCreatorThread;
    private BluetoothDevice bluetoothDevice;
    private UUID deviceUUID;
    private UUID uuidReceive = UUID.fromString("baeed2bb-a07c-486c-a57b-ad6d1c4d5de3");
    private ConnectedThread connectedThread;
    private Checkpoints checkpoints;
    private boolean writing = false;
    private boolean reading = false;

    /**
     * This is the constructor for the raceMarshallBluetoothComponent. It is set up with checkpoints to modify or send from.
     * @param checkpoints checkpoints that are wanted to be sent from, or modified.
     */
    public RaceMarshallBluetoothComponent(Checkpoints checkpoints) {
        this.checkpoints = checkpoints;
    }


    /**
     * This is a thread that will be responsible for receiving data.
     */
    private class listeningThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        /**
         * Constructor for a listening thread
         */
        public listeningThread() {
            BluetoothServerSocket tmp = null;
            reading = true;
            try {
                String appName = "RaceMarshallAndroid";
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, uuidReceive);
                Log.e("ACThread", "Created Accepting thread successfully");
            } catch (Exception e) {
                Log.e("ACThread", "Failed to create accepting thread");
            }

            this.bluetoothServerSocket = tmp;
        }

        /**
         * This is started upon thread creation.
         * This listens for attempted connections, and accepts if an attempt is made.
         */
        public void run() {
            BluetoothSocket bluetoothSocket = null;

            try {
                bluetoothSocket = bluetoothServerSocket.accept(); //Thread waits for a bluetooth connection
                Log.e("ACThread", "Received Bluetooth connection");
            } catch (Exception e) {
                Log.e("ACThread", "Bluetooth connection failed");
                //TODO Error here, bluetooth connection failed
            }

            if (bluetoothSocket != null) { //Bluetooth socket has now connected - can do stuff
                Log.e("ACThread", "Attempting to create connector");
                connected(bluetoothSocket);
            }
        }

        /**
         * This closes the socket, and cancels listening for connections.
         */
        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (Exception e) {
                Log.e("Error", "Failed to close bluetooth socket for listening");
            }
        }
    }

    /**
     * This is the thread responsible for connecting to the other device
     */
    private class connectionCreatorThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        public connectionCreatorThread(BluetoothDevice sentDevice, UUID sentUuid) {
            bluetoothDevice = sentDevice;
            deviceUUID = sentUuid;
            writing = true;
            Log.i("Connection Thread", sentDevice.getName());
        }

        /**
         * This is the thread responsible for establishing a connection ot another device
         */
        public void run() {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (Exception e) {
                Log.e("Error", "failed to connect to the other device");
            }

            bluetoothAdapter.cancelDiscovery(); //For speed reasons - connecting, don't need to discover

            try {
                bluetoothSocket.connect(); //This waits for a successful connection or fail.
                Log.i("Connection Thread", "connected to the other device.");
            } catch (Exception e){
                try {
                    Log.e("Error", "Failed to connect to the bluetooth socket");
                    bluetoothSocket.close();
                } catch (Exception e2) {
                   Log.e("Error", "Failed to close the bluetooth socket after a failed connection");
                }
            }

            connected(bluetoothSocket);
        }

        /**
         * This cancels attempting to connect to the other device.
         */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
               Log.e("Error", "Failed to close the bluetooth socket while connecting to another device");
            }
        }
    }

    /**
     * Starts the device listening for a connection
     */
    public synchronized void startListening() {
        stopConnecting();
        listeningThread = new listeningThread();
        listeningThread.start();
        Log.i("Start Listening", "Now Listening");
    }

    /**
     * Stops the device listening for a connection
     */
    public synchronized void stopListening() {
        if (listeningThread != null) {
            listeningThread.cancel();
            Log.i("Stop Listening", "Now Stopped Listening");
        }
    }

    /**
     * Stops the device connecting to another
     */
    public synchronized void stopConnecting() {
        if (connectionCreatorThread != null) {
            connectionCreatorThread.cancel();
            Log.i("Stop Connecting", "Now Stopped Connecting");
        }
    }

    /**
     * Stops the device connecting to another
     */
    public synchronized void stopConnection() {
        if (connectedThread != null) {
            connectedThread.cancel();
            Log.i("Stop Connection", "Now Stopped Connection");
        }
    }

    /**
     * Starts the device connecting to another
     * @param bluetoothDevice Bluetooth device to connect
     * @param uuid UUID to connect
     */
    public synchronized void startConnecting(BluetoothDevice bluetoothDevice, UUID uuid) {
        stopListening();

        if (connectionCreatorThread != null) {
            connectionCreatorThread.cancel();
        }
        connectionCreatorThread = new connectionCreatorThread(bluetoothDevice, uuid);
        connectionCreatorThread.start();
        Log.i("Start Connecting", "Now Started Connection");
    }

    /**
     * This is the thread that will run when both have been connected
     */
    //TODO consider having different threads for both reading and writing
    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final ObjectOutputStream objectOutputStream;
        private final ObjectInputStream objectInputStream;

        /**
         * Constructor for the connection thread (This handles the connected devices
         * @param bluetoothSocket Bluetooth socket of the connection
         */
        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = bluetoothSocket.getInputStream();
                tmpOutputStream = bluetoothSocket.getOutputStream();

            } catch (Exception e) {
                Log.e("Error", "Failed to create input or output streams");
            }
            InputStream inputStream = tmpInputStream;
            OutputStream outputStream = tmpOutputStream;

            ObjectInputStream tmpObjectInputStream = null;
            ObjectOutputStream tmpObjectOutputStream = null;

            if (writing) {
                try {
                    //Be careful with these, this could loop forever
                    tmpObjectOutputStream = new ObjectOutputStream(outputStream);
                } catch (IOException e) {
                    Log.e("Error", "Could not create ObjectOutputStream");
                }
            } else {
                while (true) {
                    try {
                        tmpObjectInputStream = new ObjectInputStream(inputStream);
                        break;
                    } catch (IOException e) {
                        Log.e("Error", "Could not create ObjectInputStream");
                    }
                }

            }

            objectOutputStream = tmpObjectOutputStream;
            objectInputStream = tmpObjectInputStream;
            Log.i("Connected Thread", "Initialised Connected Thread");
        }

        /**
         * This run function is started on the creation of a thread.
         * This shall run until it has successfully written, or read a checkpoint.
         */
        public void run() {
            if (writing) {
                boolean sucsess = false;
                while (!sucsess) {
                    sucsess = send(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()));
                }
                Log.i("Connected Thread", "Written to connected thread");
            } else {
                while (true) {
                    try {
                        //TODO Add checkpoint checking here
                        Checkpoint checkpoint = (Checkpoint) objectInputStream.readObject();
                        checkpoints.addCheckpoint(checkpoint);
                        break;
                    } catch (Exception e) {
                        //Log.e("Error", "Failed to create a checkpoint from the input stream data");
                    }
                }
                Log.i("Connected Thread", "Read Connected Thread Successfully");
            }

            try {
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                Log.e("Error", "Could not notify all observers of a change in component");
            }
                cancel();
        }

        /**
         * Stops the connection
         */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                Log.e("Error", "Failed to close bluetooth socket");
            }
        }

        /**
         * This writes the given checkpoint to the connected device
         * @param checkpoint the checkpoint for which to send data
         * @return Whether or not writing the object has been successful
         */
        public boolean send(Checkpoint checkpoint) {
            try {
                Log.i("Send", "Writing Checkpoint");
                objectOutputStream.writeObject(checkpoint);
                Log.i("Send", "Written Checkpoint");
                return true;
            } catch (Exception e) {
                Log.e("Error", "Failed writing checkpoint");
                return false;
            }
        }
    }

    /**
     * Handles the connection
     * @param bluetoothSocket the bluetooth socket of the connected device
     */
    private void connected(BluetoothSocket bluetoothSocket) {
        connectedThread = new ConnectedThread(bluetoothSocket);
        try {
            connectedThread.start();
            Log.i("Connected", "Started connected thread");
        } catch (Exception e) {
            Log.e("Error", " Failed to connect thread");
        }
    }

    /**
     * Sends the given checkpoint to the connected device
     * @param checkpoint The checkpoint tot send to the connected device
     * @return whether or not the write has been successful
     */
    public boolean send(Checkpoint checkpoint) {
        try {
            return connectedThread.send(checkpoint);
        } catch (Exception e) {
            Log.e("Error", "Failed to Write to device");
            return false;
        }
    }

    /**
     * Sets whether or not the Bluetooth Component is in a writing state
     * @param writing whether or not the Bluetooth Component is in a writing state
     */
    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    /**
     * Sets whether or not the Bluetooth Component is in a reading state
     * @param reading whether or not the Bluetooth Component is in a writing state
     */
    public void setReading(boolean reading) {
        this.reading = reading;
    }

    /**
     * getter for whether or not the Bluetooth Component is in a writing state
     * @return whether or not the Bluetooth Component is in a writing state
     */
    public boolean isWriting() {
        return writing;
    }

    /**
     * getter for whether or not the Bluetooth Component is in a reading state
     * @return whether or not the Bluetooth Component is in a reading state
     */
    public boolean isReading() {
        return reading;
    }
}
