package com.cookandroid.mom.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.mom.R;
import com.cookandroid.mom.util.User.ExerciseItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Exercise extends Activity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    public TextView step;
    public TextView timer;
    private ArrayList<String> array = new ArrayList<>();
    private int stepCount = 0;
    private int steps=0;
    private int cal=0;
    private int dis =0;
    private int mainTime =0;
    private String strTime;
    boolean Lock = true; //락을 걸어서 먼저 수행하고 값을 고정하기위하여 사용
    long firststartTime, startTime, EndTime;

    Context context;




    ExerciseItem result = null;

    DateFormat dateFormat = DateFormat.getDateInstance();
    DateFormat timeFormat = DateFormat.getTimeInstance();

    Calendar ex_today;
    public void setmApiClient(GoogleApiClient mApiClient){
        this.mApiClient = mApiClient;
    }
    public void init() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }
    Handler mHandler = new Handler( ){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //초시간을 잰다
            int div = msg.what;

            int hour = mainTime / 3600;
            int min = mainTime / 60;
            int sec = mainTime % 60;

            strTime = String.format("%02d:%02d:%02d",hour,min,sec);

            this.sendEmptyMessageDelayed(0, 1000);
            timer.setText(strTime);
            timer.invalidate();
            mainTime++;
        }
    };

    public void healthStop(int btncheck){
        step.setText(String.valueOf(0));
        mHandler.removeMessages(0);
        if (btncheck==1) { //시작상태면
            //마지막시간 저장
            EndTime = System.currentTimeMillis();
            Log.e("마지막시간",""+dateFormat.format(EndTime) + " " + timeFormat.format(EndTime));
            //헬스종료 테스크 실행(실행후 UI반영)
            new HealthFinishTask().execute();
            //구독취소(저장작업 해제)
            cancelSubscription();
            //센싱해제

            //상태저장 및 초기화
            stepCount = 0;
            array.clear();
            startTime = firststartTime;
            Log.e("첫 운동시간 ",""+dateFormat.format(startTime) + " " + timeFormat.format(startTime));

        }else{ //시작상태가 아니면 (일시정지상태)
            //구독취소,센싱정지 초기화
            cancelSubscription();

            array.clear();
            startTime = firststartTime;
            Log.e("첫 운동시간 ",""+dateFormat.format(startTime) + " " + timeFormat.format(startTime));
            // ??
            makeResult();
            popResult(getResult());
            healthreset();

            stepCount = 0;
        }
        unregisterFitnessDataListener();
    }


    public void healthStart(){
        ex_today = Calendar.getInstance();

        mHandler.sendEmptyMessage(0);
        if(!mApiClient.isConnected()){
            mApiClient.connect();
        }else{
            mApiClient.reconnect();}
        if(Lock) {//최초 운동시작 시간을 고정하기 위하여 clockLock을 걸어 값이 변경되는걸 막는다.
            firststartTime = System.currentTimeMillis();
            Toast.makeText(this, ""+firststartTime, Toast.LENGTH_SHORT).show();
            Log.e("첫시간", "" + dateFormat.format(firststartTime) + " " + timeFormat.format(firststartTime));
            startTime = firststartTime;
            Log.e("시작시간", "" + dateFormat.format(startTime) + " " + timeFormat.format(startTime));
            Lock = false;
            subscribe();
        }else {
            startTime = System.currentTimeMillis();
            Log.e("시작시간", "" + dateFormat.format(startTime) + " " + timeFormat.format(startTime));
            array.clear();
            subscribe();
        }
    }


    public void healthPause(){
        mHandler.removeMessages(0);
        EndTime = System.currentTimeMillis();
        Log.e("일시정지시간 : ",""+dateFormat.format(EndTime) + " " + timeFormat.format(EndTime));
        if (mApiClient.isConnected()) {
            cancelSubscription();
            unregisterFitnessDataListener();
            stepCount += Integer.parseInt(array.get(array.size() - 1)) - Integer.parseInt(array.get(0));
            array.clear();
            new HealthTask().execute();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);

    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {


        SensorRequest request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(3, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e("GoogleFit", "SensorApi successfully added");
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(Exercise.this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }

    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {

        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            array.add(value.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    step.setText((String.valueOf(stepCount+ Integer.parseInt(value.toString())-(Integer.parseInt(array.get(0))))));
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("GoogleFit", "RESULT_CANCELED");
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    private void unregisterFitnessDataListener() {

        Fitness.SensorsApi.remove(
                mApiClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e("register", "Listener was removed!");

                        } else {
                            Log.e("register", "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }
    public void subscribe() {

        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        // [START subscribe_to_datatype]
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                            } else {
                            }
                        } else {
                        }
                    }
                });
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_DISTANCE_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                            } else {
                            }
                        } else {
                        }
                    }
                });
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                            } else {
                            }
                        } else {
                        }
                    }
                });

    }
    private void cancelSubscription() {
        Fitness.RecordingApi.listSubscriptions(mApiClient)
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(@NonNull ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            final String dataTypeStr = dt.toString();
                            Fitness.RecordingApi.unsubscribe(mApiClient, dt)
                                    .setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            if (status.isSuccess()) {

                                            } else {
                                                // Subscription not removed
                                            }
                                        }
                                    });
                        }
                    }

                });


        // Invoke the Recording API to unsubscribe from the data type and specify a callback that
        // will check the result.
        // [START unsubscribe_from_datatype]

        // [END unsubscribe_from_datatype]
    }
    private void displayLastWeeksData(long StartTime, long endTime) {

        DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(EndTime));


        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(StartTime ,endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }


    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().toString());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();


        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                if(field.getName().equals(Field.FIELD_STEPS.getName())){
                    steps+=dp.getValue(field).asInt();
                    Log.e("History",""+ dp.getValue(field));
                    Log.e("Step",""+steps);
                }else if(field.getName().equals(Field.FIELD_CALORIES.getName())){
                    cal += Integer.parseInt(String.format("%.0f",dp.getValue(field).asFloat()));
                    Log.e("History", String.format("%.0f",dp.getValue(field).asFloat()));
                    Log.e("Calories",""+cal);
                }else if(field.getName().equals(Field.FIELD_DISTANCE.getName())){
                    dis+= Integer.parseInt(String.format("%.0f",dp.getValue(field).asFloat()));
                    Log.e("History", String.format("%.0f",dp.getValue(field).asFloat()));
                    Log.e("Distance",""+dis);
                }


            }
        }
    }
    private class HealthTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData(startTime,EndTime);
            return null;
        }
    }

    private class HealthFinishTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            displayLastWeeksData(startTime, EndTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            makeResult();
            Toast.makeText(Exercise.this, stepCount+":"+steps+":cal:"+cal, Toast.LENGTH_SHORT).show();
            popResult(getResult());

            healthreset();

        }
    }

    public void healthreset(){
        steps = 0; cal = 0;  dis = 0;
        mainTime = 0;
        strTime="";
        Lock = true;
        unregisterFitnessDataListener();

        timer.setText("00:00:00");
    }

    public void makeResult(){
        String day = format.format(ex_today.getTime());
        String location = "noloc";//getLocation(); < = 나중에 util의 클래스중 하나에서 제공하는걸로
        String ex_pic = "basic"; // getPic(); <== 역시 나중에

        ExerciseItem result
                = new ExerciseItem(null,ex_pic, day, startTime, EndTime, location, steps, cal, dis, strTime);
        this.result = result;
    }

    ExerciseItem getResult(){
        return this.result;
    }

    public void popResult(ExerciseItem result){
        TextView day;
        TextView steps, cal, dis;
        TextView extime;
        Context context = this;

        View health_popup_result = getLayoutInflater().inflate(R.layout.health_popup_rersult, null);
        View v = health_popup_result;

        day = (TextView)v.findViewById(R.id.exResult_day);

        extime = (TextView)v.findViewById(R.id.exResult_extime);

        steps = (TextView)v.findViewById(R.id.exResult_steps);
        cal = (TextView)v.findViewById(R.id.exResult_calories);
        dis = (TextView)v.findViewById(R.id.exResult_distance);

        //save = (Button)v.findViewById(R.id.exResult_save);
        //cancel = (Button)v.findViewById(R.id.exResult_cancel);


        Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();

        extime.setText(result.getEx_time());

        day.setText(result.getEx_day());

        steps.setText(result.getStep()+"steps");
        cal.setText(result.getCalories()+"cal");
        dis.setText(result.getDistance()+"m");



        Log.e("start",""+dateFormat.format(result.getEx_start()) + " " + timeFormat.format(result.getEx_start()));
        Log.e("end",""+dateFormat.format(result.getEx_end()) + " " + timeFormat.format(result.getEx_end()));

        AlertDialog.Builder schDialog = new AlertDialog.Builder(context);

        final ExerciseItem data = getResult();

        schDialog.setView(health_popup_result)
                .setPositiveButton("저장", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = Util.user.insertExData(data);
                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null);
        /*Dialog dialog = schDialog.create();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;*/

        schDialog.show();
    }


}
