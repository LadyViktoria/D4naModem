package d4namodem.d4namodem.comm;

import android.content.Intent;
import android.os.Bundle;
import d4namodem.d4namodem.MainApp;
import d4namodem.d4namodem.event.StatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MsgDummy extends DanaRMessage {
    private static Logger log = LoggerFactory.getLogger(MsgDummy.class);

    public MsgDummy() {
        super("CMD_DUMMY");
    }

    public MsgDummy(String cmdName) {
        super(cmdName);
    }

    public void handleMessage(byte[] bytes) {
    }
}
