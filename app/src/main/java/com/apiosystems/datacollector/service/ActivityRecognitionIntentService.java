package com.apiosystems.datacollector.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by jaredsheehan on 12/15/14.
 */
public class ActivityRecognitionIntentService extends IntentService {

    private static String LOG_TAG = ActivityRecognitionIntentService.class.getSimpleName();
    public static String ACTIVITY_NAME_KEY = "ACTIVITY_NAME_KEY";
    public static String ACTIVITY_CONFIDENCE_KEY = "ACTIVITY_CONFIDENCE_KEY";

    public ActivityRecognitionIntentService(){
        super(ActivityRecognitionIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();

            String confidencestr = String.valueOf(confidence);//getConfidenceRange(confidence);
            /*
             * Get an integer describing the type of activity
             */
            int activityType = mostProbableActivity.getType();
            final String activityName = String.valueOf(activityType);//getNameFromType(activityType);

            /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */
            final String log = "ActivityRecognitionResult has result: activityName: " + activityName + " confidence: " + confidencestr;
            //Log.d(LOG_TAG, log);
            broadcastNewActivityRecognized(activityName, confidencestr);
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
            Log.d(LOG_TAG, "ActivityRecognitionResult has no result");
        }
    }

    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                String inVehicle = "InVehicle";
                return inVehicle;
            case DetectedActivity.ON_BICYCLE:
                return "OnBicycle";
            case DetectedActivity.ON_FOOT:
                String onFoot = "OnFoot";
                return onFoot;
            case DetectedActivity.RUNNING:
                String running = "Running";
                return running;
            case DetectedActivity.STILL:
                return "Stationary";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.WALKING:
                String walking = "Walking";
                return walking;
        }
        return "Unknown";
    }

    private String getConfidenceRange(int i){
        String range;
        i = Math.abs(i);
        if(i <= 50){
            range = "LowConfidence";
        }else if(i <= 75){
            range = "MedConfidence";
        }else if(i <= 100){
            range = "HighConfidence";
        }else{
            range = "-";
        }
         return range;
    }

    private void broadcastNewActivityRecognized(String activityName, String confidence){
        Intent serviceLauncher = new Intent(this, ApioActivityRecognitionService.class);
        serviceLauncher.putExtra(ACTIVITY_NAME_KEY, activityName);
        serviceLauncher.putExtra(ACTIVITY_CONFIDENCE_KEY, confidence);
        startService(serviceLauncher);
    }
}