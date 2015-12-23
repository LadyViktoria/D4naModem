package d4namodem.d4namodem.services;

import android.content.Intent;
import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import d4namodem.d4namodem.DanaConnection;
import d4namodem.d4namodem.MainApp;
import d4namodem.d4namodem.Receiver;
import d4namodem.d4namodem.ServiceConnection;
import d4namodem.d4namodem.calc.IobCalc;
import d4namodem.d4namodem.event.StatusEvent;

public class Service extends android.app.IntentService {
    private static Logger log = LoggerFactory.getLogger(Service.class);

    public static final String ACTION_NEW_DATA = "danaR.action.BG_DATA";

    public static final DecimalFormat numberFormat = new DecimalFormat("0.00");
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    boolean weRequestStartOfConnection = false;

//    DetermineBasalAdapterJS determineBasalAdapterJS;

    public Service() {
        super("Service");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;

        try {
            Bundle bundle = intent.getExtras();
            Date time =  new Date(bundle.getLong("time"));
            int glucoseValue = bundle.getInt("value");
            int delta = bundle.getInt("delta");
            double deltaAvg30min = bundle.getDouble("deltaAvg30min");
            double deltaAvg15min = bundle.getDouble("deltaAvg15min");
            double avg30min = bundle.getDouble("avg30min");
            double avg15min = bundle.getDouble("avg15min");

            String msgReceived = "time:" + dateFormat.format(time)
                    + " bg " + glucoseValue
                    + " dlta: " + delta
                    + " dltaAvg30m:" + numberFormat.format(deltaAvg30min)
                    + " dltaAvg15m:" + numberFormat.format(deltaAvg15min)
                    + " avg30m:" + numberFormat.format(avg30min)
                    + " avg15m:" + numberFormat.format(avg15min);
            log.debug("onHandleIntent "+msgReceived);




            StatusEvent statusEvent = StatusEvent.getInstance();
            DanaConnection danaConnection = getDanaConnection();


            double percent = 100 ;






            if((new Date().getTime() -  statusEvent.timeLastSync.getTime())>60*60_000) {
                log.debug("Requesting status ...");

                danaConnection.connectIfNotConnected("ServiceBG 1hour");

            }


        } catch (Throwable x){
            log.error(x.getMessage(),x);

        } finally {
            Receiver.completeWakefulIntent(intent);
        }

    }



    private void broadcastIob(IobCalc.Iob bolusIobOpenAPS, IobCalc.Iob bolusIob, IobCalc.Iob basalIob) {
        Intent intent = new Intent("danaR.action.IOB_DATA");

        Bundle bundle = new Bundle();

        bundle.putLong("time", new Date().getTime());
        bundle.putDouble("bolusIob", bolusIob.iobContrib);
        bundle.putDouble("bolusIobAPS", bolusIobOpenAPS.iobContrib);
        bundle.putDouble("bolusIobActivity", bolusIobOpenAPS.activityContrib);
        bundle.putDouble("basalIob", basalIob.iobContrib);
        bundle.putDouble("basalIobActivity", basalIob.activityContrib);

        intent.putExtras(bundle);
        MainApp.instance().getApplicationContext().sendBroadcast(intent);
    }



    private DanaConnection getDanaConnection() throws InterruptedException {
        DanaConnection danaConnection = MainApp.getDanaConnection();
        if(danaConnection==null) {
            weRequestStartOfConnection = true;
            getApplicationContext().startService(new Intent(getApplicationContext(), ServiceConnection.class));
            int counter = 0;
            do{
                danaConnection = MainApp.getDanaConnection();
                Thread.sleep(100);
                counter++;
            }while(danaConnection == null && counter < 10);
            if(danaConnection == null) {
                log.error("danaConnection == null");
                weRequestStartOfConnection = false;
            }
        }
        return danaConnection;
    }
}
