package uk.co.robertjolly.racemarshallandroid.miscClasses;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;

import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;

public class RaceMarshallBluetoothComponent extends Observable {
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Activity mainActivity;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private BluetoothDevice bluetoothDevice;
    private UUID deviceUUID;
    private String appName = "RaceMarshallAndroid";
    private UUID uuidReceive = UUID.fromString("10879b40-da59-4450-b507-f0a2b26a229c");
    private ConnectedThread connectedThread;
    private Checkpoints checkpoints;
    private boolean writing = false;
    private boolean reading = false;

    public RaceMarshallBluetoothComponent(Activity activity, Checkpoints checkpoints) {
        this.mainActivity = activity;
        this.checkpoints = checkpoints;
    }


    /**
     * This is a thread that will be responsible for receiving data.
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            reading = true;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, uuidReceive);
                Log.e("ACThread", "Created Accepting thread sucsessfully");
            } catch (Exception e) {
                Log.e("ACThread", "Failed to create accepting thread");
            }

            this.bluetoothServerSocket = tmp;
        }

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
                connected(bluetoothSocket, bluetoothDevice);
            }
        }

        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (Exception e) {
                //TODO Error here - failed to close server socket
            }
        }
    }

    /**
     * This is the thread responsible for sending the data
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        public ConnectThread(BluetoothDevice sentDevice, UUID sentUuid) {
            bluetoothDevice = sentDevice;
            deviceUUID = sentUuid;
            writing = true;
            Log.e("Connected Thread: ", sentDevice.getName());
        }

        public void run() {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (Exception e) {
                //TODO Error here
            }

            bluetoothAdapter.cancelDiscovery(); //For speed reasons - connecting, don't need to discover

            try {
                bluetoothSocket.connect(); //This waits for a successful connection or fail.
            } catch (Exception e){
                try {
                    //TODO Connection failed, Error here
                    bluetoothSocket.close();;
                } catch (Exception e2) {
                    //TODO Close socket failed, Error here

                }
            }

            connected(bluetoothSocket, bluetoothDevice);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();;
            } catch (Exception e) {
                //TODO Error here, failed to close socket.
            }
        }
    }

    /**
     * Starts the device listening for a connection
     */
    public synchronized void startListening() {
        stopConnecting();
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        } else {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    /**
     * Stops the device listening for a connection
     */
    public synchronized void stopListening() {
        if (acceptThread != null) {
            acceptThread.cancel();
        }
    }

    /**
     * Stops the device connecting to another
     */
    public synchronized void stopConnecting() {
        if (connectThread != null) {
            connectThread.cancel();
        }
    }

    /**
     * Stops the device connecting to another
     */
    public synchronized void stopConnection() {
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }


    /**
     * Starts the device connecting to another
     * @param bluetoothDevice Bluetooth device to connect
     * @param uuid UUID to connect
     */
    public synchronized void startConnecting(BluetoothDevice bluetoothDevice, UUID uuid) {
        stopListening();

        if (connectThread == null) {
            connectThread = new ConnectThread(bluetoothDevice, uuid);
            connectThread.start();
        } else {
            connectThread.cancel();
            connectThread = new ConnectThread(bluetoothDevice, uuid);
            connectThread.start();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final ObjectOutputStream objectOutputStream;
        private final ObjectInputStream objectInputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            Log.e("CNT", "Attempting to create connected thread");
            this.bluetoothSocket = bluetoothSocket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = bluetoothSocket.getInputStream();
                tmpOutputStream = bluetoothSocket.getOutputStream();

                //tmpInputStream = new ObjectInputStream(new BufferedInputStream(bluetoothSocket.getInputStream()));
                //tmpOutputStream = new ObjectOutputStream(new BufferedOutputStream(bluetoothSocket.getOutputStream()));
            } catch (Exception e) {
                Log.e("CNT", "Failed in/out streams");

                //TODO Error here - failed to create streams
            }
            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;

            ObjectInputStream tmpObjectInputStream = null;
            ObjectOutputStream tmpObjectOutputStream = null;

            if (writing) {
                try {
                    tmpObjectOutputStream = new ObjectOutputStream(outputStream);
                } catch (IOException e) {
                    tmpObjectOutputStream = null;
                }
                tmpObjectInputStream = null;
            } else {
                tmpObjectOutputStream = null;
                try {
                    tmpObjectInputStream = new ObjectInputStream(inputStream);
                } catch (IOException e) {
                    tmpObjectInputStream = null;
                }
            }

            objectOutputStream = tmpObjectOutputStream;
            objectInputStream = tmpObjectInputStream;

            Log.e("CNT", "Created Connected Thread Sucsessfully");
        }

        public void run() {
            Log.e("RUN", "Running connected thread. Writing: " + writing);
            /*if (writing) {
                this.write(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()));
            }*/
            //this.write(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()));

                if (writing) {
                    boolean sucsess = false;
                    while (!sucsess) {
                        sucsess = write(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()));
                    }
                    Log.e("Sucsess!", "WRITE");
                    //this.write(checkpoints.getCheckpoint(checkpoints.getCurrentCheckpointNumber()));
                } else {
                    while (true) {
                        try {
                            //TODO Add checkpoint checking here
                            Checkpoint checkpoint = (Checkpoint) objectInputStream.readObject();
                            checkpoints.addCheckpoint(checkpoint);
                            Log.e("CHK", "FOUND!!!!!");
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Cannot log this, happens too often
                        }
                    }
                }

                try {
                    setChanged();
                    notifyObservers();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            cancel();
        }

        /**
         * Cancels the connection
         */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                //TODO Error here, failed to close connection
            }
            //writing = false;

        }

        public boolean write(Checkpoint checkpoint) {
            try {
                objectOutputStream.writeObject(checkpoint);
                return true;
            } catch (Exception e) {
                Log.e("FAILED WRITE", "Failed");
                return false;
                //TODO Error here, failed to write object
            }
        }

        
    }

    /**
     * Handles the connection
     * @param bluetoothSocket
     * @param bluetoothDevice
     */
    private void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {
        connectedThread = new ConnectedThread(bluetoothSocket);
        Log.e("CNT", "Created connection Successfully");

        try {
            Log.e("CNT", "Trying to start connected thread");
            connectedThread.start();
        } catch (Exception e) {
            try {
                Log.e("Failed connect thread: ", bluetoothDevice.getName());
            } catch (Exception e1) {

            }
        }
    }


    public boolean write(Checkpoint checkpoint) {
        try {
            return connectedThread.write(checkpoint);
            //return true;
        } catch (Exception e) {
            return false;
            //Log.e("Failed Write", "Failed write");
            //TODO Error here, failed to write to connected thread
        }
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }

    public boolean isWriting() {
        return writing;
    }

    public boolean isReading() {
        return reading;
    }
}
