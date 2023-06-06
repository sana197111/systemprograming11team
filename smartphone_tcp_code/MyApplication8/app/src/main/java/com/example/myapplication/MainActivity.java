package com.example.myapplication;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;


// 자이로//
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//끝//


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    private static final int SERVER_PORT = 5000;
    private EditText ipEditText;
    private Button connectButton;
    private Button disconnectButton;
    private TextView receivedTextView;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    private ExecutorService executorService;

    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private TextView textView;
    private static boolean touched = true;
    private static boolean gyroed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.ipEditText);
        connectButton = findViewById(R.id.connectButton);
        disconnectButton = findViewById(R.id.disconnectButton);
        receivedTextView = findViewById(R.id.receivedTextView);
        executorService = Executors.newFixedThreadPool(10);
        textView = findViewById(R.id.text_view);

        // SensorManager 및 자이로 센서 가져오기
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectFromServer();
            }
        });
    }
    private void mainCont() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                if (touched && gyroed) {
                    sendInBackground("1");
                }
                touched = true;
                gyroed = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });
    }
    private void sendInBackground(final String message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (output != null) {
                    try {
                        output.println(message);
                        output.flush();
                        System.out.println("Message sent: " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void connectToServer() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverIp = ipEditText.getText().toString(); // EditText에서 입력된 IP 주소 가져오기
                    socket = new Socket(serverIp, SERVER_PORT);
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 연결에 성공한 경우, 서버로부터 데이터를 수신하기 위한 쓰레드를 시작합니다.
                    startReceivingData();
                    System.out.println("Message s1ent: " );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void disconnectFromServer() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startReceivingData() {
        try {
            String receivedMessage;
            while ((receivedMessage = input.readLine()) != null) {
                System.out.println("Mess3age sent: ");
                displayReceivedMessage(receivedMessage);
                System.out.println("Message se2nt: ");
                sendInBackground(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayReceivedMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receivedTextView.setText(message);
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touched=false; // Send "1" when a touch is detected
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 센서 등록
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 센서 해제
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (values[0] > 0.5f || values[0] < -0.5f ||
                values[1] > 0.5f || values[1] < -0.5f ||
                values[2] > 0.5f || values[2] < -0.5f) {
            textView.setText("True");
        } else {
            textView.setText("False");
            gyroed=false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 사용하지 않음
    }

}
