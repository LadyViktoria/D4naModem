package d4namodem.d4namodem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import d4namodem.d4namodem.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver extends WakefulBroadcastReceiver {
    private static Logger log = LoggerFactory.getLogger(Receiver.class);

    @Override
	public void onReceive(Context context, Intent intent) {
        log.debug("onReceive "+intent);
        startWakefulService(context, new Intent(context, Service.class)
                .setAction(intent.getAction())
                .putExtras(intent));
    }

}
